package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.bizsupport.api.OperationCategoryService;
import com.mysoft.b2b.search.scheduler.helper.SearchHelper;
import com.mysoft.b2b.search.spi.recruit.*;
import com.mysoft.b2b.search.utils.CommonUtil;
import com.mysoft.b2b.search.utils.PinyinUtil;
import com.mysoft.b2b.search.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 招募定时任务
 * 
 * @author ganq
 * 
 */
public class RecruitScheduler extends BaseScheduler<RecruitSearchModel> {
    private static final Logger logger = Logger.getLogger(RecruitScheduler.class);

    @Autowired
    private RecruitService recruitService;

    @Autowired
    private SearchHelper searchHelper;

    @Override
    protected void init() {
        setDateKey(PropertiesUtil.SOLR_CORE_RECRUIT_LAST_MODIFY_DATE);
        setSearchHelper(searchHelper);
        setSearchSPIService(recruitService);
        setSolrServer(new HttpSolrServer(PropertiesUtil.getKey(PropertiesUtil.solrProp,PropertiesUtil.SOLR_CORE_RECRUIT_ADDRESS)));
    }
    /**
     * 构建solr导入需要的数据集合
     */
    @Override
    List<SolrInputDocument> buildIndexList(List<RecruitSearchModel> dataList) {
        List<SolrInputDocument> indexList = new ArrayList<SolrInputDocument>(dataList.size());

        SolrInputDocument solrInputDoc;
        for (RecruitSearchModel recruitSearchModel : dataList) {
            solrInputDoc = new SolrInputDocument();
            // 招募id
            String recruitId = recruitSearchModel.getId();
            solrInputDoc.setField(PropertiesUtil.SOLR_CORE_RECRUIT_PRIMARY_KEY, recruitId);
            Recruit recruit = recruitSearchModel.getRecruit();

            if (!(recruit != null && recruit.getRecruitBasic() != null)) {
                logger.error(getJobName() + "Id为" + recruitId + "的取不到数据，此条数据将不会导入");
                continue;
            }

            // 招募基本信息
            RecruitBasic recruitBasic = recruit.getRecruitBasic();
            if (StringUtils.defaultString(recruitBasic.getSubject()).startsWith("test_")) {
                logger.error(getJobName() + "Id为" + recruitId + "为测试数据，此条数据将不会导入");
                continue;
            }

            // 基础分类信息
            if (CollectionUtils.isEmpty(recruitBasic.getCategoryCodeSet())) {
                logger.error(getJobName() + "Id为" + recruitId + "没有基础分类，此条数据将不会导入");
                continue;
            }

            // 招募开发商
            RecruitDeveloper recruitDeveloper = recruit.getRecruitDeveloper();
            if (recruitDeveloper != null) {
                if (StringUtils.defaultString(recruitDeveloper.getName()).startsWith("test_")) {
                    logger.error(getJobName() + "Id为" + recruitId + "的开发商为测试数据，此条数据将不会导入");
                    continue;
                }
                solrInputDoc.setField("companyId", recruitDeveloper.getId());
                solrInputDoc.setField("companyName", recruitDeveloper.getName());
                solrInputDoc.setField("companyShortName", recruitDeveloper.getShortName());
                solrInputDoc.setField("companyLogo", recruitDeveloper.getCompanyLogo());
            }

            solrInputDoc.setField("basicCategoryCode", recruitBasic.getCategoryCodeSet());
            solrInputDoc.setField("basicCategoryCode1", recruitBasic.getFirstCategoryCodeSet());
            solrInputDoc.setField("basicCategoryCode2", recruitBasic.getSecondCategoryCodeSet());
            solrInputDoc.setField("basicCategoryCode3", recruitBasic.getThirdCategoryCodeSet());
            solrInputDoc.setField("basicCategoryCode4", recruitBasic.getFourthCategoryCodeSet());

            solrInputDoc.setField("basicCategoryName", recruitBasic.getCategoryNameSet());
            solrInputDoc.setField("basicCategoryName1", recruitBasic.getFirstCategoryNameSet());
            solrInputDoc.setField("basicCategoryName2", recruitBasic.getSecondCategoryNameSet());
            solrInputDoc.setField("basicCategoryName3", recruitBasic.getThirdCategoryNameSet());
            solrInputDoc.setField("basicCategoryName4", recruitBasic.getFourthCategoryNameSet());

            Map<String,Object> operationCodeMap = searchHelper.getOperationCategoryCodes(recruitBasic.getCategoryCodeSet(), OperationCategoryService.DataType.BID);
            if (operationCodeMap.isEmpty()){
                logger.error(getJobName() + "Id为" + recruitId + "没有运营分类，此条数据将不会导入");
                continue;
            }
            for (Map.Entry<String,Object> entry : operationCodeMap.entrySet()){
                solrInputDoc.setField(entry.getKey(), entry.getValue());
            }

            solrInputDoc.setField("subject", CommonUtil.escapeXml(recruitBasic.getSubject()));
            solrInputDoc.setField("subjectPinyin", PinyinUtil.getPinyin(recruitBasic.getSubject()));
            solrInputDoc.setField("image", recruitBasic.getImage());
            solrInputDoc.setField("registerCondition", CommonUtil.escapeXml(recruitBasic.getRegisterCondition()));
            solrInputDoc.setField("registerFund", recruitBasic.getRegisterFund());
            solrInputDoc.setField("serviceAreaProvinceCode", recruitBasic.getServiceAreaProvinceCode());
            solrInputDoc.setField("serviceAreaProvinceName", recruitBasic.getServiceAreaProvinceName());
            solrInputDoc.setField("serviceAreaCityCode", recruitBasic.getServiceAreaCityCode());
            solrInputDoc.setField("serviceAreaCityName", recruitBasic.getServiceAreaCityName());

            solrInputDoc.setField("searchServiceAreaCode", CollectionUtils.isEmpty(recruitBasic.getSearchServiceAreaCode()) ? new String[]{"null"} : recruitBasic.getSearchServiceAreaCode());
            solrInputDoc.setField("searchServiceAreaName", recruitBasic.getSearchServiceAreaName());

            solrInputDoc.setField("registerEndDate", CommonUtil.convertToBeijingTime(recruitBasic.getRegisterEndDate()));
            solrInputDoc.setField("publishTime", CommonUtil.convertToBeijingTime(recruitBasic.getPublishTime()));
            solrInputDoc.setField("state", recruitBasic.getState());
            // 状态排序字段
            int stateSort;
            if (recruitBasic.getState() == 1) {//报名中
                stateSort = 0;
            } else if (recruitBasic.getState() == 2 || recruitBasic.getState() == 3) {//报名结束
                stateSort = 1;
            } else {
                stateSort = 2;
            }
            solrInputDoc.setField("stateSort", stateSort);

            indexList.add(solrInputDoc);
        }
        return indexList;
    }


}