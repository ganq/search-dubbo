package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.search.scheduler.helper.SearchHelper;
import com.mysoft.b2b.search.spi.developer.*;
import com.mysoft.b2b.search.utils.CommonUtil;
import com.mysoft.b2b.search.utils.PinyinUtil;
import com.mysoft.b2b.search.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发商定时任务
 * 
 * @author ganq
 *
 * 
 */
public class DeveloperScheduler extends BaseScheduler<DeveloperSearchModel>{
	private static final Logger logger = Logger.getLogger(DeveloperScheduler.class);

	@Autowired
	private SearchHelper searchHelper;
	
	@Autowired
	private DeveloperService developerService;

    @Override
    protected void init() {
        setDateKey(PropertiesUtil.SOLR_CORE_DEVELOPER_LAST_MODIFY_DATE);
        setSearchHelper(searchHelper);
        setSearchSPIService(developerService);
        setSolrServer(new HttpSolrServer(PropertiesUtil.getKey(PropertiesUtil.solrProp,PropertiesUtil.SOLR_CORE_DEVELOPER_ADDRESS)));
    }
    /**
     * 构建solr导入需要的数据集合
     */
    @Override
    List<SolrInputDocument> buildIndexList(List<DeveloperSearchModel> dataList) {
        List<SolrInputDocument> indexList = new ArrayList<SolrInputDocument>(dataList.size());
        SolrInputDocument solrInputDoc;

        for (DeveloperSearchModel developerSearchModel : dataList) {
            solrInputDoc = new SolrInputDocument();
            String developerId = developerSearchModel.getId();
            solrInputDoc.setField(PropertiesUtil.SOLR_CORE_DEVELOPER_PRIMARY_KEY, developerId);
			Developer developer = developerSearchModel.getDeveloper();
			if (!(developer != null && developer.getDeveloperBasic() != null)) {			
				logger.error(getJobName() + "Id为" + developerId + "的取不到dubbo数据，此条数据将不会导入");
				continue;
			}
			DeveloperBasic developerBaisc = developer.getDeveloperBasic();
			if (StringUtils.defaultString(developerBaisc.getName()).startsWith("test_")) {
				logger.error(getJobName() + "Id为" + developerId + "的为测试数据，此条数据将不会导入");
				continue;
			}
			
			// 基本信息
			solrInputDoc.setField("developerId", developerId);
			solrInputDoc.setField("developerName", CommonUtil.escapeXml(developerBaisc.getName()));
			solrInputDoc.setField("shortName", CommonUtil.escapeXml(developerBaisc.getShortName()));
			solrInputDoc.setField("namePinyin", PinyinUtil.getPinyin(developerBaisc.getName()));
			solrInputDoc.setField("companyLogo", StringUtils.defaultString(developerBaisc.getCompanyLogo()));
			solrInputDoc.setField("regCityCode", developerBaisc.getRegCityCode());
			solrInputDoc.setField("regProvinceCode", developerBaisc.getRegProvinceCode());
			solrInputDoc.setField("regAddress", developerBaisc.getRegAddress());
			solrInputDoc.setField("regCityName", developerBaisc.getRegCityName());
			solrInputDoc.setField("regProvinceName", developerBaisc.getRegProvinceName());
            solrInputDoc.setField("authorize", developerBaisc.isAuthorize());
			
			String regLocation = "无";
			if (!StringUtils.isBlank(developerBaisc.getRegProvinceName()) && !StringUtils.isBlank(developerBaisc.getRegCityName())) {
				if (developerBaisc.getRegProvinceName().equals(developerBaisc.getRegCityName())) {
					regLocation = developerBaisc.getRegProvinceName() + "市" + developerBaisc.getRegAddress();
				}else{
					regLocation = developerBaisc.getRegProvinceName() + "省" + developerBaisc.getRegCityName() + "市" + developerBaisc.getRegAddress();
				}
			}
			solrInputDoc.setField("regLocation", CommonUtil.escapeXml(regLocation));

			// 开发商统计信息
			DeveloperStatics developerStatics = developerSearchModel.getDeveloperStatics();
			if (developerStatics != null) {
				solrInputDoc.setField("biddingCount", developerStatics.getBiddingCount());
				solrInputDoc.setField("biddingHistoryCount", developerStatics.getBiddingHistoryCount());
                solrInputDoc.setField("recruitCount", developerStatics.getRecruitCount());
			}
			
			// 在建项目
			List<DeveloperProject> developerProjects = developer.getDeveloperProjects();
			if (developerProjects != null && !developerProjects.isEmpty()) {
				List<String> projectInfo = new ArrayList<String>();
				for (DeveloperProject project : developerProjects) {
					String projectCityName = project.getCityName();
					projectInfo.add(project.getName() + (!"".equals(projectCityName) ? "(" + projectCityName + ")" : ""));
				}
				solrInputDoc.setField("projectInfo", CommonUtil.escapeXml(StringUtils.join(projectInfo, "、")));
				solrInputDoc.setField("projectCount", projectInfo.size());
			}
            indexList.add(solrInputDoc);
		}
		return indexList;
	}

}