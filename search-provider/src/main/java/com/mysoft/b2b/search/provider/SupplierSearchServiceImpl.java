package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.basicsystem.settings.api.DictionaryService;
import com.mysoft.b2b.basicsystem.settings.api.Region;
import com.mysoft.b2b.bizsupport.api.*;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.search.api.SearchRecordService;
import com.mysoft.b2b.search.api.SupplierSearchService;
import com.mysoft.b2b.search.param.SupplierParam;
import com.mysoft.b2b.search.solr.SolrQueryBO;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.util.BaseUtil;
import com.mysoft.b2b.search.vo.SupplierVO;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;

/**
 * SupplierSearchService接口的实现类,提供供应商搜索相关服务
 * @author ganq
 *
 */
@Service("supplierSearchService")
public class SupplierSearchServiceImpl implements SupplierSearchService {
	private Logger logger =  Logger.getLogger(this.getClass());
	@Autowired
	private SolrQueryEnhanced supplierSolr;

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private OperationCategoryService operationCategoryService;
	
	@Autowired
	private QualificationService qualificationService;
	
	@Autowired
	private QualificationLevelService qualificationLevelService;
	
	@Autowired
	private SearchRecordService searchRecordService;
	
	@Autowired
	private JdbcTemplate jdbcSearch;
		
	private static final String  LOG_MSG = "供应商搜索";
	
	/**
	 * 获取供应商搜索结果
	 * @param supplierParam 查询参数
	 * @return List<AnnouncementsVO>
	 * 
	 */
	public Map<String,Object> getSearchResult(SupplierParam supplierParam){
		
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
		if (!StringUtils.isBlank(supplierParam.getKeyword())) {
			
			try {
				//analysisWords = BaseUtil.mergeAdjacentString(new ArrayList<String>(BaseUtil.getAnalysisWord(supplierSolr.getSolrServer(),supplierParam.getKeyword())));
                analysisWords = BaseUtil.getAnalysisWord(supplierSolr.getSolrServer(),supplierParam.getKeyword());
            } catch (IOException e) {
                logger.error(LOG_MSG + "IOException", e);
                analysisWords.add(supplierParam.getKeyword());
            } catch (SolrServerException e) {
                logger.error(LOG_MSG + "SolrServerException", e);
                analysisWords.add(supplierParam.getKeyword());
            } catch (Exception e) {
                logger.error(LOG_MSG + "分词错误", e);
                analysisWords.add(supplierParam.getKeyword());
            }
			
			word  = "(" + StringUtils.join(analysisWords.toArray(), " AND ") + ")";
			dos.add(new SolrQueryBO().setfN("companyName").setHighlightField(true));
			dos.add(new SolrQueryBO().setfN("businessScope").setHighlightField(true));
			dos.add(new SolrQueryBO().setfN("qualificationLevelName").setHighlightField(true).setHighlightPreserveMulti(true));
			dos.add(new SolrQueryBO().setfN("projectLocation").setHighlightField(true).setHighlightPreserveMulti(true));
			dos.add(new SolrQueryBO().setfN("productName").setHighlightField(true).setHighlightPreserveMulti(true));
			
						
			dos.add(new SolrQueryBO().setCustomQueryStr(word).setQueryField(true));
			dos2.add(dos.get(dos.size()-1));
			dos3.add(dos.get(dos.size()-1));
			
			Map<String, String[]> paramMap = new HashMap<String, String[]>();
			paramMap.put("defType", new String []{"edismax"});
			paramMap.put("qf", new String []{"companyName shortName businessScope qualificationLevelName projectLocation productName companyNameSearchField"});
			paramMap.put("pf", new String []{"companyName shortName businessScope qualificationLevelName projectLocation productName companyNameSearchField"});
			
			SolrParams solrParams = new MultiMapSolrParams(paramMap);
			dos.add(new SolrQueryBO().setSolrParams(solrParams));
			
			dos2.add(dos.get(dos.size()-1));
			dos3.add(dos.get(dos.size()-1));
			
			dos2.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode1"));
			dos2.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode2"));
			dos3.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode3"));

			//dos3.add(new SolrQueryBO().setFacetField(true).setfN("qualificationCode_Level"));
		}else{
			dos3.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode3"));
			//dos3.add(new SolrQueryBO().setFacetField(true).setfN("qualificationCode_Level"));
			
			// 没有关键字，查询全部
			dos.add(new SolrQueryBO().setfN("*").setfV("*").setQueryField(true));
			dos2.add(dos.get(dos.size()-1));
			dos3.add(dos.get(dos.size()-1));
		}
		// 项目所在地查询
		if (!StringUtils.isBlank(supplierParam.getLocation())) {
			Region currentRegion = dictionaryService.getRegionByCode(supplierParam.getLocation());
			String location = "china";
			if (currentRegion != null) {
				location += " OR " + currentRegion.getCode() +" OR "+currentRegion.getParentCode();
			}
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("projectLocationId").setfV("(" + location + ")"));
			
			// 根据地区打分排序
            String bfStr = "";
			if (currentRegion != null) {
				if (!StringUtils.isBlank(currentRegion.getParentCode())) {
					bfStr = "if(exists(query({!v='projectLocationId:" + currentRegion.getCode() + "'})),sum(10,sortScore),if(exists(query({!v='projectLocationId:" + currentRegion.getParentCode() + "'})),sum(5,scale(sortScore,0.01,0.99)),if(exists(query({!v='projectLocationId:china'})),sum(1,scale(sortScore,0.01,0.99)),0.7)))";
				}else{
					bfStr = "if(exists(query({!v='projectLocationId:" + currentRegion.getCode() + "'})),sum(10,sortScore),if(exists(query({!v='projectLocationId:china'})),sum(5,scale(sortScore,0.01,0.99)),1))";
				}
			}
            // 选择全国
			if("china".equals(supplierParam.getLocation())){
                bfStr = "sum(0,sortScore)";
            }
            Map<String, String[]> paramMap = new HashMap<String, String[]>();
            paramMap.put("defType", new String []{"edismax"});
            paramMap.put("bf", new String []{bfStr});

            SolrParams solrParams = new MultiMapSolrParams(paramMap);
            dos.add(new SolrQueryBO().setSolrParams(solrParams));
		}else{

            String scoreExpress = "_val_:\"sum(supplierSort(supplierId|string),supplierSort(sortScore|double))\"";
            dos.add(new SolrQueryBO().setCustomQueryStr(scoreExpress).setQueryField(true));
		}
		
		// 一级分类code查询
		if (!StringUtils.isBlank(supplierParam.getCodelevel1())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode1").setfV(supplierParam.getCodelevel1()));
			dos3.add(dos.get(dos.size()-1));
		}
		// 二级分类code查询
		if (!StringUtils.isBlank(supplierParam.getCodelevel2())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode2").setfV(supplierParam.getCodelevel2()));
			dos3.add(dos.get(dos.size()-1));
		}
		// 三级分类code查询
		if (!StringUtils.isBlank(supplierParam.getCodelevel3())) {
			String codellvl3Value;
			if (supplierParam.getCodelevel3().contains(",")) {
				String [] fccodeArray = supplierParam.getCodelevel3().split(",");
				codellvl3Value = "(" + StringUtils.join(fccodeArray, " OR ") + ")";
			}else{
				codellvl3Value = supplierParam.getCodelevel3();
			}
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode3").setfV(codellvl3Value));
		}
		
		// 注册资金(查询转换人民币汇率后)
		if (!StringUtils.isBlank(supplierParam.getRegisteredcapital()) && NumberUtils.isNumber(supplierParam.getRegisteredcapital())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("regCapitalExchange").setfV("[" + supplierParam.getRegisteredcapital() + " TO " + "*]"));
		}
		
	
		
		// 资质
		if (!StringUtils.isBlank(supplierParam.getQualification())) {
			
			// 带等级查询
			if (!StringUtils.isBlank(supplierParam.getQualificationLevel())) {
				QualificationLevel qualLevel = qualificationLevelService.getQualificationLevelByCode(supplierParam.getQualificationLevel());
				if (qualLevel != null) {
					//动态字段
					dos.add(new SolrQueryBO().setFilterQueryField(true).
							setCustomQueryStr("qualification_"+supplierParam.getQualification()+":" + "[" + qualLevel.getPriority() + " TO *]"));
						
				}
				
			}else{
				// 直接查询资质
				dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("qualificationCode").setfV(supplierParam.getQualification()));
			}
		}
		
		// 成立年限不低于
		if (!StringUtils.isBlank(supplierParam.getYear()) && NumberUtils.isNumber(supplierParam.getYear())) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int year = calendar.get(Calendar.YEAR) - NumberUtils.toInt(supplierParam.getYear());
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("establishYear").setfV("[* TO " + year + "]"));
		}
		
		// 点击注册资金排序
		if (!StringUtils.isBlank(supplierParam.getRegsort())) {
			SolrQueryBO do7 = new SolrQueryBO();
			do7.setSortField(true).setfN("regCapital");
			if ("0".equals(supplierParam.getRegsort())) {;
				do7.setSort(ORDER.asc);
			}else{
				do7.setSort(ORDER.desc);
			}
			dos.add(do7);
								
		}
		
		// 按成立年限排序
		if (!StringUtils.isBlank(supplierParam.getYearsort())) {
			SolrQueryBO do7 = new SolrQueryBO();
			do7.setSortField(true).setfN("establishYear");
			if ("0".equals(supplierParam.getYearsort())) {
				do7.setSort(ORDER.asc);
			}else{
				do7.setSort(ORDER.desc);
			}
			dos.add(do7);
								
		}
		
		try {

			int rowNum = supplierParam.getRowNum();
			int pageSize = supplierParam.getPageSize();
			QueryResponse queryResponse = BaseUtil.getQueryResponse(supplierSolr,dos , rowNum,pageSize);
			QueryResponse queryResponse2 = BaseUtil.getQueryResponse(supplierSolr , dos2, 0, 0);
			QueryResponse queryResponse3 = BaseUtil.getQueryResponse(supplierSolr , dos3, 0, 0);
			SolrDocumentList searchResult = queryResponse.getResults();				
			
			// 没有关键字则查询全部运营分类，否则根据搜索结果反向匹配
			if (!StringUtils.isBlank(supplierParam.getKeyword())) {
				resultMap.put("relatedCategory", BaseUtil.getResultCategory(DataType.SUPPLIER,operationCategoryService,queryResponse2.getFacetFields()));
			}else{
				resultMap.put("relatedCategory", BaseUtil.getOperationCategoryList(DataType.SUPPLIER, operationCategoryService));
			}
			
			
			if (!StringUtils.isBlank(supplierParam.getCodelevel1()) || !StringUtils.isBlank(supplierParam.getCodelevel2()) 
					|| !StringUtils.isBlank(supplierParam.getCodelevel3()) || !StringUtils.isBlank(supplierParam.getKeyword())){
			// 有关键字查询或者用二级分类查询时，三级分类显示
			
				resultMap.put("level3Category", BaseUtil.getResultLvl3Category(operationCategoryService,
						queryResponse3.getFacetFields(),DataType.SUPPLIER,supplierParam.getCodelevel2(),supplierParam.getCodelevel1()));
			}
			// 如果有三级分类或者二级分类查询，需要得到他下属的资质
			if (!StringUtils.isBlank(supplierParam.getCodelevel3()) || !StringUtils.isBlank(supplierParam.getCodelevel2())) {
				String [] codeLevel3Array ;
				// 只要有三级分类就查询三级分类下的资质，否则查询二级分类下的资质
				if (StringUtils.isBlank(supplierParam.getCodelevel3())) {
					codeLevel3Array = new String [] {supplierParam.getCodelevel2()};
				}else{
					codeLevel3Array = supplierParam.getCodelevel3().split(",");
				}
				if (!ArrayUtils.isEmpty(codeLevel3Array)) {
					List<Qualification> qualifications = new ArrayList<Qualification>();
					for (String code3 : codeLevel3Array) {
						BasicCategory level3CategoryNode = operationCategoryService.getCategoryByCode(DataType.SUPPLIER, code3);
						if (level3CategoryNode == null) {
							continue;
						}
						SupplierOperationCategory supplierOperationCategory = (SupplierOperationCategory)level3CategoryNode;
						List<String> basicCategoryIds = supplierOperationCategory.getBindBasicCategoryIds();
						if (basicCategoryIds != null) {
							for (String basicId : basicCategoryIds) {
								qualifications.addAll(qualificationService.getQualificationsByCategoryCode(basicId));
							}
						}
						qualifications = BaseUtil.mergeBidQualification(qualifications);
						
						resultMap.put("relatedQualification", qualifications);
					}
				}
				
			}
			// 如果选择资质查询，将其下的资质等级查询出来
			if(!StringUtils.isBlank(supplierParam.getQualification())){
				List<QualificationLevel> qualificationLevels = qualificationLevelService.getLevelsByQualificationCode(supplierParam.getQualification());
				Collections.reverse(qualificationLevels);
				resultMap.put("relatedQualificationLevel", qualificationLevels);
			}
			
			// 设置名称高亮
			BaseUtil.setHighlightText(queryResponse, "supplierId", "companyName",word, false);
			BaseUtil.setHighlightText(queryResponse, "supplierId", "businessScope",word,false);
			BaseUtil.setHighlightText(queryResponse, "supplierId", "qualificationLevelName",word,true);
			BaseUtil.setHighlightText(queryResponse, "supplierId", "projectLocation",word,true);
			BaseUtil.setHighlightText(queryResponse, "supplierId", "productName",word,true);
			
			resultMap.put("searchResult", BaseUtil.docListToVoList(searchResult,SupplierVO.class));
			resultMap.put("totalRecordNum", searchResult.getNumFound());
			
			//搜索记录小于一页推荐 相近词
			if (searchResult.getNumFound() < pageSize && "1".equals(supplierParam.getPage())) {
				resultMap.put("recommendWords",getRecommendWords(getAllSubstr(supplierParam.getKeyword()),supplierParam.getKeyword()));
			}
			 
			//添加搜索记录
			if (!StringUtils.isBlank(supplierParam.getKeyword()) && StringUtils.isBlank(supplierParam.getCodelevel1()) 
					&& StringUtils.isBlank(supplierParam.getCodelevel2()) && StringUtils.isBlank(supplierParam.getCodelevel3())
					&& StringUtils.isBlank(supplierParam.getLocation()) && StringUtils.isBlank(supplierParam.getRegisteredcapital())
					&& StringUtils.isBlank(supplierParam.getYear()) && StringUtils.isBlank(supplierParam.getRegsort())
					&& StringUtils.isBlank(supplierParam.getQualification()) && StringUtils.isBlank(supplierParam.getQualificationLevel())
					&& StringUtils.isBlank(supplierParam.getYearsort()) && "1".equals(supplierParam.getPage())) {
				searchRecordService.execAddSearchRecord(supplierParam, analysisWords, searchResult.getNumFound());
			}
			logger.info("---------------------拆分搜索词:" + analysisWords + "---------------------------");
			logger.info(MessageFormat.format("----------------本次搜索：搜索参数“{0}”,结果行数：“{1}”----------------", supplierParam,searchResult.getNumFound()));

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

	//得到所有正向子串
	private Set<String> getAllSubstr(String keyword){
		if (StringUtils.isBlank(keyword)) {
			return null;
		}
		Set<String> substrSet = new HashSet<String>();
		int len = keyword.length();
		for (int i = 0; i < len-1; i++) {
			for (int j = i+2; j <= len; j++) {
				substrSet.add(keyword.substring(i,j));	
			}
		}
		return substrSet;
	}
	
	//获取推荐相近词
	private List<String> getRecommendWords(Set<String> words,String keyword){
		if (CollectionUtils.isEmpty(words)) {
			return null;
		}
		
		String sqlItem = StringUtils.repeat("?", ",", words.size());
		String sql = "select word from related_words_dictionary "
				+ "where category in "
				+ "(select category from related_words_dictionary where word in (" + sqlItem + "))  "
				+ "and word  != ? "
				+ "and result_count >  0 "
				+ "order by result_count desc";
		List<String> sqlValues = new ArrayList<String>(words);
        sqlValues.add(keyword);
		return jdbcSearch.queryForList(sql, String.class,sqlValues.toArray());
	}
	
	/**
	 * 获取需要推送的供应商id列表
	 */
	@SuppressWarnings("unchecked")
	public List<String> getPushSupplierIds(Map<String, Object> paramMap) {
		if (paramMap == null) {
			logger.info("--------获取推送供应商id失败!错误：参数列表为null");
			return null;
		}
		List<String> category  = (List<String>) paramMap.get("category");
		List<Map<String,String>> qualify = (List<Map<String, String>>) paramMap.get("qualify");
		List<String> serviceArea  = (List<String>)(paramMap.get("serviceArea"));
		int registerFund = NumberUtils.toInt(ObjectUtils.toString(paramMap.get("registerFund")));
		int buildYears = NumberUtils.toInt(ObjectUtils.toString(paramMap.get("buildYears")));
		int caseNum = NumberUtils.toInt(ObjectUtils.toString(paramMap.get("caseNum")));
		
		List<SolrQueryBO> dos = new ArrayList<SolrQueryBO>();
		
		//查询服务分类（满足其中一个即可）
		if (!CollectionUtils.isEmpty(category)) {
			dos.add(new SolrQueryBO().setQueryField(true).setfN("basicCategoryCode3").setfV("("+StringUtils.join(category," OR ")+")"));
		}
		//查询资质（满足其中一个即可）
		if (!CollectionUtils.isEmpty(qualify)) {
			List<String> qualQueryStr = new ArrayList<String>();
			for (Map<String,String> qual : qualify) {
				String qualCode = qual.get("qualCode");
				String priority = qual.get("priority");
				if (!StringUtils.isBlank(qualCode) && !StringUtils.isBlank(priority)) {
					qualQueryStr.add("qualification_" + qualCode + ":" + "[" + priority + " TO *]");
				}
			}
			if (!qualQueryStr.isEmpty()) {
				dos.add(new SolrQueryBO().setQueryField(true).setCustomQueryStr("(" + StringUtils.join(qualQueryStr," OR ") + ")"));	
			}
		}
		//查询服务区域（须带上“全国”的数据）
		if (!CollectionUtils.isEmpty(serviceArea)) {
			dos.add(new SolrQueryBO().setQueryField(true).setfN("projectLocationId").setfV("(" + StringUtils.join(serviceArea," OR ") + " OR china)"));
		}
		// 注册资本
		if (registerFund > 0) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("regCapitalExchange").setfV("[" + registerFund + " TO " + "*]"));
		}
		// 成立年限
		if (buildYears > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int year = calendar.get(Calendar.YEAR) - buildYears;
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("establishYear").setfV("[* TO " + year + "]"));
		}
		// 案例数量
		if (caseNum > 0) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("projectCount").setfV("[" + caseNum + " TO " + "*]"));
		}
		
		//设置只返回 supplierId
		Map<String, String> pMap = new HashMap<String, String>();
		paramMap.put("fl", "supplierId");
		SolrParams solrParams = new MapSolrParams(pMap);
		dos.add(new SolrQueryBO().setSolrParams(solrParams));
		
		int page = NumberUtils.toInt(ObjectUtils.toString(paramMap.get("page")));
		int pageSize = NumberUtils.toInt(ObjectUtils.toString(paramMap.get("pageSize")));
		int rowNum = (page-1) * pageSize;
		List<String> supplierIds = new ArrayList<String>();
		try{	
			QueryResponse queryResponse = BaseUtil.getQueryResponse(supplierSolr,dos , rowNum,pageSize);
			SolrDocumentList searchResult = queryResponse.getResults();				
			if (CollectionUtils.isEmpty(searchResult)) {
				return supplierIds;
			}
			
			// 得到supplierId列表
			for (SolrDocument solrDocument : searchResult) {
				supplierIds.add(ObjectUtils.toString(solrDocument.get("supplierId")));
			}
			logger.info(MessageFormat.format("----------------本次供应商推送搜索：搜索参数“{0}”,结果行数：“{1}”----------------", paramMap,searchResult.getNumFound()));

		} catch (SolrServerException e) {
            logger.error(LOG_MSG + "SolrServerException ", e);
        } catch (Exception e) {
            logger.error(LOG_MSG + "推送查询错误", e);
        }
        return supplierIds;
	}


}
