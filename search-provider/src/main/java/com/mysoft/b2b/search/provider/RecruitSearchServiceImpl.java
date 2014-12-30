package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.bizsupport.api.OperationCategoryService;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.search.api.RecruitSearchService;
import com.mysoft.b2b.search.api.SearchRecordService;
import com.mysoft.b2b.search.param.RecruitParam;
import com.mysoft.b2b.search.solr.SolrQueryBO;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.util.BaseUtil;
import com.mysoft.b2b.search.vo.RecruitVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
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
 * RecruitSearchService接口的实现类,提供招募搜索相关服务
 * @author ganq
 *
 */
@Service("recruitSearchService")
public class RecruitSearchServiceImpl implements RecruitSearchService {

	private Logger logger =  Logger.getLogger(this.getClass());
	
	@Autowired
	private SolrQueryEnhanced recruitSolr;

	@Autowired
	private OperationCategoryService operationCategoryService;

	@Autowired
	private SearchRecordService searchRecordService;

	
	private static final String  LOG_MSG = "招募搜索";
	/**
	 * 获取搜索结果
	 * @
	 */
	public Map<String,Object> getSearchResult(RecruitParam recruitParam) {
		
		
		Map<String,Object> resultMap = new HashMap<String, Object>();

		//dos 作为字段查询
		List<SolrQueryBO> dos = new ArrayList<SolrQueryBO>();
		
		//dos2 作为一二级分类code  facet字段查询
		List<SolrQueryBO> dos2 = new ArrayList<SolrQueryBO>();
		
		//dos3作为三级分类code  facet字段查询
		List<SolrQueryBO> dos3 = new ArrayList<SolrQueryBO>();
				
		// 关键字分词列表
		Set<String> analysisWords = new HashSet<String>();

        String word = "";
		// 根据关键字查询
		if (!StringUtils.isBlank(recruitParam.getKeyword())) {
			
			try {
				analysisWords = BaseUtil.getAnalysisWord(recruitSolr.getSolrServer(),recruitParam.getKeyword());
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
			word  = "(" + StringUtils.join(analysisWords.toArray(), " ") + ")";
			dos.add(new SolrQueryBO().setfN("subject").setHighlightField(true));
			dos.add(new SolrQueryBO().setfN("registerCondition").setHighlightField(true));
			
			
			dos.add(new SolrQueryBO().setCustomQueryStr(word).setQueryField(true));
			
			dos2.add(dos.get(dos.size()-1));
			dos3.add(dos.get(dos.size()-1));
			
			
			Map<String, String[]> paramMap = new HashMap<String, String[]>();
			paramMap.put("defType", new String []{"edismax"});
			paramMap.put("qf", new String []{"subject^10 registerCondition searchServiceAreaName^3"});
			paramMap.put("pf", new String []{"subject^10 registerCondition searchServiceAreaName^3"});
			
			SolrParams solrParams = new MultiMapSolrParams(paramMap);
			dos.add(new SolrQueryBO().setSolrParams(solrParams));
			
			dos2.add(dos.get(dos.size()-1));
			dos3.add(dos.get(dos.size()-1));
		
			dos2.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode1"));
			dos2.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode2"));
			dos3.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode3"));
			
			//关键字搜索下 按状态分段排序再按相关度倒序
			dos.add(new SolrQueryBO().setSortField(true).setfN("stateSort").setSort(ORDER.asc));
			dos.add(new SolrQueryBO().setSortField(true).setfN("score").setSort(ORDER.desc));
			
		}else{
			
			dos3.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode3"));
			
			// 没有关键字，查询全部
			dos.add(new SolrQueryBO().setfN("*").setfV("*").setQueryField(true));
			dos2.add(dos.get(dos.size()-1));
			dos3.add(dos.get(dos.size()-1));
			
			//按状态和报名截止时间双重影响排序
			dos.add(BaseUtil.setRecruitBfSortBo());
			
		}
	
		// 项目所在地查询
		if (!StringUtils.isBlank(recruitParam.getLocation()) &&!"china".equals(recruitParam.getLocation())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("searchServiceAreaCode").setfV("("+recruitParam.getLocation()+" OR null)"));
		}
		
		// 一级分类code查询
		if (!StringUtils.isBlank(recruitParam.getCodelevel1())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode1").setfV(recruitParam.getCodelevel1()));
			dos3.add(dos.get(dos.size()-1));
		}
		// 二级分类code查询
		if (!StringUtils.isBlank(recruitParam.getCodelevel2())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode2").setfV(recruitParam.getCodelevel2()));
			dos3.add(dos.get(dos.size()-1));
		}
		// 三级分类code查询
		if (!StringUtils.isBlank(recruitParam.getCodelevel3())) {
			String codellvl3Value;
			if (recruitParam.getCodelevel3().contains(",")) {
				String [] fccodeArray = recruitParam.getCodelevel3().split(",");
				codellvl3Value = "(" + StringUtils.join(fccodeArray, " OR ") + ")";
			}else{
				codellvl3Value = recruitParam.getCodelevel3();
			}
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode3").setfV(codellvl3Value));
		}
		// 状态  (x代表不限)
		if (!StringUtils.isBlank(recruitParam.getState()) && !"x".equals(recruitParam.getState())) {
			String [] stateArray = recruitParam.getState().split(",");
			for (int i=0;i<stateArray.length;i++) {
				stateArray[i] = NumberUtils.toInt(stateArray[i]) + "";
			}
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("state").setfV("("+StringUtils.join(stateArray," OR ")+")"));
		}
		// 注册资本不高于
		if (!StringUtils.isBlank(recruitParam.getRegcapital()) && NumberUtils.isNumber(recruitParam.getRegcapital())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("registerFund").setfV("[* TO "+recruitParam.getRegcapital() + "]"));
		}

		
		
		// 点击报名截止时间排序
		if (!StringUtils.isBlank(recruitParam.getSdatesort())) {
			SolrQueryBO do7 = new SolrQueryBO();
			do7.setSortField(true).setfN("registerEndDate");
			if ("0".equals(recruitParam.getSdatesort())) {
				do7.setSort(ORDER.asc);
			}else{
				do7.setSort(ORDER.desc);
			}
			dos.add(do7);
		}
		
		// 按发布时间排序
		if (!StringUtils.isBlank(recruitParam.getPdatesort())) {
			SolrQueryBO do7 = new SolrQueryBO();
			do7.setSortField(true).setfN("publishTime");
			if ("0".equals(recruitParam.getPdatesort())) {
				do7.setSort(ORDER.asc);
			}else{
				do7.setSort(ORDER.desc);
			}
			dos.add(do7);
								
		}
		
		try {
			int rowNum = recruitParam.getRowNum();
			int pageSize = recruitParam.getPageSize();
			QueryResponse queryResponse = BaseUtil.getQueryResponse(recruitSolr, dos, rowNum, pageSize);
			QueryResponse queryResponse2 = BaseUtil.getQueryResponse(recruitSolr, dos2, 0, 0);
			QueryResponse queryResponse3 = BaseUtil.getQueryResponse(recruitSolr, dos3, 0, 0);
			
			SolrDocumentList searchResult = queryResponse.getResults();
			
			// 没有关键字则查询全部运营分类，否则根据搜索结果反向匹配
			if (!StringUtils.isBlank(recruitParam.getKeyword())) {
				resultMap.put("relatedCategory", BaseUtil.getResultCategory(DataType.BID,operationCategoryService,queryResponse2.getFacetFields()));
			}else{
				resultMap.put("relatedCategory", BaseUtil.getOperationCategoryList(DataType.BID, operationCategoryService));
			}
			
			// 有关键字查询或者用一，二级分类查询时，三级分类显示
			
			if (!StringUtils.isBlank(recruitParam.getCodelevel1()) || !StringUtils.isBlank(recruitParam.getCodelevel2())
					|| !StringUtils.isBlank(recruitParam.getCodelevel3()) || !StringUtils.isBlank(recruitParam.getKeyword())){
				resultMap.put("level3Category", BaseUtil.getResultLvl3Category(operationCategoryService,
						queryResponse3.getFacetFields(),DataType.BID,recruitParam.getCodelevel2(),recruitParam.getCodelevel1()));
			}
			
			// 设置标题高亮
			BaseUtil.setHighlightText(queryResponse, "recruitId", "subject",word,false);
			BaseUtil.setHighlightText(queryResponse, "recruitId", "registerCondition",word,false);
			
			resultMap.put("searchResult", BaseUtil.docListToVoList(searchResult, RecruitVO.class));
			resultMap.put("totalRecordNum", searchResult.getNumFound());
			
			//添加搜索记录
			if (!StringUtils.isBlank(recruitParam.getKeyword()) && StringUtils.isBlank(recruitParam.getCodelevel1())
					&& StringUtils.isBlank(recruitParam.getCodelevel2()) && StringUtils.isBlank(recruitParam.getCodelevel3())
					&& StringUtils.isBlank(recruitParam.getLocation()) && StringUtils.isBlank(recruitParam.getState())
					&& StringUtils.isBlank(recruitParam.getRegcapital()) && StringUtils.isBlank(recruitParam.getSdatesort())
					&& StringUtils.isBlank(recruitParam.getPdatesort()) && "1".equals(recruitParam.getPage())) {

				searchRecordService.execAddSearchRecord(recruitParam, analysisWords, searchResult.getNumFound());
			}
			
			logger.info(MessageFormat.format("----------------本次搜索：搜索参数“{0}”,结果行数：“{1}”----------------", recruitParam,searchResult.getNumFound()));
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

