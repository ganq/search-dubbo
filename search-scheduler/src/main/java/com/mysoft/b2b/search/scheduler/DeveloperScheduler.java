package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.commons.scheduler.MysoftJob;
import com.mysoft.b2b.search.scheduler.helper.SearchHelper;
import com.mysoft.b2b.search.spi.developer.*;
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
 * 开发商定时任务
 * 
 * @author ganq
 * 
                    _ooOoo_ 
                   o8888888o 
                   88" . "88 
                   (| -_- |) 
                   O\  =  /O 
                ____/`---'\____ 
              .'  \\|     |//  `. 
             /  \\|||  :  |||//  \ 
            /  _||||| -:- |||||-  \ 
            |   | \\\  -  /// |   | 
            | \_|  ''\---/''  |   | 
            \  .-\__  `-`  ___/-. / 
          ___`. .'  /--.--\  `. . __ 
       ."" '<  `.___\_<|>_/___.'  >'"". 
      | | :  `- \`.;`\ _ /`;.`/ - ` : | | 
      \  \ `-.   \_ __\ /__ _/   .-` /  / 
 ======`-.____`-.___\_____/___.-`____.-'====== 
                    `=---=' 
 
 
 
 
 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 
                                               佛祖保佑       永无bug
                                               心外无法       法外无心 
 * 
 */
public class DeveloperScheduler extends MysoftJob{
	private static final Logger logger = Logger.getLogger(DeveloperScheduler.class);

	@Autowired
	private SearchHelper searchHelper;
	
	@Autowired
	private DeveloperService developerService;
	
	// 是否首次导入
	private boolean isFirstImport = false;
	
	private SolrServer developerServer = new HttpSolrServer(PropertiesUtil.getKey(PropertiesUtil.SOLR_CORE_DEVELOPER_ADDRESS));

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
		List<Map<String, Object>> dataList = getDevelopers();
		if (CollectionUtils.isEmpty(dataList)) {
			logger.info(getJobName() + "此次构建没有数据更新！");
			return;
		}
		buildDeveloperToSolr(dataList);
	}
	
	/**
	 * 获取开发商信息
	 */
	private List<Map<String, Object>> getDevelopers() {
		
		String date = PropertiesUtil.getKey(PropertiesUtil.SOLR_CORE_DEVELOPER_LAST_MODIFY_DATE);
		Set<String> developerIds;
		  
		if ("".equals(date)) {
			isFirstImport = true;
			developerIds = developerService.getAllId();
		} else {
			isFirstImport = false;
			developerIds = developerService.getIdsByLastModifyTime(NumberUtils.toLong(date));
			if (!CollectionUtils.isEmpty(developerIds)) {
				logger.info(getJobName() + "本次增量更新的Id为：" + StringUtils.join(developerIds,","));
			}
		}
		
		lastModifyTime = developerService.getNewestLastModifyTime();
		PropertiesUtil.setKey(PropertiesUtil.SOLR_CORE_DEVELOPER_LAST_MODIFY_DATE, ObjectUtils.toString(lastModifyTime));
				
		
		if (developerIds == null) {
			logger.info(getJobName() + "通过spi接口获取id数据异常");
			return null;
		}
		logger.info(getJobName() + "本次待插入索引数据" + developerIds.size() + "条");
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String developerId : developerIds) {
			DeveloperSearchModel developerSearchModel  = developerService.getSearchModelById(developerId);
			if (developerSearchModel == null) {
				logger.info(getJobName() + "Id 为“"+developerId+"”通过spi接口获取数据异常，将不导入索引库！！！");
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			
			Developer developer = developerSearchModel.getDeveloper();
			if (!(developer != null && developer.getDeveloperBasic() != null)) {			
				logger.warn(getJobName() + "Id为" + developerId + "的取不到dubbo数据，此条数据将不会导入");
				continue;
			}
			DeveloperBasic developerBaisc = developer.getDeveloperBasic();
			if (StringUtils.defaultString(developerBaisc.getName()).startsWith("test_")) {
				logger.warn(getJobName() + "Id为" + developerId + "的为测试数据，此条数据将不会导入");
				continue;
			}
			
			// 基本信息
			map.put("developerId", developerId);
			map.put("developerName", CommonUtil.escapeXml(developerBaisc.getName()));
			map.put("shortName", CommonUtil.escapeXml(developerBaisc.getShortName()));
			map.put("namePinyin", PinyinUtil.getPinyin(developerBaisc.getName()));
			map.put("companyLogo", StringUtils.defaultString(developerBaisc.getCompanyLogo()));
			map.put("regCityCode", developerBaisc.getRegCityCode());
			map.put("regProvinceCode", developerBaisc.getRegProvinceCode());
			map.put("regAddress", developerBaisc.getRegAddress());
			map.put("regCityName", developerBaisc.getRegCityName());
			map.put("regProvinceName", developerBaisc.getRegProvinceName());
			
			String regLocation = "无";
			if (!StringUtils.isBlank(developerBaisc.getRegProvinceName()) && !StringUtils.isBlank(developerBaisc.getRegCityName())) {
				if (developerBaisc.getRegProvinceName().equals(developerBaisc.getRegCityName())) {
					regLocation = developerBaisc.getRegProvinceName() + "市" + developerBaisc.getRegAddress();
				}else{
					regLocation = developerBaisc.getRegProvinceName() + "省" + developerBaisc.getRegCityName() + "市" + developerBaisc.getRegAddress();
				}
			}
			map.put("regLocation", CommonUtil.escapeXml(regLocation));

			// 开发商统计信息
			DeveloperStatics developerStatics = developerSearchModel.getDeveloperStatics();
			if (developerStatics != null) {
				map.put("biddingCount", developerStatics.getBiddingCount());	
				map.put("biddingHistoryCount", developerStatics.getBiddingHistoryCount());
			}
			
			// 在建项目
			List<DeveloperProject> developerProjects = developer.getDeveloperProjects();
			if (developerProjects != null && !developerProjects.isEmpty()) {
				List<String> projectInfo = new ArrayList<String>();
				for (DeveloperProject project : developerProjects) {
					String projectCityName = project.getCityName();
					projectInfo.add(project.getName() + (!"".equals(projectCityName) ? "(" + projectCityName + ")" : ""));
				}
				map.put("projectInfo", CommonUtil.escapeXml(StringUtils.join(projectInfo,"、")));
				map.put("projectCount", projectInfo.size());
			}
			list.add(map);
		}
		return list;
	}

	/**
	 * 创建开发商索引到Solr
	 * 
	 * @throws IOException
	 * @throws SolrServerException
	 * 
	 */
	private void buildDeveloperToSolr(List<Map<String, Object>> solrIndexList) throws SolrServerException,IOException {

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
			developerServer.deleteByQuery("*:*");
		}
		developerServer.add(docs);
		developerServer.commit();

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
		Set<String> invalidIds = developerService.getInvalidIdsByLastModifyTime(lastModifyTime);
		if (CollectionUtils.isEmpty(invalidIds)) {
			return;
		}
		developerServer.deleteById(new ArrayList<String>(invalidIds));
		developerServer.commit();
		logger.info(getJobName() +"删除不可用的索引" + invalidIds.size() + "条,他们的id为：" + StringUtils.join(invalidIds,","));
	}

}