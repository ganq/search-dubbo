package com.mysoft.b2b.search.scheduler;

import com.alibaba.fastjson.JSON;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.search.scheduler.helper.SearchHelper;
import com.mysoft.b2b.search.spi.supplier.*;
import com.mysoft.b2b.search.utils.CommonUtil;
import com.mysoft.b2b.search.utils.PinyinUtil;
import com.mysoft.b2b.search.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class SupplierScheduler extends BaseScheduler<SupplierSearchModel>{
	private static final Logger logger = Logger.getLogger(SupplierScheduler.class);

	@Autowired
	private SupplierService supplierService;

    @Autowired
    private SearchHelper searchHelper;

    @Override
    protected void init() {
        setDateKey(PropertiesUtil.SOLR_CORE_SUPPLIER_LAST_MODIFY_DATE);
        setSearchHelper(searchHelper);
        setSearchSPIService(supplierService);
        setSolrServer(new HttpSolrServer(PropertiesUtil.getKey(PropertiesUtil.solrProp,PropertiesUtil.SOLR_CORE_SUPPLIER_ADDRESS)));
    }

    /**
	 * 构建Solr导入需要的数据集合
	 */
    @Override
	List<SolrInputDocument> buildIndexList(List<SupplierSearchModel> dataList) {
		List<SolrInputDocument> resultList = new ArrayList<SolrInputDocument>(dataList.size());
        SolrInputDocument solrInputDoc;
		for (SupplierSearchModel supplierSearchModel : dataList) {
            solrInputDoc = new SolrInputDocument();
		
			String supplierId = supplierSearchModel.getId();

			solrInputDoc.setField(PropertiesUtil.SOLR_CORE_SUPPLIER_PRIMARY_KEY, supplierId);
			
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
				logger.error(getJobName() + "Id为" + supplierId + "没有基础分类，此条数据将不会导入");
				continue;
			}
			solrInputDoc.setField("basicCategoryCode", supplierBasic.getCategoryCodeSet());
			solrInputDoc.setField("basicCategoryCode1", supplierBasic.getFirstCategoryCodeSet());
			solrInputDoc.setField("basicCategoryCode2", supplierBasic.getSecondCategoryCodeSet());
			solrInputDoc.setField("basicCategoryCode3", supplierBasic.getThirdCategoryCodeSet());
			solrInputDoc.setField("basicCategoryCode4", supplierBasic.getFourthCategoryCodeSet());
			
			solrInputDoc.setField("basicCategoryName", supplierBasic.getCategoryNameSet());
			solrInputDoc.setField("basicCategoryName1", supplierBasic.getFirstCategoryNameSet());
			solrInputDoc.setField("basicCategoryName2", supplierBasic.getSecondCategoryNameSet());
			solrInputDoc.setField("basicCategoryName3", supplierBasic.getThirdCategoryNameSet());
			solrInputDoc.setField("basicCategoryName4", supplierBasic.getFourthCategoryNameSet());

            Map<String,Object> operationCodeMap = searchHelper.getOperationCategoryCodes(supplierBasic.getCategoryCodeSet(),DataType.SUPPLIER);
            if (operationCodeMap.isEmpty()){
                logger.error(getJobName() + "Id为" + supplierId + "没有运营分类，此条数据将不会导入");
                continue;
            }
            for (Map.Entry<String,Object> entry : operationCodeMap.entrySet()){
                solrInputDoc.setField(entry.getKey(), entry.getValue());
            }

			
			// 供应商基本信息
			solrInputDoc.setField("companyName",CommonUtil.escapeXml(supplierBasic.getName()));
			solrInputDoc.setField("shortName", CommonUtil.escapeXml(supplierBasic.getShortName()));
			solrInputDoc.setField("companyNamePinyin",PinyinUtil.getPinyin(supplierBasic.getName()));
			solrInputDoc.setField("establishYear", supplierBasic.getEstablishYear());
			solrInputDoc.setField("currency", supplierBasic.getCurrency());
			solrInputDoc.setField("regCapital", supplierBasic.getRegCapital());
			solrInputDoc.setField("regCapitalExchange", supplierBasic.getRegCapitalExchange());
			solrInputDoc.setField("authTag", supplierBasic.isAuthTag());
			solrInputDoc.setField("projectLocationId", supplierBasic.getServiceRegionCodeSet());
			solrInputDoc.setField("projectArea", supplierBasic.getServiceAreaCodeSet());
			solrInputDoc.setField("projectLocation", supplierBasic.getServiceRegionNameSet());
			solrInputDoc.setField("supplierType", supplierBasic.getType());
			solrInputDoc.setField("businessScope", CommonUtil.escapeXml(supplierBasic.getBusinessScope()));
			solrInputDoc.setField("defaultAward", CommonUtil.escapeXml(supplierBasic.getDefaultAward()));
            solrInputDoc.setField("awardCount",supplierBasic.getAwardCount());
			solrInputDoc.setField("legalName", supplierBasic.getLegalName());
			solrInputDoc.setField("regProvinceName", supplierBasic.getProvinceName());
			solrInputDoc.setField("regCityName", supplierBasic.getCityName());
			solrInputDoc.setField("regAddress", supplierBasic.getAddress());
            solrInputDoc.setField("registerLocation",Arrays.asList(supplierBasic.getProvinceCode(),supplierBasic.getCityCode()));

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
						solrInputDoc.setField("qualification_" + qualifyCode,supplierQualifyLevel.getPriority());
						solrInputDoc.setField("qualificationCode_Level", qualifyCode + "_" + supplierQualifyLevel.getQualifyLevelCode());
						qualificationCodeList.add(qualifyCode);
						qualificationNameList.add(supplierQualify.getQualifyName() + supplierQualifyLevel.getQualifyLevelName());
					}
				}
				solrInputDoc.setField("qualificationCode", qualificationCodeList);
				solrInputDoc.setField("qualificationLevelName", qualificationNameList);
				
			}

            // 将用户对象序列化成json串存入
            if (!CollectionUtils.isEmpty(supplier.getSupplierUsers())) {
                solrInputDoc.setField("userListJSON",JSON.toJSONString(supplier.getSupplierUsers()));
            }

			// 项目案例列表
			if (!CollectionUtils.isEmpty(supplier.getSupplierCases())) {
				Set<String> projectNameList = new HashSet<String>();
				for (SupplierCase supplierCase : supplier.getSupplierCases()) {
					projectNameList.add(supplierCase.getName());
				}
				solrInputDoc.setField("projectName", projectNameList);
				solrInputDoc.setField("projectCount", projectNameList.size());
				
			}
			
			// 产品名称
			if (!CollectionUtils.isEmpty(supplier.getSupplierProducts())) {
				Set<String> productNameList = new HashSet<String>();
				for (SupplierProduct supplierProduct : supplier.getSupplierProducts()) {
					productNameList.add(supplierProduct.getName());
				}
				solrInputDoc.setField("productName", productNameList);
				solrInputDoc.setField("productCount", productNameList.size());
				
			}
			
			// 供应商统计信息
			SupplierStatics supplierStatics = supplierSearchModel.getSupplierStatics();
			if (supplierStatics != null) {
				// 勋章
				solrInputDoc.setField("medalLevel", supplierStatics.getMedalLevel());
				//已中标次数
				solrInputDoc.setField("awardBidCount", supplierStatics.getAwardBidCount());
				// 资料完整度
				solrInputDoc.setField("dataCount", supplierStatics.getDataCount());
				//过去7天登录次数
				solrInputDoc.setField("loginCount", supplierStatics.getLoginCount());
				// 入库次数
				solrInputDoc.setField("inStorageCount", supplierStatics.getInStorageCount());
				//被关注次数
				solrInputDoc.setField("followCount", supplierStatics.getFollowCount());
			}
			
			// 排序分值
			solrInputDoc.setField("sortScore", searchHelper.getSupplierSortScore(supplier.getSupplierScoreItem()));
			
			if (supplier.getSupplierProducts()!= null && !supplier.getSupplierProducts().isEmpty()) {
				Set<String> productNameSet = new HashSet<String>();
				for (SupplierProduct supplierProduct : supplier.getSupplierProducts()) {
					productNameSet.add(supplierProduct.getName());
				}
				solrInputDoc.setField("productName", productNameSet);
			}
			resultList.add(solrInputDoc);
		}
		return resultList;
		
	}

}