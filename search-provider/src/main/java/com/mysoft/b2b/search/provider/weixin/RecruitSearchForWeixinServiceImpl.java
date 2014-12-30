package com.mysoft.b2b.search.provider.weixin;

import com.mysoft.b2b.search.api.SearchRecordService;
import com.mysoft.b2b.search.api.weixin.RecruitSearchForWeixinService;
import com.mysoft.b2b.search.param.RecruitParam;
import com.mysoft.b2b.search.param.SearchSource;
import com.mysoft.b2b.search.solr.SolrQueryBO;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.util.BaseUtil;
import com.mysoft.b2b.search.vo.RecruitVO;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service("recruitSearchForWeixinService")
public class RecruitSearchForWeixinServiceImpl implements RecruitSearchForWeixinService {

	private Logger logger =  Logger.getLogger(this.getClass());
	
	@Autowired
	private SolrQueryEnhanced recruitSolr;
	
	@Autowired
	private SearchRecordService searchRecordService;
	
	private static final String  LOG_MSG = "招募微信搜索";
	
	/**
	 * 获取招募搜索结果
	 * @
	 */
	public Map<String,Object> getRecruitSearchResult(RecruitParam recruitParam) {
		
		Map<String,Object> resultMap = new HashMap<String, Object>();		
		List<SolrQueryBO> dos = new ArrayList<SolrQueryBO>();
						
		// 关键字分词列表
		Set<String> analysisWords = new HashSet<String>();
		// 根据关键字查询
		if (!StringUtils.isBlank(recruitParam.getKeyword())) {
			
			try {
				analysisWords = BaseUtil.getAnalysisWord(recruitSolr.getSolrServer(), recruitParam.getKeyword());
            } catch (IOException e) {
                logger.error(LOG_MSG + "IOException", e);
                analysisWords.add(recruitParam.getKeyword());
            } catch (SolrServerException e) {
                logger.error(LOG_MSG + "SolrServerException", e);
                analysisWords.add(recruitParam.getKeyword());
            } catch (Exception e) {
                logger.error(LOG_MSG + "分词错误", e);
                analysisWords.add(recruitParam.getKeyword());
            }
			String word  = "(" + StringUtils.join(analysisWords.toArray(), " ") + ")";						
			
			dos.add(new SolrQueryBO().setCustomQueryStr(word).setQueryField(true));			
			
			Map<String, String[]> paramMap = new HashMap<String, String[]>();
			paramMap.put("defType", new String []{"edismax"});
            paramMap.put("qf", new String []{"subject^10 registerCondition searchServiceAreaName^3"});
            paramMap.put("pf", new String []{"subject^10 registerCondition searchServiceAreaName^3"});

            SolrParams solrParams = new MultiMapSolrParams(paramMap);
			dos.add(new SolrQueryBO().setSolrParams(solrParams));

            //关键字搜索下 按状态分段排序再按相关度倒序
            dos.add(new SolrQueryBO().setSortField(true).setfN("stateSort").setSort(SolrQuery.ORDER.asc));
            dos.add(new SolrQueryBO().setSortField(true).setfN("score").setSort(SolrQuery.ORDER.desc));
			
		}else{
			// 没有关键字，查询全部
			dos.add(new SolrQueryBO().setfN("*").setfV("*").setQueryField(true));
		}
			
		//按状态和报名截止时间双重影响排序
		dos.add(BaseUtil.setRecruitBfSortBo());
				
		try {
			int rowNum = recruitParam.getRowNum();
			int pageSize = recruitParam.getPageSize();
			QueryResponse queryResponse = BaseUtil.getQueryResponse(recruitSolr, dos, rowNum, pageSize);
			
			SolrDocumentList searchResult = queryResponse.getResults();			
			
			resultMap.put("searchResult", BaseUtil.docListToVoList(searchResult, RecruitVO.class));
			resultMap.put("totalRecordNum", searchResult.getNumFound());
			
			//添加搜索记录
			if (!StringUtils.isBlank(recruitParam.getKeyword()) && "1".equals(recruitParam.getPage())) {
				recruitParam.setSearchSource(SearchSource.WEIXIN);
				searchRecordService.execAddSearchRecord(recruitParam, analysisWords, searchResult.getNumFound());
			}
        } catch (NoSuchMethodException e) {
            logger.error(LOG_MSG + "：NoSuchMethodException  ", e);
        } catch (IllegalAccessException e) {
            logger.error(LOG_MSG + "：IllegalAccessException  ", e);
        } catch (InstantiationException e) {
            logger.error(LOG_MSG + "：InstantiationException  ", e);
        } catch (ClassNotFoundException e) {
            logger.error(LOG_MSG + "：ClassNotFoundException  ", e);
        } catch (InvocationTargetException e) {
            logger.error(LOG_MSG + "：InvocationTargetException  ", e);
        } catch (SolrServerException e) {
            logger.error(LOG_MSG + "：SolrServerException  ", e);
        }catch (Exception e) {
            logger.error(LOG_MSG + "错误", e);
        }
				
		return resultMap;
	}
}

