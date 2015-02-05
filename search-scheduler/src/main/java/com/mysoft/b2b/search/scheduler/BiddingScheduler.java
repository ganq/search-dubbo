package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.search.scheduler.helper.SearchHelper;
import com.mysoft.b2b.search.spi.bidding.*;
import com.mysoft.b2b.search.utils.CommonUtil;
import com.mysoft.b2b.search.utils.PinyinUtil;
import com.mysoft.b2b.search.utils.PropertiesUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 招标预告定时任务
 * 
 * @author ganq
 * 
 */
public class BiddingScheduler extends BaseScheduler<BiddingSearchModel>{

	private static final Logger logger = Logger.getLogger(BiddingScheduler.class);

	@Autowired
	private BiddingService biddingService;
	
	@Autowired
	private SearchHelper searchHelper;


    @Override
    void init() {
        setDateKey(PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_LAST_MODIFY_DATE);
        setSearchHelper(searchHelper);
        setSearchSPIService(biddingService);
        setSolrServer(new HttpSolrServer(PropertiesUtil.getKey(PropertiesUtil.solrProp,PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_ADDRESS)));
    }

    /**
	 * 构建solr导入需要的数据集合
	 */
    @Override
	List<SolrInputDocument> buildIndexList(List<BiddingSearchModel> dataList) {
		List<SolrInputDocument> indexList = new ArrayList<SolrInputDocument>(dataList.size());
        SolrInputDocument solrInputDoc;
		for (BiddingSearchModel biddingSearchModel : dataList) {
            solrInputDoc= new SolrInputDocument();
			
			// 招标公告id
			String biddingId = biddingSearchModel.getId();
			solrInputDoc.setField(PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_PRIMARY_KEY, biddingId);
			Bidding bidding = biddingSearchModel.getBidding();
			
			if (!(bidding != null && bidding.getBiddingBasic() != null)) {
				logger.error(getJobName() + "Id为" + biddingId + "的取不到数据，此条数据将不会导入");
				continue;
			}
			
			// 招标基本信息
			BiddingBasic biddingBasic = bidding.getBiddingBasic();
			if (StringUtils.defaultString(biddingBasic.getTitle()).startsWith("test_")) {
				logger.error(getJobName() + "Id为" + biddingId + "为测试数据，此条数据将不会导入");
				continue;
			}
			// 基础分类信息
			if (CollectionUtils.isEmpty(biddingBasic.getCategoryCodeSet())) {
				logger.error(getJobName() + "Id为" + biddingId + "没有基础分类，此条数据将不会导入");
				continue;
			}
			solrInputDoc.setField("basicCategoryCode", biddingBasic.getCategoryCodeSet());
			solrInputDoc.setField("basicCategoryCode1", biddingBasic.getFirstCategoryCodeSet());
			solrInputDoc.setField("basicCategoryCode2", biddingBasic.getSecondCategoryCodeSet());
			solrInputDoc.setField("basicCategoryCode3", biddingBasic.getThirdCategoryCodeSet());
			solrInputDoc.setField("basicCategoryCode4", biddingBasic.getFourthCategoryCodeSet());
			
			solrInputDoc.setField("basicCategoryName", biddingBasic.getCategoryNameSet());
			solrInputDoc.setField("basicCategoryName1", biddingBasic.getFirstCategoryNameSet());
			solrInputDoc.setField("basicCategoryName2", biddingBasic.getSecondCategoryNameSet());
			solrInputDoc.setField("basicCategoryName3", biddingBasic.getThirdCategoryNameSet());
			solrInputDoc.setField("basicCategoryName4", biddingBasic.getFourthCategoryNameSet());

            Map<String, Object> operationCodeMap = searchHelper.getOperationCategoryCodes(biddingBasic.getCategoryCodeSet(), DataType.BID);
            if (operationCodeMap.isEmpty()){
                logger.error(getJobName() + "Id为" + biddingId + "没有运营分类，此条数据将不会导入");
                continue;
            }
            for (Map.Entry<String,Object> entry : operationCodeMap.entrySet()){
                solrInputDoc.setField(entry.getKey(),entry.getValue());
            }

			solrInputDoc.setField("title", CommonUtil.escapeXml(biddingBasic.getTitle()));
			solrInputDoc.setField("shortTitle", biddingBasic.getShortTitle());
			solrInputDoc.setField("titlePinyin", PinyinUtil.getPinyin(biddingBasic.getTitle()));
			solrInputDoc.setField("registerEndDate", CommonUtil.convertToBeijingTime(biddingBasic.getRegisterEndDate()));
			solrInputDoc.setField("auditTime", CommonUtil.convertToBeijingTime(biddingBasic.getAuditTime()));
			solrInputDoc.setField("deposit", NumberUtils.toDouble(ObjectUtils.toString(biddingBasic.getDeposit())));
			solrInputDoc.setField("biddingRange", CommonUtil.escapeXml(biddingBasic.getBiddingRange()));
			solrInputDoc.setField("biddingCompany", StringUtils.join(biddingBasic.getBiddingCompanySet(), "、"));
			solrInputDoc.setField("publishTime", CommonUtil.convertToBeijingTime(biddingBasic.getPublishTime()));
			solrInputDoc.setField("state", biddingBasic.getState());
			// 状态排序字段
			int stateSort;
			if (biddingBasic.getState() == 2) {
				stateSort = 0;
			}else if(biddingBasic.getState() == 5){
				stateSort = 1;
			}else{
				stateSort = 2;
			}
			solrInputDoc.setField("stateSort", stateSort);


			// 招标开发商
			BiddingDeveloper biddingDeveloper = bidding.getBiddingDeveloper();
			if (biddingDeveloper != null) {
				solrInputDoc.setField("developerId", biddingDeveloper.getId());
				solrInputDoc.setField("developerName", StringUtils.trim(biddingDeveloper.getName()));
				solrInputDoc.setField("developerShortName", StringUtils.trim(biddingDeveloper.getShortName()));
				solrInputDoc.setField("developerLogo", biddingDeveloper.getCompanyLogo());
			}
			
			// 招标关联项目
			BiddingProject biddingProject = bidding.getBiddingProject();
			if (biddingProject != null) {
				solrInputDoc.setField("projectProvinceId", biddingProject.getProvinceCode());
				solrInputDoc.setField("projectProvince", biddingProject.getProvinceName());
				solrInputDoc.setField("projectCityId", biddingProject.getCityCode());
				solrInputDoc.setField("projectCity", biddingProject.getCityName());
				solrInputDoc.setField("projectName", biddingProject.getName());
				solrInputDoc.setField("projectImage", biddingProject.getImage());
				solrInputDoc.setField("projectArea", biddingProject.getArea());
				solrInputDoc.setField("projectTotArea", biddingProject.getTotArea());
				solrInputDoc.setField("projectType", biddingProject.getType());
				
			}
			
			// 招标统计
			BiddingStatics biddingStatics = biddingSearchModel.getBiddingStatics();
			if (biddingStatics != null) {
				solrInputDoc.setField("registerCount", biddingStatics.getRegisterCount());
			}
			
			// 报名条件
			BiddingRegisterCondition biddingRegisterCondition = bidding.getBiddingRegisterCondition();
			if (biddingRegisterCondition == null || biddingRegisterCondition.getItems() == null) {
				logger.warn(getJobName() + "Id为" + biddingId + "的取不到报名条件数据，此条数据将不会导入");
				continue;
			}			
			Map<String, Object> biddingRCItems = biddingRegisterCondition.getItems();
			for (String key : biddingRCItems.keySet()) {
				solrInputDoc.setField(key, biddingRCItems.get(key));
			}
			
			indexList.add(solrInputDoc);
		}
		return indexList;
	}

}