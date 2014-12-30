package com.mysoft.b2b.search.provider.weixin;

import com.mysoft.b2b.basicsystem.settings.api.DictionaryService;
import com.mysoft.b2b.basicsystem.settings.api.Region;
import com.mysoft.b2b.bizsupport.api.BasicCategory;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.search.api.SearchRecordService;
import com.mysoft.b2b.search.api.weixin.SupplierSearchForWeixinService;
import com.mysoft.b2b.search.param.SearchLocation;
import com.mysoft.b2b.search.param.SearchSource;
import com.mysoft.b2b.search.param.SupplierParam;
import com.mysoft.b2b.search.solr.SolrQueryBO;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.util.BaseUtil;
import com.mysoft.b2b.search.vo.SupplierVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service("supplierSearchForWeixinService")
public class SupplierSearchForWeixinServiceImpl implements SupplierSearchForWeixinService {   

	private Logger logger =  Logger.getLogger(this.getClass());
	
	@Autowired
	private SolrQueryEnhanced supplierSolr;
	
	@Autowired
	private SearchRecordService searchRecordService;

	@Autowired
	private DictionaryService dictionaryService;
	
	@Autowired
	private OperationCategoryService operationCategoryService;
	
	@Autowired
	private JdbcTemplate jdbcCompany;

	private static final String  LOG_MSG = "供应商微信搜索";

	/**
	 * 供应商查询
	 */
	public Map<String, Object> getSupplierSearchResult(SupplierParam supplierParam) {
		Map<String,Object> resultMap = new HashMap<String, Object>();		
		List<SolrQueryBO> dos = new ArrayList<SolrQueryBO>();		
		
		// 关键字分词列表
		Set<String> analysisWords = new HashSet<String>();
		// 根据关键字查询
		if (!StringUtils.isBlank(supplierParam.getKeyword())) {
			
			try {
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
			
			String word  = "(" + StringUtils.join(analysisWords.toArray(), " AND ") + ")";
						
			dos.add(new SolrQueryBO().setCustomQueryStr(word).setQueryField(true));		
			
			Map<String, String[]> paramMap = new HashMap<String, String[]>();
			paramMap.put("defType", new String []{"edismax"});
			paramMap.put("qf", new String []{"companyName shortName businessScope qualificationLevelName projectLocation productName"});
			paramMap.put("pf", new String []{"companyName shortName businessScope qualificationLevelName projectLocation productName"});
			
			SolrParams solrParams = new MultiMapSolrParams(paramMap);
			dos.add(new SolrQueryBO().setSolrParams(solrParams));
			
		}else{			
			// 没有关键字，查询全部
			dos.add(new SolrQueryBO().setfN("*").setfV("*").setQueryField(true));
		}	
		
		// 省份查询
		if (!StringUtils.isBlank(supplierParam.getProvince())) {
			Region currentRegion = dictionaryService.getRegionByCode(supplierParam.getProvince());
			if (currentRegion != null) {
				String location = "china OR " + currentRegion.getCode();
				dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("projectLocationId").setfV("(" + location + ")"));
				
				// 根据地区打分排序
				String bfStr = "if(exists(query({!v='projectLocationId:" + currentRegion.getCode() + "'})),sum(10,sortScore),if(exists(query({!v='projectLocationId:china'})),sum(5,scale(sortScore,0.01,0.99)),1))";
				Map<String, String[]> paramMap = new HashMap<String, String[]>();
				paramMap.put("defType", new String []{"edismax"});
				paramMap.put("bf", new String []{bfStr});
				
				SolrParams solrParams = new MultiMapSolrParams(paramMap);
				dos.add(new SolrQueryBO().setSolrParams(solrParams));
				
			}
			
		}else{
            String scoreExpress = "_val_:\"sum(supplierSort(supplierId|string),supplierSort(sortScore|double))\"";
            dos.add(new SolrQueryBO().setCustomQueryStr(scoreExpress).setQueryField(true));
		}
		
		// 一级分类code查询
		if (!StringUtils.isBlank(supplierParam.getCodelevel1())) {
			dos.add(new SolrQueryBO().setFilterQueryField(true).setfN("operationCategoryCode1").setfV(supplierParam.getCodelevel1()));
		}
				
		try {
			int rowNum = supplierParam.getRowNum();
			int pageSize = supplierParam.getPageSize();
			QueryResponse queryResponse = BaseUtil.getQueryResponse(supplierSolr,dos , rowNum,pageSize);
			SolrDocumentList searchResult = queryResponse.getResults();							
			//设置供应商背书信息
			setSupplierEndorsement(searchResult);
			resultMap.put("searchResult", BaseUtil.docListToVoList(searchResult,SupplierVO.class));
			resultMap.put("totalRecordNum", searchResult.getNumFound());
						
			//添加搜索记录
			if (!StringUtils.isBlank(supplierParam.getKeyword()) && StringUtils.isBlank(supplierParam.getCodelevel1()) 
					&& StringUtils.isBlank(supplierParam.getArea()) && StringUtils.isBlank(supplierParam.getProvince())
					&& "1".equals(supplierParam.getPage())) {
				supplierParam.setSearchSource(SearchSource.WEIXIN);
				searchRecordService.execAddSearchRecord(supplierParam, analysisWords, searchResult.getNumFound());
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

	/**
	 * 供应商统计信息
	 */
	public Map<String, Object> getSupplierStat() {
		Map<String,Object> resultMap = new HashMap<String, Object>();		
		List<SolrQueryBO> dos = new ArrayList<SolrQueryBO>();

		dos.add(new SolrQueryBO().setfN("*").setfV("*").setQueryField(true));
		dos.add(new SolrQueryBO().setfN("operationCategoryCode1").setFacetField(true));	
		dos.add(new SolrQueryBO().setfN("projectArea").setFacetField(true));	
				
		try {
			QueryResponse queryResponse = BaseUtil.getQueryResponse(supplierSolr , dos, 0, 0);
			List<FacetField> facetFields = queryResponse.getFacetFields();
			List<Count> levelCode1FacetResult = new ArrayList<Count>();
			List<Count> areaFacetResult = new ArrayList<Count>();
			
			// 得到facet的统计结果
			for (FacetField facetField : facetFields) {
				if ("operationCategoryCode1".equals(facetField.getName())) {
					levelCode1FacetResult = facetField.getValues();
				}
				if ("projectArea".equals(facetField.getName())) {
					areaFacetResult = facetField.getValues();
				}		
			}
			
			
			List<String> level1Codes = new ArrayList<String>();
			Map<String, Long> level1CodesMap = new HashMap<String, Long>();
			for (Count lvl1 : levelCode1FacetResult) {
				level1Codes.add(lvl1.getName());
				level1CodesMap.put(lvl1.getName(), lvl1.getCount());
			}
			
			List<Map<String,Object>> levelCode1Result = new ArrayList<Map<String,Object>>();			
			List<BasicCategory> operationCategories = operationCategoryService.getCategoriesByCodes(DataType.SUPPLIER, level1Codes);
			for (BasicCategory basicCategory : operationCategories) {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("categoryCode", basicCategory.getCategoryCode());
				map.put("cagetoryName", basicCategory.getCategoryName());
				map.put("statCount", level1CodesMap.get(basicCategory.getCategoryCode()));
				levelCode1Result.add(map);
			}
			
			resultMap.put("category", levelCode1Result);
			
			List<Map<String,Object>> areaResult = new ArrayList<Map<String,Object>>();
			
			for (Count count : areaFacetResult) {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("areaCode", count.getName());
				map.put("areaName", SearchLocation.getLocationNameByCode(count.getName()));
				map.put("areaStat", count.getCount());
				List<Region> childRegionList = dictionaryService.getRegionsByArea(count.getName());
				if (childRegionList != null && !childRegionList.isEmpty()) {
					List<Map<String,Object>> childList = new ArrayList<Map<String,Object>>();
					for (Region region : childRegionList) {
						Map<String,Object> childMap = new HashMap<String, Object>();
						childMap.put("provinceCode", region.getCode());
						childMap.put("provinceName", region.getName());
						childList.add(childMap);
					}
					map.put("childProvinceList", childList);
				}
				
				areaResult.add(map);
			}
			
			resultMap.put("area", areaResult);

		} catch (SolrServerException e) {
			logger.error(LOG_MSG + "SolrServerException：",e);
        } catch (Exception e) {
            logger.error(LOG_MSG + "获取供应商统计错误：",e);
        }
		return resultMap;
	}

	//设置供应商背书信息
	private SolrDocumentList setSupplierEndorsement(SolrDocumentList documentList){
		if (CollectionUtils.isEmpty(documentList)) {
			return documentList;
		}
		
		for (SolrDocument solrDocument : documentList) {
			String supplierId = ObjectUtils.toString(solrDocument.get("supplierId"));
			solrDocument.addField("endorsementList", getSupplierEndorsementByDB(supplierId ));
		}
		return documentList;
	}
	// 从数据库里查询供应商背书信息
	private List<String> getSupplierEndorsementByDB(String supplierId){
		String sql = "select name from uuc_supplier_endorsement where supplier_id = ? order by created_time desc ";
		return jdbcCompany.queryForList(sql,String.class,supplierId);
	}
}

