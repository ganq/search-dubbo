package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.search.api.DeveloperSearchService;
import com.mysoft.b2b.search.api.SearchRecordService;
import com.mysoft.b2b.search.param.DeveloperParam;
import com.mysoft.b2b.search.solr.SolrQueryBO;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.util.BaseUtil;
import com.mysoft.b2b.search.vo.DeveloperVO;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;

/**
 * developerSearchService接口的实现类,提供开发商搜索相关服务
 * @author ganq
 *
 */
@Service("developerSearchService")
public class DeveloperSearchServiceImpl implements DeveloperSearchService {
	private Logger logger =  Logger.getLogger(this.getClass());
	@Autowired
	private SolrQueryEnhanced developerSolr;

	@Autowired
	private SearchRecordService searchRecordService;
	
	private static final String  LOG_MSG = "开发商搜索";
	
	/**
	 * 获取开发商搜索结果
	 * @param developerParam 查询参数
	 * @return List<AnnouncementsVO>
	 * 
	 */
	public Map<String,Object> getSearchResult(DeveloperParam developerParam){
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		//dos 作为字段查询
		List<SolrQueryBO> dos = new ArrayList<SolrQueryBO>();
		
		// 关键字分词列表
		Set<String> analysisWords = new HashSet<String>();
        String word  = "";

		// 根据关键字查询
		if (!StringUtils.isBlank(developerParam.getKeyword())) {
			
			try {
				analysisWords = BaseUtil.getAnalysisWord(developerSolr.getSolrServer(),developerParam.getKeyword());
            } catch (IOException e) {
                logger.error(LOG_MSG + "IOException", e);
                analysisWords.add(developerParam.getKeyword());
            } catch (SolrServerException e) {
                logger.error(LOG_MSG + "SolrServerException", e);
                analysisWords.add(developerParam.getKeyword());
            } catch (Exception e) {
                logger.error(LOG_MSG + "分词错误", e);
                analysisWords.add(developerParam.getKeyword());
            }
						
			word  = "(" + StringUtils.join(analysisWords.toArray(), " ") + ")";
			dos.add(new SolrQueryBO().setfN("developerName").setHighlightField(true));
			dos.add(new SolrQueryBO().setfN("regLocation").setHighlightField(true));
			dos.add(new SolrQueryBO().setfN("projectInfo").setHighlightField(true));
						
			
			dos.add(new SolrQueryBO().setCustomQueryStr(word).setQueryField(true));
			
			Map<String, String[]> paramMap = new HashMap<String, String[]>();
			paramMap.put("defType", new String []{"edismax"});
			paramMap.put("qf", new String []{"shortName developerName^10 regLocation projectInfo nameSearchField"});
			paramMap.put("pf", new String []{"shortName developerName^10 regLocation projectInfo nameSearchField"});
			
			SolrParams solrParams = new MultiMapSolrParams(paramMap);
			dos.add(new SolrQueryBO().setSolrParams(solrParams));
		}else{
			// 没有关键字，查询全部
			dos.add(new SolrQueryBO().setfN("*").setfV("*").setQueryField(true));
		}
		
		Map<String, String[]> paramMap = new HashMap<String, String[]>();
		paramMap.put("defType", new String []{"edismax"});
		paramMap.put("bf", new String []{"ord(biddingCount)"});
		
		SolrParams solrParams = new MultiMapSolrParams(paramMap);
		dos.add(new SolrQueryBO().setSolrParams(solrParams));
		
		try {

			int rowNum = developerParam.getRowNum();
			int pageSize = developerParam.getPageSize();
			QueryResponse queryResponse = BaseUtil.getQueryResponse(developerSolr,dos , rowNum,pageSize);
			
			SolrDocumentList searchResult = queryResponse.getResults();				
			
			// 设置名称高亮
			BaseUtil.setHighlightText(queryResponse, "developerId", "developerName",word,false);
			BaseUtil.setHighlightText(queryResponse, "developerId", "projectInfo",word,false);
			BaseUtil.setHighlightText(queryResponse, "developerId", "regLocation",word,false);
			
			resultMap.put("searchResult", BaseUtil.docListToVoList(searchResult,DeveloperVO.class));
			resultMap.put("totalRecordNum", searchResult.getNumFound());
			
			if (!StringUtils.isBlank(developerParam.getKeyword()) && "1".equals(developerParam.getPage())) {
				searchRecordService.execAddSearchRecord(developerParam, analysisWords, searchResult.getNumFound());
			}
			
			logger.info(MessageFormat.format("----------------本次搜索：搜索参数：“{0}”,结果行数：“{1}”----------------", developerParam,searchResult.getNumFound()));
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
