package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.search.api.AnnouncementSubscribeService;
import com.mysoft.b2b.search.param.AnnouncementParam;
import com.mysoft.b2b.search.solr.SolrQueryBO;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.util.BaseUtil;
import com.mysoft.b2b.search.vo.AnnouncementsVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AnnouncementSubscribeService接口的实现类,提供订阅招标预告搜索相关服务
 * @author ganq
 *
 */
@Service("announcementSubscribeService")
public class AnnouncementSubscribeServiceImpl implements AnnouncementSubscribeService {

	private Logger logger =  Logger.getLogger(this.getClass());
	
	@Autowired
	private SolrQueryEnhanced announcementsSolr;

    private static final String  LOG_MSG = "招标预告订阅搜索";
	/**
	 * 获取搜索结果
	 */
	public Map<String,Object> getSearchResult(AnnouncementParam announcementParam) {
		
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		//dos 作为字段查询
		List<SolrQueryBO> dos = new ArrayList<SolrQueryBO>();
		
		// 不带任何条件查询全部
		if (announcementParam.isQueryAll()) {
			dos.add(new SolrQueryBO().setQueryField(true).setfN("*").setfV("*"));	
		}else{
		
			if (!StringUtils.isBlank(announcementParam.getCategorycode())) {
				dos.add(new SolrQueryBO().setQueryField(true).setfN("searchBasicCategoryCode").setfV("("+announcementParam.getCategorycode().replace(",", " OR ")+")"));	
			}
			if (!StringUtils.isBlank(announcementParam.getLocation())) {
				dos.add(new SolrQueryBO().setQueryField(true).setfN("projectLocation").setfV("("+announcementParam.getLocation().replace(",", " OR ")+")"));
			}
			
			if (!StringUtils.isBlank(announcementParam.getKeyword())) {
				String keywordValue = "("+announcementParam.getKeyword().replace(",", " OR ")+")";
				String customQueryStr = "(title:" + keywordValue + " OR basicCategoryName:" + keywordValue + ")";
				dos.add(new SolrQueryBO().setQueryField(true).setCustomQueryStr(customQueryStr));	
			}
			
		}
		
		// 招标预告审核时间(查询该时间当天的数据)
		if (announcementParam.getAuditTime() != null) {
			String dateVal = DateUtils.formatDate(announcementParam.getAuditTime(), "yyyy-MM-dd");
			String dateBegin = dateVal + "T00:00:00Z";
			String dateEnd = dateVal + "T23:59:59Z";				
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("auditTime").setfV("[" + dateBegin + " TO " + dateEnd + "]"));
		}
		if (!StringUtils.isBlank(announcementParam.getPdatesort())) {
			if ("0".equals(announcementParam.getPdatesort())) {
				dos.add(new SolrQueryBO().setSortField(true).setfN("publishTime").setSort(ORDER.asc));	
			}else{
				dos.add(new SolrQueryBO().setSortField(true).setfN("publishTime").setSort(ORDER.desc));
			}
		}
		if (!StringUtils.isBlank(announcementParam.getSdatesort())) {
			if ("0".equals(announcementParam.getSdatesort())) {
				dos.add(new SolrQueryBO().setSortField(true).setfN("registerEndDate").setSort(ORDER.asc));	
			}else{
				dos.add(new SolrQueryBO().setSortField(true).setfN("registerEndDate").setSort(ORDER.desc));
			}
		}
		try {

			int rowNum = announcementParam.getRowNum();
			int pageSize = announcementParam.getPageSize();
			QueryResponse queryResponse = BaseUtil.getQueryResponse(announcementsSolr , dos, rowNum, pageSize);
						
			SolrDocumentList searchResult = queryResponse.getResults();
			
			resultMap.put("searchResult", BaseUtil.docListToVoList(searchResult,AnnouncementsVO.class));
			resultMap.put("totalRecordNum", searchResult.getNumFound());
			
			logger.info(MessageFormat.format("----------------本次搜索：关键字“{0}”,结果行数：“{1}”----------------", announcementParam,searchResult.getNumFound()));
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

