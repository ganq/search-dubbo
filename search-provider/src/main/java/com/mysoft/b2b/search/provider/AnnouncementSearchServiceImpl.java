package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.bizsupport.api.OperationCategoryService;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.bizsupport.api.QualificationLevel;
import com.mysoft.b2b.bizsupport.api.QualificationLevelService;
import com.mysoft.b2b.bizsupport.api.QualificationService;
import com.mysoft.b2b.search.api.AnnouncementSearchService;
import com.mysoft.b2b.search.api.SearchRecordService;
import com.mysoft.b2b.search.param.AnnouncementParam;
import com.mysoft.b2b.search.solr.SolrQueryBO;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.util.BaseUtil;
import com.mysoft.b2b.search.vo.AnnouncementsVO;
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
 * AnnouncementSearchService接口的实现类,提供招标预告搜索相关服务
 * @author ganq
 *
 */
@Service("announcementSearchService")
public class AnnouncementSearchServiceImpl implements AnnouncementSearchService {   

	private Logger logger =  Logger.getLogger(this.getClass());
	
	@Autowired
	private SolrQueryEnhanced announcementsSolr;

	@Autowired
	private OperationCategoryService operationCategoryService;
	
	@Autowired
	private QualificationService qualificationService;
	
	@Autowired
	private QualificationLevelService qualificationLevelService;
	
	@Autowired
	private SearchRecordService searchRecordService;

	
	private static final String  LOG_MSG = "招标预告搜索";
	/**
	 * 获取搜索结果
	 * @
	 */
	public Map<String,Object> getSearchResult(AnnouncementParam announcementParam) {
		
		
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
		if (!StringUtils.isBlank(announcementParam.getKeyword())) {
			
			try {
				analysisWords = BaseUtil.getAnalysisWord(announcementsSolr.getSolrServer(),announcementParam.getKeyword());

			} catch (IOException e) {
                logger.error(LOG_MSG + "IOException", e);
                analysisWords.add(announcementParam.getKeyword());
            } catch (SolrServerException e) {
                logger.error(LOG_MSG + "SolrServerException", e);
                analysisWords.add(announcementParam.getKeyword());
            } catch (Exception e) {
                logger.error(LOG_MSG + "分词错误", e);
                analysisWords.add(announcementParam.getKeyword());
            }
            word  = "(" + StringUtils.join(analysisWords.toArray(), " ") + ")";
			dos.add(new SolrQueryBO().setfN("title").setHighlightField(true));
			dos.add(new SolrQueryBO().setfN("biddingRange").setHighlightField(true));
			
			
			dos.add(new SolrQueryBO().setCustomQueryStr(word).setQueryField(true));
			
			dos2.add(dos.get(dos.size()-1));
			dos3.add(dos.get(dos.size()-1));
			
			
			Map<String, String[]> paramMap = new HashMap<String, String[]>();
			paramMap.put("defType", new String []{"edismax"});
			paramMap.put("qf", new String []{"title^10 developerName developerShortName biddingRange projectLocation"});
			paramMap.put("pf", new String []{"title^10 developerName developerShortName biddingRange projectLocation"});
			
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
			dos.add(BaseUtil.setBiddingBfSortBo());
			
		}
	
		// 项目所在地查询
		if (!StringUtils.isBlank(announcementParam.getLocation())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("projectLocation").setfV(announcementParam.getLocation()));
		}
		
		// 一级分类code查询
		if (!StringUtils.isBlank(announcementParam.getCodelevel1())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode1").setfV(announcementParam.getCodelevel1()));
			dos3.add(dos.get(dos.size()-1));
		}
		// 二级分类code查询
		if (!StringUtils.isBlank(announcementParam.getCodelevel2())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode2").setfV(announcementParam.getCodelevel2()));
			dos3.add(dos.get(dos.size()-1));
		}
		// 三级分类code查询
		if (!StringUtils.isBlank(announcementParam.getCodelevel3())) {
			String codellvl3Value;
			if (announcementParam.getCodelevel3().contains(",")) {
				String [] fccodeArray = announcementParam.getCodelevel3().split(",");
				codellvl3Value = "(" + StringUtils.join(fccodeArray, " OR ") + ")";
			}else{
				codellvl3Value = announcementParam.getCodelevel3();
			}
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode3").setfV(codellvl3Value));
		}
		// 状态  (x代表不限)
		if (!StringUtils.isBlank(announcementParam.getState()) && !"x".equals(announcementParam.getState())) {
			String [] stateArray = announcementParam.getState().split(",");
			for (int i=0;i<stateArray.length;i++) {
				stateArray[i] = NumberUtils.toInt(stateArray[i]) + "";
			}
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("state").setfV("("+StringUtils.join(stateArray," OR ")+")"));
		}
		// 注册资本不高于
		if (!StringUtils.isBlank(announcementParam.getRegcapital()) && NumberUtils.isNumber(announcementParam.getRegcapital())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("scRegCapital").setfV("[* TO "+announcementParam.getRegcapital() + "]"));
		}
		// 资质
		if (!StringUtils.isBlank(announcementParam.getQualification())) {
			
			// 带等级查询
			if (!StringUtils.isBlank(announcementParam.getQualificationLevel())) {
				QualificationLevel qualLevel = qualificationLevelService.getQualificationLevelByCode(announcementParam.getQualificationLevel());
				if (qualLevel != null) {
					//动态字段
					dos.add(new SolrQueryBO().setFilterQueryField(true).
							setCustomQueryStr("qualification_"+announcementParam.getQualification()+":" + "[* TO " + qualLevel.getPriority() + "]"));
						
				}
				
			}else{
				// 直接查询资质
				dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("scQualificationCode").setfV(announcementParam.getQualification()));
			}
		}
		//默认查询按报名截止时间升序数据
		/*if (StringUtils.isEmpty(announcementParam.getKeyword()) && StringUtils.isEmpty(announcementParam.getSdatesort())
				&& StringUtils.isEmpty(announcementParam.getPdatesort())) {
			announcementParam.setSdatesort("0");
			dos.add(new SolrQueryBO().setSortField(true).setfN("registerEndDate").setSort(ORDER.asc));
		}*/
		
		
		// 点击报名截止时间排序
		if (!StringUtils.isBlank(announcementParam.getSdatesort())) {
			SolrQueryBO do7 = new SolrQueryBO();
			do7.setSortField(true).setfN("registerEndDate");
			if ("0".equals(announcementParam.getSdatesort())) {
				do7.setSort(ORDER.asc);
			}else{
				do7.setSort(ORDER.desc);
			}
			dos.add(do7);
		}
		
		// 按发布时间排序
		if (!StringUtils.isBlank(announcementParam.getPdatesort())) {
			SolrQueryBO do7 = new SolrQueryBO();
			do7.setSortField(true).setfN("publishTime");
			if ("0".equals(announcementParam.getPdatesort())) {
				do7.setSort(ORDER.asc);
			}else{
				do7.setSort(ORDER.desc);
			}
			dos.add(do7);
								
		}
		
		try {
			int rowNum = announcementParam.getRowNum();
			int pageSize = announcementParam.getPageSize();
			QueryResponse queryResponse = BaseUtil.getQueryResponse(announcementsSolr , dos, rowNum, pageSize);
			QueryResponse queryResponse2 = BaseUtil.getQueryResponse(announcementsSolr , dos2, 0, 0);
			QueryResponse queryResponse3 = BaseUtil.getQueryResponse(announcementsSolr , dos3, 0, 0);
			
			SolrDocumentList searchResult = queryResponse.getResults();
			
			// 没有关键字则查询全部运营分类，否则根据搜索结果反向匹配
			if (!StringUtils.isBlank(announcementParam.getKeyword())) {
				resultMap.put("relatedCategory", BaseUtil.getResultCategory(DataType.BID,operationCategoryService,queryResponse2.getFacetFields()));
			}else{
				resultMap.put("relatedCategory", BaseUtil.getOperationCategoryList(DataType.BID, operationCategoryService));
			}
			
			// 有关键字查询或者用一，二级分类查询时，三级分类显示
			
			if (!StringUtils.isBlank(announcementParam.getCodelevel1()) || !StringUtils.isBlank(announcementParam.getCodelevel2()) 
					|| !StringUtils.isBlank(announcementParam.getCodelevel3()) || !StringUtils.isBlank(announcementParam.getKeyword())){
				resultMap.put("level3Category", BaseUtil.getResultLvl3Category(operationCategoryService,
						queryResponse3.getFacetFields(),DataType.BID,announcementParam.getCodelevel2(),announcementParam.getCodelevel1()));
			}
			// 如果有三级分类或者二级分类查询，需要得到他下属的资质
			/*if (!StringUtils.isBlank(announcementParam.getCodelevel3()) || !StringUtils.isBlank(announcementParam.getCodelevel2())) {
				
				String [] codeLevel3Array = null;
				// 只要有三级分类就查询三级分类下的资质，否则查询二级分类下的资质
				if (StringUtils.isBlank(announcementParam.getCodelevel3())) {
					codeLevel3Array = new String [] {announcementParam.getCodelevel2()};
				}else{
					codeLevel3Array = announcementParam.getCodelevel3().split(",");
				}
				if (!ArrayUtils.isEmpty(codeLevel3Array)) {
					List<Qualification> qualifications = new ArrayList<Qualification>();
					for (String code3 : codeLevel3Array) {
						BasicCategory level3CategoryNode = operationCategoryService.getCategoryByCode(DataType.BID, code3);
						if (level3CategoryNode == null) {
							continue;
						}
						BidOperationCategory bidOperationCategory = (BidOperationCategory)level3CategoryNode;
						List<String> basicCategoryIds = bidOperationCategory.getBindBasicCategoryIds();
						for (String basicId : basicCategoryIds) {
							qualifications.addAll(qualificationService.getQualificationsByCategoryCode(basicId));
						}
						qualifications = BaseUtil.mergeBidQualification(qualifications);
						
						resultMap.put("relatedQualification", qualifications);
					}
				}
				
			}
			
			// 如果选择资质查询，将其下的资质等级查询出来
			if(!StringUtils.isBlank(announcementParam.getQualification())){
				List<QualificationLevel> qualificationLevels = qualificationLevelService.getLevelsByQualificationCode(announcementParam.getQualification());
				// 资质等级列表默认从高到低，需要反过来
				Collections.reverse(qualificationLevels);
				resultMap.put("relatedQualificationLevel", qualificationLevels);
			}*/
			
			// 设置标题高亮
			BaseUtil.setHighlightText(queryResponse, "biddingId", "title",word,false);
			BaseUtil.setHighlightText(queryResponse, "biddingId", "biddingRange",word,false);
			
			resultMap.put("searchResult", BaseUtil.docListToVoList(searchResult, AnnouncementsVO.class));
			resultMap.put("totalRecordNum", searchResult.getNumFound());
			
			//添加搜索记录
			if (!StringUtils.isBlank(announcementParam.getKeyword()) && StringUtils.isBlank(announcementParam.getCodelevel1()) 
					&& StringUtils.isBlank(announcementParam.getCodelevel2()) && StringUtils.isBlank(announcementParam.getCodelevel3())
					&& StringUtils.isBlank(announcementParam.getLocation()) && StringUtils.isBlank(announcementParam.getState())
					&& StringUtils.isBlank(announcementParam.getRegcapital()) && StringUtils.isBlank(announcementParam.getSdatesort())
					&& StringUtils.isBlank(announcementParam.getPdatesort()) && "1".equals(announcementParam.getPage())) {

				searchRecordService.execAddSearchRecord(announcementParam, analysisWords, searchResult.getNumFound());
			}
			
			logger.info(MessageFormat.format("----------------本次搜索：搜索参数“{0}”,结果行数：“{1}”----------------", announcementParam,searchResult.getNumFound()));

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

	/**
	 * 获取通用搜索结果
	 */
	public List<AnnouncementsVO> getCommonSearchResult(AnnouncementParam announcementParam){		

		//dos 作为字段查询
		List<SolrQueryBO> dos = new ArrayList<SolrQueryBO>();
		
		//查询所有
		if (announcementParam.isQueryAll()) {
			dos.add(new SolrQueryBO().setfN("*").setfV("*").setQueryField(true));
		}else{

            // 从id
            if (!StringUtils.isBlank(announcementParam.getId())){
                dos.add(new SolrQueryBO().setQueryField(true).setfN("biddingId").setfV(announcementParam.getId()));
            }
			// 从关键字
			if (!StringUtils.isBlank(announcementParam.getKeyword())) {
				String word  = "(" + announcementParam.getKeyword().trim() + ")";
				dos.add(new SolrQueryBO().setQueryField(true).setfN("searchField").setfV(word));
			}
			// 从1级分类查
			if (!StringUtils.isBlank(announcementParam.getCodelevel1())) {
				dos.add(new SolrQueryBO().setQueryField(true).setfN("operationCategoryCode1").setfV(announcementParam.getCodelevel1()));
			}
			// 从2级分类查
			if (!StringUtils.isBlank(announcementParam.getCodelevel2())) {
				dos.add(new SolrQueryBO().setQueryField(true).setfN("operationCategoryCode2").setfV(announcementParam.getCodelevel2()));
			}
			// 从3级分类查
			if (!StringUtils.isBlank(announcementParam.getCodelevel3())) {
				dos.add(new SolrQueryBO().setQueryField(true).setfN("operationCategoryCode3").setfV(announcementParam.getCodelevel3()));
			}
			// 从运营分类查
			if (!StringUtils.isBlank(announcementParam.getCategorycode())) {
				dos.add(new SolrQueryBO().setQueryField(true).setfN("searchOperationCategoryCode").setfV(announcementParam.getCategorycode()));
			}				
			
			// 从开发商id查
			if (!StringUtils.isBlank(announcementParam.getDevid())) {
				dos.add(new SolrQueryBO().setQueryField(true).setfN("developerId").setfV(announcementParam.getDevid()));
			}				
			
			// 项目所在地条件查询
			if (!StringUtils.isBlank(announcementParam.getLocation())) {
				dos.add(new SolrQueryBO().setQueryField(true).setfN("projectLocationId").setfV(announcementParam.getLocation()));
			}
			
		}
		
		// 状态  
		if (!StringUtils.isBlank(announcementParam.getState())) {
			String [] stateArray = announcementParam.getState().split(",");
			for (int i=0;i<stateArray.length;i++) {
				stateArray[i] = NumberUtils.toInt(stateArray[i]) + "";
			}
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("state").setfV("("+StringUtils.join(stateArray," OR ")+")"));
		}
	
		// 点击报名截止时间排序
		if (!StringUtils.isBlank(announcementParam.getSdatesort())) {
			SolrQueryBO do7 = new SolrQueryBO();
			do7.setSortField(true).setfN("registerEndDate");
			if ("0".equals(announcementParam.getSdatesort())) {
				do7.setSort(ORDER.asc);
			}else{
				do7.setSort(ORDER.desc);
			}
			dos.add(do7);
		}
		
		// 点击发布时间排序
		if (!StringUtils.isBlank(announcementParam.getPdatesort())) {
			SolrQueryBO do7 = new SolrQueryBO();
			do7.setSortField(true).setfN("publishTime");
			if ("0".equals(announcementParam.getPdatesort())) {
				do7.setSort(ORDER.asc);
			}else{
				do7.setSort(ORDER.desc);
			}
			dos.add(do7);
		}
		
		dos.add(BaseUtil.setBiddingBfSortBo());
		
		try {
			QueryResponse queryResponse = BaseUtil.getQueryResponse(announcementsSolr , dos, announcementParam.getRowNum(), announcementParam.getPageSize());
			
			SolrDocumentList searchResult = queryResponse.getResults();
			logger.info(MessageFormat.format("----------------本次搜索：搜索参数“{0}”,结果行数：“{1}”----------------", announcementParam,searchResult.getNumFound()));
			return BaseUtil.docListToVoList(searchResult,AnnouncementsVO.class);

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
        return Collections.<AnnouncementsVO>emptyList();
				
	}

}

