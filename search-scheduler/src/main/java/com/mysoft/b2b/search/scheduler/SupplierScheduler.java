package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.commons.scheduler.MysoftJob;
import com.mysoft.b2b.search.scheduler.helper.SchedulerThreadData;
import com.mysoft.b2b.search.scheduler.helper.SearchHelper;
import com.mysoft.b2b.search.scheduler.helper.SupplierHelper;
import com.mysoft.b2b.search.spi.SearchModel;
import com.mysoft.b2b.search.spi.supplier.*;
import com.mysoft.b2b.search.utils.CommonUtil;
import com.mysoft.b2b.search.utils.PinyinUtil;
import com.mysoft.b2b.search.utils.PropertiesUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

public class SupplierScheduler extends MysoftJob implements SchedulerThreadData{
	private static final Logger logger = Logger.getLogger(SupplierScheduler.class);

	@Autowired
	private SearchHelper searchHelper;

	@Autowired
	private SupplierHelper supplierHelper;
	
	@Autowired
	private SupplierService supplierService;
	
	
	// 是否首次导入
	private boolean isFirstImport = false;
	
	private SolrServer supplierServer = new HttpSolrServer(PropertiesUtil.getKey(PropertiesUtil.SOLR_CORE_SUPPLIER_ADDRESS));

	private Long lastModifyTime;
	/**
	 * 定时调度的方法
	 */
	@Override
	public void run() {
		try {
			// 构建索引
			buildIndexes();
			// 删除失效的索引
			deleteInvalidIndex();
        } catch (SolrServerException e) {
            logger.info(getJobName() + "构建SolrServer异常:", e);
        } catch (IOException e) {
            logger.info(getJobName() + "构建IO异常:", e);
        } catch (Exception e){
            logger.info(getJobName() + "构建异常:", e);
        }
	}

	/**
	 * 构建索引
	 * 
	 * @throws SolrServerException
	 * @throws IOException
	 */
	private void buildIndexes() throws SolrServerException, IOException {
		List<SupplierSearchModel> dataList = getSupplies();
		if (CollectionUtils.isEmpty(dataList)) {
			logger.info(getJobName() + "此次构建没有数据更新！");
			return;
		}
				
		List<Map<String, Object>> indexList = buildIndexList(dataList);
		if (CollectionUtils.isEmpty(indexList)) {
			logger.info(getJobName() + "此次构建没有数据更新！");
			return; 
		}
		buildSuppliesToSolr(indexList);
		
		
		Set<String> ids = new HashSet<String>();
		if (!isFirstImport) {
			for (Map<String,Object> map :indexList) {
				ids.add(PropertiesUtil.SOLR_CORE_SUPPLIER_PRIMARY_KEY + ":" + ObjectUtils.toString(map.get(PropertiesUtil.SOLR_CORE_SUPPLIER_PRIMARY_KEY)));
			}
		}
		int rowCount = searchHelper.updateOperationCategory(ids, DataType.SUPPLIER, supplierServer,indexList,PropertiesUtil.SOLR_CORE_SUPPLIER_PRIMARY_KEY);
		logger.info(getJobName() +"运营分类更新成功" + rowCount + "条");
	}

	/**
	 * 获取供应商信息
	 * 
	 */
	private List<SupplierSearchModel> getSupplies() {
		
		String date = PropertiesUtil.getKey(PropertiesUtil.SOLR_CORE_SUPPLIER_LAST_MODIFY_DATE);

		Set<String> supplierIds;
		if ("".equals(date)) {
			isFirstImport = true;
			supplierIds = supplierService.getAllId();
		} else {
			isFirstImport = false;
			supplierIds = supplierService.getIdsByLastModifyTime(NumberUtils.toLong(date));
			if (!CollectionUtils.isEmpty(supplierIds)) {
				logger.info(getJobName() + "本次增量更新的Id为：" + StringUtils.join(supplierIds,","));
			}
		}
		
		lastModifyTime = supplierService.getNewestLastModifyTime();
		PropertiesUtil.setKey(PropertiesUtil.SOLR_CORE_SUPPLIER_LAST_MODIFY_DATE, ObjectUtils.toString(lastModifyTime));
		
		if (supplierIds == null) {
			logger.info(getJobName() + "通过spi接口获取id数据异常");
			return null;
		}
        logger.info(getJobName() + "id数量为：" + supplierIds.size());

        /*int i = 0;
        for (Iterator<String> it= supplierIds.iterator();it.hasNext();) {
            String id = it.next();
            if (i >= 100) {
                it.remove();
            }
            i++;
        }*/

        List<SupplierSearchModel> list = searchHelper.getDataByThread(supplierIds, 10, this,SupplierSearchModel.class);
		
		logger.info(getJobName() + "本次待插入索引数据" + list.size() + "条");
		return list;

	}	
	
	@Override
	public SearchModel getSingleDataObj(String id) {
		SupplierSearchModel supplierSearchModel = supplierService.getSearchModelById(id);
		if (supplierSearchModel == null) {
			logger.info(getJobName() + "Id 为“"+id+"”通过spi接口获取数据异常，将不导入索引库！！！");
			return null;
		}
		return supplierSearchModel;
	}
	
	
	/**
	 * 构建Solr导入需要的数据集合
	 */
	private List<Map<String, Object>> buildIndexList(List<SupplierSearchModel> dataList) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		for (SupplierSearchModel supplierSearchModel : dataList) {
			
		
			String supplierId = supplierSearchModel.getId();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(PropertiesUtil.SOLR_CORE_SUPPLIER_PRIMARY_KEY, supplierId);
			
			// 供应商id
			Supplier supplier = supplierSearchModel.getSupplier();
			if (!(supplier != null && supplier.getSupplierBasic() != null)) {			
				logger.error(getJobName() + "Id为" + supplierId + "的取不到数据，此条数据将不会导入");
				continue;
			}
			SupplierBasic supplierBasic = supplier.getSupplierBasic();
			
			if (StringUtils.defaultString(supplierBasic.getName()).startsWith("test_")) {			
				logger.error(getJobName() + "Id为" + supplierId + "的为测试数据，此条数据将不会导入");
				continue;
			}
			// 基础分类信息
			if (CollectionUtils.isEmpty(supplierBasic.getCategoryCodeSet())) {
				logger.warn(getJobName() + "Id为" + supplierId + "没有基础分类，此条数据将不会导入");
				continue;
			}
			map.put("basicCategoryCode", supplierBasic.getCategoryCodeSet());
			map.put("basicCategoryCode1", supplierBasic.getFirstCategoryCodeSet());
			map.put("basicCategoryCode2", supplierBasic.getSecondCategoryCodeSet());
			map.put("basicCategoryCode3", supplierBasic.getThirdCategoryCodeSet());
			map.put("basicCategoryCode4", supplierBasic.getFourthCategoryCodeSet());
			
			map.put("basicCategoryName", supplierBasic.getCategoryNameSet());
			map.put("basicCategoryName1", supplierBasic.getFirstCategoryNameSet());
			map.put("basicCategoryName2", supplierBasic.getSecondCategoryNameSet());
			map.put("basicCategoryName3", supplierBasic.getThirdCategoryNameSet());
			map.put("basicCategoryName4", supplierBasic.getFourthCategoryNameSet());
			
			// 供应商基本信息
			map.put("companyName",CommonUtil.escapeXml(supplierBasic.getName()));
			map.put("shortName", CommonUtil.escapeXml(supplierBasic.getShortName()));
			map.put("companyNamePinyin",PinyinUtil.getPinyin(supplierBasic.getName()));
			map.put("establishYear", supplierBasic.getEstablishYear());
			map.put("currency", supplierBasic.getCurrency());
			map.put("regCapital", supplierBasic.getRegCapital());
			map.put("regCapitalExchange", supplierBasic.getRegCapitalExchange());
			map.put("authTag", supplierBasic.isAuthTag());
			map.put("projectLocationId", supplierBasic.getServiceRegionCodeSet());
			map.put("projectArea", supplierBasic.getServiceAreaCodeSet());
			map.put("projectLocation", supplierBasic.getServiceRegionNameSet());
			map.put("supplierType", supplierBasic.getType());
			map.put("businessScope", CommonUtil.escapeXml(supplierBasic.getBusinessScope()));
			map.put("defaultAward", CommonUtil.escapeXml(supplierBasic.getDefaultAward()));			
			map.put("legalName", supplierBasic.getLegalName());
			map.put("regProvinceName", supplierBasic.getProvinceName());
			map.put("regCityName", supplierBasic.getCityName());
			map.put("regAddress", supplierBasic.getAddress());
			
			// 资质
			if (supplier.getSupplierQualifies() != null && !supplier.getSupplierQualifies().isEmpty()) {
				List<String> qualificationCodeList = new ArrayList<String>();
				List<String> qualificationNameList = new ArrayList<String>();
				for (SupplierQualify supplierQualify : supplier.getSupplierQualifies()) {
					if (supplierQualify == null) {
						logger.error(getJobName() + "id" + supplierId + "通过dubbo接口查询不到资质数据.");
						continue;
					}
					String qualifyCode = supplierQualify.getQualifyCode();
					SupplierQualifyLevel supplierQualifyLevel = supplierQualify.getSupplierQualifyLevel();
					if (supplierQualifyLevel != null) {
						map.put("qualification_" + qualifyCode,supplierQualifyLevel.getPriority());	
						map.put("qualificationCode_Level", qualifyCode + "_" +supplierQualifyLevel.getQualifyLevelCode());
						qualificationCodeList.add(qualifyCode);
						qualificationNameList.add(supplierQualify.getQualifyName() + supplierQualifyLevel.getQualifyLevelName());
					}
				}
				map.put("qualificationCode", qualificationCodeList);
				map.put("qualificationLevelName", qualificationNameList);
				
			}
			 
			// 项目案例列表
			
			if (!CollectionUtils.isEmpty(supplier.getSupplierCases())) {
				Set<String> projectNameList = new HashSet<String>();
				for (SupplierCase supplierCase : supplier.getSupplierCases()) {
					projectNameList.add(supplierCase.getName());
				}
				map.put("projectName", projectNameList);
				map.put("projectCount", projectNameList.size());
				
			}
			
			// 产品名称
			if (!CollectionUtils.isEmpty(supplier.getSupplierProducts())) {
				Set<String> productNameList = new HashSet<String>();
				for (SupplierProduct supplierProduct : supplier.getSupplierProducts()) {
					productNameList.add(supplierProduct.getName());
				}
				map.put("productName", productNameList);
				map.put("productCount", productNameList.size());
				
			}
			
			// 供应商统计信息
			SupplierStatics supplierStatics = supplierSearchModel.getSupplierStatics();
			if (supplierStatics != null) {
				// 勋章
				map.put("medalLevel", supplierStatics.getMedalLevel());
				//已中标次数
				map.put("awardBidCount", supplierStatics.getAwardBidCount());	
				// 资料完整度
				map.put("dataCount", supplierStatics.getDataCount());
				//过去7天登录次数
				map.put("loginCount", supplierStatics.getLoginCount());
				// 入库次数
				map.put("inStorageCount", supplierStatics.getInStorageCount());
				//被关注次数
				map.put("followCount", supplierStatics.getFollowCount());
			}
			
			// 排序分值
			map.put("sortScore", supplierHelper.getSupplierSortScore(supplier.getSupplierScoreItem()));
			
			if (supplier.getSupplierProducts()!= null && !supplier.getSupplierProducts().isEmpty()) {
				Set<String> productNameSet = new HashSet<String>();
				for (SupplierProduct supplierProduct : supplier.getSupplierProducts()) {
					productNameSet.add(supplierProduct.getName());
				}
				map.put("productName", productNameSet);
			}
			resultList.add(map);
		}
		return resultList;
		
	}
	
	/**
	 * 创建供应商索引到Solr
	 * 
	 * @throws IOException
	 * @throws SolrServerException
	 * 
	 */
	private void buildSuppliesToSolr(List<Map<String, Object>> solrIndexList) throws SolrServerException,IOException {

		// 生成文档到solr
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		for (Map<String, Object> map : solrIndexList) {
			SolrInputDocument doc = new SolrInputDocument();
			
			for (String key : map.keySet()) {
				doc.addField(key, map.get(key));
			}

			docs.add(doc);
		}
		if (isFirstImport) {
			supplierServer.deleteByQuery("*:*");
		}
		supplierServer.add(docs);
		supplierServer.commit();

		logger.info("成功更新"+getJobName() + "索引" + getJobName() + solrIndexList.size() + "条数据！！！");
	}

	/**
	 * 删除失效索引
	 * @throws SolrServerException
	 * @throws IOException
	 */
	private void deleteInvalidIndex() throws SolrServerException, IOException{
		if (lastModifyTime == null) {
			logger.error("lastModifyTime获取异常.....将不执行删除失效索引方法");
			return;
		}
		Set<String> invalidIds = supplierService.getInvalidIdsByLastModifyTime(lastModifyTime);
		if (CollectionUtils.isEmpty(invalidIds)) {
			return;
		}
		supplierServer.deleteById(new ArrayList<String>(invalidIds));
		supplierServer.commit();
		logger.info(getJobName() +"删除不可用的索引" + invalidIds.size() + "条,他们的id为：" + StringUtils.join(invalidIds,","));
	}

}