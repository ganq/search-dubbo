package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.basicsystem.settings.api.DictionaryService;
import com.mysoft.b2b.basicsystem.settings.api.Region;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.commons.scheduler.MysoftJob;
import com.mysoft.b2b.search.scheduler.helper.AnnouncementHelper;
import com.mysoft.b2b.search.scheduler.helper.SchedulerThreadData;
import com.mysoft.b2b.search.scheduler.helper.SearchHelper;
import com.mysoft.b2b.search.spi.SearchModel;
import com.mysoft.b2b.search.spi.recruit.*;
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
 * 招募定时任务
 * 
 * @author ganq
 * 
 */
public class RecruitScheduler extends MysoftJob implements SchedulerThreadData{
	private static final Logger logger = Logger.getLogger(RecruitScheduler.class);

	@Autowired
	private AnnouncementHelper announcementHelper;

	@Autowired
	private RecruitService recruitService;

	@Autowired
	private SearchHelper searchHelper;

    @Autowired
    private DictionaryService dictionaryService;



    // 是否首次导入
	private boolean isFirstImport = false;

	private SolrServer recruitServer = new HttpSolrServer(PropertiesUtil.getKey(PropertiesUtil.SOLR_CORE_RECRUIT_ADDRESS));

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
	 * @throws org.apache.solr.client.solrj.SolrServerException
	 * @throws java.io.IOException
	 */
	private void buildIndexes() throws SolrServerException, IOException {
		List<RecruitSearchModel> dataList = getRecruitInfo();
		if (CollectionUtils.isEmpty(dataList)) {
			logger.info(getJobName() + "此次构建没有数据更新！");
			return;
		}

		List<Map<String, Object>> solrIndexList = builidIndexList(dataList);
		if (CollectionUtils.isEmpty(solrIndexList)) {
			logger.info(getJobName() + "此次构建没有数据更新！");
			return;
		}

		buildRecruitToSolr(solrIndexList);

		Set<String> ids = new HashSet<String>();
		if (!isFirstImport) {
			for (Map<String,Object> map :solrIndexList) {
				ids.add(PropertiesUtil.SOLR_CORE_RECRUIT_PRIMARY_KEY + ":" + ObjectUtils.toString(map.get(PropertiesUtil.SOLR_CORE_RECRUIT_PRIMARY_KEY)));
			}
		}
		int rowCount = announcementHelper.updateOperationCategory(ids, DataType.BID, recruitServer,solrIndexList,PropertiesUtil.SOLR_CORE_RECRUIT_PRIMARY_KEY);
		logger.info(getJobName() +"运营分类更新成功" + rowCount + "条");
	}

	/**
	 * 获取招募数据
	 * @throws java.io.IOException
	 * @throws org.apache.solr.client.solrj.SolrServerException
	 *
	 */
	private List<RecruitSearchModel> getRecruitInfo() throws SolrServerException, IOException {
		String date = PropertiesUtil.getKey(PropertiesUtil.SOLR_CORE_RECRUIT_LAST_MODIFY_DATE);

		Set<String> recruitIds;

		if ("".equals(date)) {
			isFirstImport = true;
			recruitIds = recruitService.getAllId();
		} else {
			isFirstImport = false;
			recruitIds = recruitService.getIdsByLastModifyTime(NumberUtils.toLong(date));
			if (!CollectionUtils.isEmpty(recruitIds)) {
				logger.info(getJobName() + "本次增量更新的Id为：" + StringUtils.join(recruitIds,","));
			}

		}

		// 根据lastmodifydate 判断是否要增量更新
		lastModifyTime = recruitService.getNewestLastModifyTime();
		PropertiesUtil.setKey(PropertiesUtil.SOLR_CORE_RECRUIT_LAST_MODIFY_DATE,ObjectUtils.toString(lastModifyTime));

		if (recruitIds == null) {
			logger.info(getJobName() + "通过spi接口获取id数据异常");
			return null;
		}

        List<RecruitSearchModel> list = searchHelper.getDataByThread(recruitIds, 10, this,RecruitSearchModel.class);

		logger.info(getJobName() + "本次待插入索引数据" + list.size() + "条");
		return list;

	}

	@Override
	public SearchModel getSingleDataObj(String id) {
		RecruitSearchModel recruitSearchModel = recruitService.getSearchModelById(id);
		if (recruitSearchModel == null) {
			logger.info(getJobName() + "Id 为“"+id+"”通过spi接口获取数据异常，将不导入索引库！！！");
			return null;
		}
		return recruitSearchModel;
	}

	/**
	 * 构建solr导入需要的数据集合
	 */
	private List<Map<String, Object>> builidIndexList(List<RecruitSearchModel> dataList) {
		List<Map<String, Object>> indexList = new ArrayList<Map<String,Object>>();
		for (RecruitSearchModel recruitSearchModel : dataList) {
			Map<String, Object> map = new HashMap<String, Object>();

			// 招募id
			String recruitId = recruitSearchModel.getId();
			map.put(PropertiesUtil.SOLR_CORE_RECRUIT_PRIMARY_KEY, recruitId);
			Recruit recruit = recruitSearchModel.getRecruit();

			if (!(recruit != null && recruit.getRecruitBasic() != null)) {
				logger.warn(getJobName() + "Id为" + recruitId + "的取不到数据，此条数据将不会导入");
				continue;
			}

			// 招募基本信息
            RecruitBasic recruitBasic = recruit.getRecruitBasic();
			if (StringUtils.defaultString(recruitBasic.getSubject()).startsWith("test_")) {
				logger.warn(getJobName() + "Id为" + recruitId + "为测试数据，此条数据将不会导入");
				continue;
			}
			// 基础分类信息
			if (CollectionUtils.isEmpty(recruitBasic.getCategoryCodeSet())) {
				logger.warn(getJobName() + "Id为" + recruitId + "没有基础分类，此条数据将不会导入");
				continue;
			}

            // 招募开发商
            RecruitDeveloper recruitDeveloper = recruit.getRecruitDeveloper();
            if (recruitDeveloper != null) {
                if (StringUtils.defaultString(recruitDeveloper.getName()).startsWith("test_")) {
                    logger.warn(getJobName() + "Id为" + recruitId + "的开发商为测试数据，此条数据将不会导入");
                    continue;
                }
                map.put("companyId", recruitDeveloper.getId());
                map.put("companyName",recruitDeveloper.getName());
                map.put("companyShortName", recruitDeveloper.getShortName());
                map.put("companyLogo", recruitDeveloper.getCompanyLogo());
            }

			map.put("basicCategoryCode", recruitBasic.getCategoryCodeSet());
			map.put("basicCategoryCode1", recruitBasic.getFirstCategoryCodeSet());
			map.put("basicCategoryCode2", recruitBasic.getSecondCategoryCodeSet());
			map.put("basicCategoryCode3", recruitBasic.getThirdCategoryCodeSet());
			map.put("basicCategoryCode4", recruitBasic.getFourthCategoryCodeSet());

			map.put("basicCategoryName", recruitBasic.getCategoryNameSet());
			map.put("basicCategoryName1", recruitBasic.getFirstCategoryNameSet());
			map.put("basicCategoryName2", recruitBasic.getSecondCategoryNameSet());
			map.put("basicCategoryName3", recruitBasic.getThirdCategoryNameSet());
			map.put("basicCategoryName4", recruitBasic.getFourthCategoryNameSet());

			map.put("subject", CommonUtil.escapeXml(recruitBasic.getSubject()));
			map.put("subjectPinyin", PinyinUtil.getPinyin(recruitBasic.getSubject()));
            map.put("image",recruitBasic.getImage());
            map.put("registerCondition",CommonUtil.escapeXml(recruitBasic.getRegisterCondition()));
            map.put("registerFund",recruitBasic.getRegisterFund());
            map.put("serviceAreaProvinceCode",recruitBasic.getServiceAreaProvinceCode());
            map.put("serviceAreaProvinceName",recruitBasic.getServiceAreaProvinceName());
            map.put("serviceAreaCityCode",recruitBasic.getServiceAreaCityCode());
            map.put("serviceAreaCityName",recruitBasic.getServiceAreaCityName());

            map.put("searchServiceAreaCode",CollectionUtils.isEmpty(recruitBasic.getSearchServiceAreaCode())?new String[]{"null"}:recruitBasic.getSearchServiceAreaCode());
            map.put("searchServiceAreaName",recruitBasic.getSearchServiceAreaName());

            map.put("registerEndDate",CommonUtil.convertToBeijingTime(recruitBasic.getRegisterEndDate()));
			map.put("publishTime", CommonUtil.convertToBeijingTime(recruitBasic.getPublishTime()));
			map.put("state", recruitBasic.getState());
			// 状态排序字段
			int stateSort;
			if (recruitBasic.getState() == 1) {//报名中
				stateSort = 0;
			}else if(recruitBasic.getState() == 2 || recruitBasic.getState() == 3){//报名结束
				stateSort = 1;
			}else{
				stateSort = 2;
			}
			map.put("stateSort", stateSort);

			indexList.add(map);
		}
		return indexList;
	}


	/**
	 * 创建招募索引到Solr
	 *
	 * @throws java.io.IOException
	 * @throws org.apache.solr.client.solrj.SolrServerException
	 * 
	 */
	private void buildRecruitToSolr(List<Map<String, Object>> solrIndexList) throws SolrServerException,IOException {
		
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
			recruitServer.deleteByQuery("*:*");
		}
		recruitServer.add(docs);
		recruitServer.commit();

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
		Set<String> invalidIds = recruitService.getInvalidIdsByLastModifyTime(lastModifyTime);
		if (CollectionUtils.isEmpty(invalidIds)) {
			return;
		}
		recruitServer.deleteById(new ArrayList<String>(invalidIds));
		recruitServer.commit();
		logger.info(getJobName() +"删除不可用的索引" + invalidIds.size() + "条,他们的id为：" + StringUtils.join(invalidIds,","));
	}
}