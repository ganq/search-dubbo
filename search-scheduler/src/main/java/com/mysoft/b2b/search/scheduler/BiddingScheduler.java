package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.commons.scheduler.MysoftJob;
import com.mysoft.b2b.search.scheduler.helper.AnnouncementHelper;
import com.mysoft.b2b.search.scheduler.helper.SchedulerThreadData;
import com.mysoft.b2b.search.scheduler.helper.SearchHelper;
import com.mysoft.b2b.search.spi.SearchModel;
import com.mysoft.b2b.search.spi.bidding.*;
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

/**
 * 招标预告定时任务
 * 
 * @author ganq
 * 
 */
public class BiddingScheduler extends MysoftJob implements SchedulerThreadData{
	private static final Logger logger = Logger.getLogger(BiddingScheduler.class);

	@Autowired
	private AnnouncementHelper announcementHelper;
	
	@Autowired
	private BiddingService biddingService;
	
	@Autowired
	private SearchHelper searchHelper;
	
	// 是否首次导入
	private boolean isFirstImport = false;
	
	private SolrServer announcementsServer = new HttpSolrServer(PropertiesUtil.getKey(PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_ADDRESS));
	
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
		List<BiddingSearchModel> dataList = getBiddingInfo();
		if (CollectionUtils.isEmpty(dataList)) {
			logger.info(getJobName() + "此次构建没有数据更新！");
			return; 
		}
				
		List<Map<String, Object>> solrIndexList = builidIndexList(dataList);
		if (CollectionUtils.isEmpty(solrIndexList)) {
			logger.info(getJobName() + "此次构建没有数据更新！");
			return; 
		}

		buildAnnouncementsToSolr(solrIndexList);
		
		Set<String> ids = new HashSet<String>();
		if (!isFirstImport) {
			for (Map<String,Object> map :solrIndexList) {
				ids.add(PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_PRIMARY_KEY + ":" + ObjectUtils.toString(map.get(PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_PRIMARY_KEY)));
			}
		}
		int rowCount = announcementHelper.updateOperationCategory(ids, DataType.BID, announcementsServer,solrIndexList,PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_PRIMARY_KEY);
		logger.info(getJobName() +"运营分类更新成功" + rowCount + "条");
	}
	
	/**
	 * 获取招标预告数据
	 * @throws IOException 
	 * @throws SolrServerException 
	 * 
	 */
	private List<BiddingSearchModel> getBiddingInfo() throws SolrServerException, IOException {
		String date = PropertiesUtil.getKey(PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_LAST_MODIFY_DATE);
		
		Set<String> biddingIds;
  
		if ("".equals(date)) {
			isFirstImport = true;
			biddingIds = biddingService.getAllId();
		} else {
			isFirstImport = false;
			biddingIds = biddingService.getIdsByLastModifyTime(NumberUtils.toLong(date));
			if (!CollectionUtils.isEmpty(biddingIds)) {
				logger.info(getJobName() + "本次增量更新的Id为：" + StringUtils.join(biddingIds,","));	
			}
						
		}
		
		// 根据lastmodifydate 判断是否要增量更新
		lastModifyTime = biddingService.getNewestLastModifyTime();
		PropertiesUtil.setKey(PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_LAST_MODIFY_DATE,ObjectUtils.toString(lastModifyTime));
		
		if (biddingIds == null) {
			logger.info(getJobName() + "通过spi接口获取id数据异常");
			return null;
		}

        List<BiddingSearchModel> list = searchHelper.getDataByThread(biddingIds, 10, this,BiddingSearchModel.class);
		
		logger.info(getJobName() + "本次待插入索引数据" + list.size() + "条");
		return list;

	}
	
	@Override
	public SearchModel getSingleDataObj(String id) {
		BiddingSearchModel biddingSearchModel = biddingService.getSearchModelById(id);
		if (biddingSearchModel == null) {
			logger.info(getJobName() + "Id 为“"+id+"”通过spi接口获取数据异常，将不导入索引库！！！");
			return null;
		}
		return biddingSearchModel;
	}

	/**
	 * 构建solr导入需要的数据集合
	 */
	private List<Map<String, Object>> builidIndexList(List<BiddingSearchModel> dataList) {
		List<Map<String, Object>> indexList = new ArrayList<Map<String,Object>>();
		for (BiddingSearchModel biddingSearchModel : dataList) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			// 招标公告id
			String biddingId = biddingSearchModel.getId();
			map.put(PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_PRIMARY_KEY, biddingId);
			Bidding bidding = biddingSearchModel.getBidding();
			
			if (!(bidding != null && bidding.getBiddingBasic() != null)) {
				logger.warn(getJobName() + "Id为" + biddingId + "的取不到数据，此条数据将不会导入");
				continue;
			}
			
			// 招标基本信息
			BiddingBasic biddingBasic = bidding.getBiddingBasic();
			if (StringUtils.defaultString(biddingBasic.getTitle()).startsWith("test_")) {
				logger.warn(getJobName() + "Id为" + biddingId + "为测试数据，此条数据将不会导入");
				continue;
			}
			// 基础分类信息
			if (CollectionUtils.isEmpty(biddingBasic.getCategoryCodeSet())) {
				logger.warn(getJobName() + "Id为" + biddingId + "没有基础分类，此条数据将不会导入");
				continue;
			}
			map.put("basicCategoryCode", biddingBasic.getCategoryCodeSet());
			map.put("basicCategoryCode1", biddingBasic.getFirstCategoryCodeSet());
			map.put("basicCategoryCode2", biddingBasic.getSecondCategoryCodeSet());
			map.put("basicCategoryCode3", biddingBasic.getThirdCategoryCodeSet());
			map.put("basicCategoryCode4", biddingBasic.getFourthCategoryCodeSet());
			
			map.put("basicCategoryName", biddingBasic.getCategoryNameSet());
			map.put("basicCategoryName1", biddingBasic.getFirstCategoryNameSet());
			map.put("basicCategoryName2", biddingBasic.getSecondCategoryNameSet());
			map.put("basicCategoryName3", biddingBasic.getThirdCategoryNameSet());
			map.put("basicCategoryName4", biddingBasic.getFourthCategoryNameSet());
						
			map.put("title", CommonUtil.escapeXml(biddingBasic.getTitle()));
			map.put("shortTitle", biddingBasic.getShortTitle());
			map.put("titlePinyin", PinyinUtil.getPinyin(biddingBasic.getTitle()));
			map.put("registerEndDate",CommonUtil.convertToBeijingTime(biddingBasic.getRegisterEndDate()));
			map.put("auditTime",CommonUtil.convertToBeijingTime(biddingBasic.getAuditTime()));
			map.put("deposit", NumberUtils.toDouble(ObjectUtils.toString(biddingBasic.getDeposit())));
			map.put("biddingRange", CommonUtil.escapeXml(biddingBasic.getBiddingRange()));
			map.put("biddingCompany", StringUtils.join(biddingBasic.getBiddingCompanySet(),"、"));
			map.put("publishTime", CommonUtil.convertToBeijingTime(biddingBasic.getPublishTime()));
			map.put("state", biddingBasic.getState());
			// 状态排序字段
			int stateSort;
			if (biddingBasic.getState() == 2) {
				stateSort = 0;
			}else if(biddingBasic.getState() == 5){
				stateSort = 1;
			}else{
				stateSort = 2;
			}
			map.put("stateSort", stateSort);


			// 招标开发商
			BiddingDeveloper biddingDeveloper = bidding.getBiddingDeveloper();
			if (biddingDeveloper != null) {
				map.put("developerId", biddingDeveloper.getId());
				map.put("developerName",biddingDeveloper.getName());
				map.put("developerShortName",biddingDeveloper.getShortName());
				map.put("developerLogo", biddingDeveloper.getCompanyLogo());
			}
			
			// 招标关联项目
			BiddingProject biddingProject = bidding.getBiddingProject();
			if (biddingProject != null) {
				map.put("projectProvinceId", biddingProject.getProvinceCode());
				map.put("projectProvince", biddingProject.getProvinceName());
				map.put("projectCityId",biddingProject.getCityCode());
				map.put("projectCity",biddingProject.getCityName());
				map.put("projectName", biddingProject.getName());
				map.put("projectImage", biddingProject.getImage());
				map.put("projectArea", biddingProject.getArea());
				map.put("projectTotArea", biddingProject.getTotArea());
				map.put("projectType", biddingProject.getType());
				
			}
			
			// 招标统计
			BiddingStatics biddingStatics = biddingSearchModel.getBiddingStatics();
			if (biddingStatics != null) {
				map.put("registerCount", biddingStatics.getRegisterCount());
			}
			
			// 报名条件
			BiddingRegisterCondition biddingRegisterCondition = bidding.getBiddingRegisterCondition();
			if (biddingRegisterCondition == null || biddingRegisterCondition.getItems() == null) {
				logger.warn(getJobName() + "Id为" + biddingId + "的取不到报名条件数据，此条数据将不会导入");
				continue;
			}			
			Map<String, Object> biddingRCItems = biddingRegisterCondition.getItems();
			for (String key : biddingRCItems.keySet()) {
				map.put(key, biddingRCItems.get(key));
			}
			
			indexList.add(map);
		}
		return indexList;
	}
		

	/**
	 * 创建招标预告索引到Solr
	 * 
	 * @throws IOException
	 * @throws SolrServerException
	 * 
	 */
	private void buildAnnouncementsToSolr(List<Map<String, Object>> solrIndexList) throws SolrServerException,IOException {
		
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
			announcementsServer.deleteByQuery("*:*");
		}
		announcementsServer.add(docs);
		announcementsServer.commit();

		logger.info("成功更新"+getJobName() + "索引" + solrIndexList.size() + "条数据！！！");
	}

	
	/**
	 * 删除失效索引
	 */
	private void deleteInvalidIndex() throws SolrServerException, IOException{
		if (lastModifyTime == null) {
			logger.error("lastModifyTime获取异常.....将不执行删除失效索引方法");
			return;
		}
		Set<String> invalidIds = biddingService.getInvalidIdsByLastModifyTime(lastModifyTime);
		if (CollectionUtils.isEmpty(invalidIds)) {
			return;
		}
		announcementsServer.deleteById(new ArrayList<String>(invalidIds));
		announcementsServer.commit();
		logger.info(getJobName() +"删除不可用的索引" + invalidIds.size() + "条,他们的id为：" + StringUtils.join(invalidIds,","));
	}
}