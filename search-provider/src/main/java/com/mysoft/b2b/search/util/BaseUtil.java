package com.mysoft.b2b.search.util;

import com.mysoft.b2b.bizsupport.api.*;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.search.solr.SolrQueryBO;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.vo.SearchCategoryVO;
import com.mysoft.b2b.search.vo.SupplierVO;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.AnalysisPhase;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.TokenInfo;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.DateUtil;
import org.apache.solr.common.util.NamedList;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;

public class BaseUtil {

	private static Logger logger = Logger.getLogger(BaseUtil.class);

	/**
	 * solr文档集合转换成Vo集合
	 */
	public static <T> List<T> docListToVoList(SolrDocumentList solrDocumentList, Class<T> clz)
            throws IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            ClassNotFoundException, InstantiationException, ParseException {
		List<T> list = new ArrayList<T>();

		if (solrDocumentList == null || solrDocumentList.isEmpty()) {
			return list;
		}
		for (SolrDocument sd : solrDocumentList) {			
			list.add(solrDocToVo(sd, clz));
		}
		return list;
	}

	/**
	 * 单个solr文档转换成vo
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public static <T> T solrDocToVo(SolrDocument sd, Class<T> clz) throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, ParseException {
		T vo = (T) Class.forName(clz.getName()).newInstance();
		// 获取实体类的所有属性，返回Field数组
		Field[] fields = vo.getClass().getDeclaredFields();
		for (Field field : fields) {

			String name = field.getName();
			if (name.equals("serialVersionUID")) {
				continue;
			}
			Method setMethod = vo.getClass().getMethod("set" + UpperCaseField(name), field.getType());
			String fieldType = field.getGenericType().toString();

            
			if (fieldType.equals("class java.lang.String")) {
				setMethod.invoke(vo, ObjectUtils.toString(sd.getFieldValue(name)));
			}
			if (fieldType.equals("class java.util.Date")) {
				if (sd.getFieldValue(name) instanceof  Date) {
                    setMethod.invoke(vo, (Date) sd.getFieldValue(name));
                }

			}
			if (fieldType.equals("class java.lang.Integer")) {
				setMethod.invoke(vo, NumberUtils.toInt(ObjectUtils.toString(sd.getFieldValue(name))));
			}
			if (fieldType.equals("int")) {
				setMethod.invoke(vo, NumberUtils.toInt(ObjectUtils.toString(sd.getFieldValue(name))));
			}
			if (fieldType.equals("class java.lang.Boolean")) {
				setMethod.invoke(vo, BooleanUtils.toBoolean(ObjectUtils.toString(sd.getFieldValue(name))));
			}
			if (fieldType.equals("boolean")) {
				setMethod.invoke(vo, BooleanUtils.toBoolean(ObjectUtils.toString(sd.getFieldValue(name))));
			}
			if (fieldType.equals("class java.lang.Double")) {
				setMethod.invoke(vo, NumberUtils.toDouble(ObjectUtils.toString(sd.getFieldValue(name))));
			}
			if (fieldType.equals("interface java.util.List")) {
                if (sd.getFieldValue(name) instanceof List) {
                    setMethod.invoke(vo, (List) sd.getFieldValue(name));
                }
			}

		}
		return vo;
	}

	/**
	 * 转化字段首字母为大写
	 */
	public static String UpperCaseField(String fieldName) {
		fieldName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());
		return fieldName;
	}

	/**
	 * 基础分类集合转换为SearchCateogry VO对象
	 */
	public static List<SearchCategoryVO> basicCategoryToSearchCategoryVO(DataType dataType,List<BasicCategory> categories,
			Map<String, Long> codesMap) {
		List<SearchCategoryVO> searchCategoryVOs = new ArrayList<SearchCategoryVO>();
		if (categories == null || categories.isEmpty()) {
			return searchCategoryVOs;
		}

		for (BasicCategory basicCategory : categories) {
			SearchCategoryVO searchCategoryVO = new SearchCategoryVO();
			searchCategoryVO.setCode(basicCategory.getCategoryCode());
			searchCategoryVO.setName(basicCategory.getCategoryName());
			searchCategoryVO.setParentCode(basicCategory.getParentCode());
			searchCategoryVO.setCount(codesMap.get(basicCategory.getCategoryCode()).intValue());
			
			if (dataType == DataType.BID) {
				BidOperationCategory bidOperationCategory = (BidOperationCategory)basicCategory;
				if (bidOperationCategory.getSeoModel() != null) {
					searchCategoryVO.setPinyin(bidOperationCategory.getSeoModel().getDirectoryName());	
				}
				
			}
			if (dataType == DataType.SUPPLIER) {
				SupplierOperationCategory supplierOperationCategory = (SupplierOperationCategory)basicCategory;
				if (supplierOperationCategory.getSeoModel() != null) {
					searchCategoryVO.setPinyin(supplierOperationCategory.getSeoModel().getDirectoryName());	
				}
			}
			
			
			searchCategoryVOs.add(searchCategoryVO);
		}

		return searchCategoryVOs;

	}

	/**
	 * 获取全部1，2级运营分类
	 */
	public static List<SearchCategoryVO> getOperationCategoryList(DataType dataType,
			OperationCategoryService operationCategoryService) {
		List<OperationCategoryNode> categoryList = operationCategoryService.getCategoryRootHierarchy(dataType);
		List<SearchCategoryVO> categoryVOs = new ArrayList<SearchCategoryVO>();
		for (OperationCategoryNode operationCategoryNode : categoryList) {
			SearchCategoryVO searchCategoryVO = new SearchCategoryVO();
			searchCategoryVO.setCode(operationCategoryNode.getCategoryCode());
			searchCategoryVO.setName(operationCategoryNode.getCategoryName());
			if (dataType == DataType.BID) {
				BidOperationCategory bidOperationCategory = (BidOperationCategory)operationCategoryService.getCategoryByCode(dataType, operationCategoryNode.getCategoryCode());
				if (bidOperationCategory.getSeoModel() != null) {
					searchCategoryVO.setPinyin(bidOperationCategory.getSeoModel().getDirectoryName());	
				}
			}
			if (dataType == DataType.SUPPLIER) {
				SupplierOperationCategory supplierOperationCategory = (SupplierOperationCategory)operationCategoryService.getCategoryByCode(dataType, operationCategoryNode.getCategoryCode());
				if (supplierOperationCategory.getSeoModel() != null) {
					searchCategoryVO.setPinyin(supplierOperationCategory.getSeoModel().getDirectoryName());	
				}
			}
			List<SearchCategoryVO> childCategoryVOs = new ArrayList<SearchCategoryVO>();
			for (OperationCategoryNode childNode : operationCategoryNode.getChildrenCategoryNodes()) {
				SearchCategoryVO childCategoryVO = new SearchCategoryVO();
				childCategoryVO.setCode(childNode.getCategoryCode());
				childCategoryVO.setName(childNode.getCategoryName());
				childCategoryVO.setParentCode(searchCategoryVO.getCode());
				childCategoryVOs.add(childCategoryVO);
			}
			searchCategoryVO.setChildrenList(childCategoryVOs);
			categoryVOs.add(searchCategoryVO);
		}
		return categoryVOs;
	}

	/**
	 * 得到搜索结果的分类导航
	 */
	public static List<SearchCategoryVO> getResultCategory(DataType dataType,
			OperationCategoryService operationCategoryService, List<FacetField> facetFields) {
		List<Count> level1FacetResult = new ArrayList<Count>();
		List<Count> level2FacetResult = new ArrayList<Count>();
		// 得到facet的统计结果
		for (FacetField facetField : facetFields) {

			if ("operationCategoryCode1".equals(facetField.getName())) {
				level1FacetResult = facetField.getValues();
			}
			if ("operationCategoryCode2".equals(facetField.getName())) {
				level2FacetResult = facetField.getValues();
			}
		}

		List<String> level1Codes = new ArrayList<String>();
		List<String> level2Codes = new ArrayList<String>();

		Map<String, Long> level1CodesMap = new HashMap<String, Long>();
		Map<String, Long> level2CodesMap = new HashMap<String, Long>();
		for (Count lvl1 : level1FacetResult) {
			level1Codes.add(lvl1.getName());
			level1CodesMap.put(lvl1.getName(), lvl1.getCount());
		}
		for (Count lvl2 : level2FacetResult) {
			level2Codes.add(lvl2.getName());
			level2CodesMap.put(lvl2.getName(), lvl2.getCount());
		}

		List<SearchCategoryVO> level1BasicCategory = basicCategoryToSearchCategoryVO(dataType,
				operationCategoryService.getCategoriesByCodes(dataType, level1Codes), level1CodesMap);
		List<SearchCategoryVO> level2BasicCategory = basicCategoryToSearchCategoryVO(dataType,
				operationCategoryService.getCategoriesByCodes(dataType, level2Codes), level2CodesMap);

		for (SearchCategoryVO level1Category : level1BasicCategory) {

			// 判断下面的三级分类所在的二级分类
			List<SearchCategoryVO> level2Nodes = new ArrayList<SearchCategoryVO>();
			for (SearchCategoryVO level2Category : level2BasicCategory) {
				if (level1Category.getCode().equals(level2Category.getParentCode())) {
					level2Nodes.add(level2Category);
				}
			}
			Collections.sort(level2Nodes, new Comparator<SearchCategoryVO>() {
				public int compare(SearchCategoryVO o1, SearchCategoryVO o2) {
					return o1.getCount() > o2.getCount() ? -1 : 1;
				}
			});
			
			level1Category.setChildrenList(level2Nodes);
		}
		/*
		 * if (dataType == DataType.BID) { Iterator<SearchCategoryVO>
		 * level2Iterator = level2BasicCategory.iterator(); while
		 * (level2Iterator.hasNext()) { SearchCategoryVO bcn =
		 * level2Iterator.next(); if (bcn.getChildrenList() == null ||
		 * bcn.getChildrenList().isEmpty()) { level2Iterator.remove(); } } }
		 */
		Collections.sort(level1BasicCategory, new Comparator<SearchCategoryVO>() {
			public int compare(SearchCategoryVO o1, SearchCategoryVO o2) {
				return o1.getCount() > o2.getCount() ? -1 : 1;
			}
		});
		return level1BasicCategory;
	}

	/**
	 * 修正错误页码
	 */
	private static QueryResponse correctPageError(SolrQueryEnhanced solrServer, QueryResponse queryResponse,
			List<SolrQueryBO> dos, int rowNum, int pageSize) throws SolrServerException {
		SolrDocumentList searchResult = queryResponse.getResults();
		// 如果传入的页码超过最大页码，则修正为最大页码
		long numFound = searchResult.getNumFound();
		if (rowNum >= numFound && pageSize > 0) {
			int lastPage = NumberUtils.toInt("" + (numFound % pageSize == 0 ? numFound / pageSize : ((numFound / pageSize) + 1)));
			int newRowNum = (lastPage - 1) * pageSize;
			queryResponse = getQueryResponse(solrServer, dos, newRowNum, pageSize);
		}
		return queryResponse;
	}

	/**
	 * 得到solr查询原始结果
	 */
	public static QueryResponse getQueryResponse(SolrQueryEnhanced solrServer, List<SolrQueryBO> dos, int startIndex,
			int pageSize) throws SolrServerException {
		QueryResponse queryResponse = solrServer.query(dos, startIndex, pageSize);
		return correctPageError(solrServer, queryResponse, dos, startIndex, pageSize);
	}

	/**
	 * 获取solr terms 搜索建议结果
	 */
	public static Map<String, Long> getSuggestionTerms(QueryResponse queryResponse) {
		Map<String, Long> resultMap = new HashMap<String, Long>();

		TermsResponse termsResponse = queryResponse.getTermsResponse();
		if (termsResponse != null) {
			Map<String, List<TermsResponse.Term>> termsMap = termsResponse.getTermMap();
			for (Map.Entry<String, List<TermsResponse.Term>> termsEntry : termsMap.entrySet()) {

				List<TermsResponse.Term> termList = termsEntry.getValue();
				for (TermsResponse.Term term : termList) {
					resultMap.put(term.getTerm(), term.getFrequency());
				}
			}
		}
		return resultMap;
	}

	/**
	 * 得到pivot的facet
	 */
	public static List<Map<String, Object>> getPivotFacetResult(NamedList<List<PivotField>> pivotFields, String field) {

		if (pivotFields == null) {
			return null;
		}
		List<PivotField> fields = pivotFields.get(field);

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		if (fields == null) {
			return resultList;
		}
		for (PivotField pivotField : fields) {

			if (pivotField.getPivot() == null || pivotField.getPivot().size() != 1) {
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(pivotField.getField(), pivotField.getValue());
			PivotField dNamePivot = pivotField.getPivot().get(0);
			map.put(dNamePivot.getField(), dNamePivot.getValue());
			map.put("count", pivotField.getCount());

			resultList.add(map);
		}

		return resultList;
	}

	/**
	 * 设置高亮字段
	 */
	@SuppressWarnings("rawtypes")
	public static void setHighlightText(QueryResponse queryResponse, String primaryField, String textField,String queryStr, boolean isMultiValue) {
		
		// 单字高亮特殊处理
		queryStr = queryStr.replace("*", "").replace("(","").replace(")", "").trim().toLowerCase();
		if (queryStr.length() == 1) {
			setSingleKeywordHl(queryResponse,queryStr, textField, isMultiValue);
			return;
		}
		
		// 将高亮查询结果合并到搜索结果集中
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		SolrDocumentList searchResult = queryResponse.getResults();
		if (highlighting != null && highlighting.size() > 0) {

			for (SolrDocument solrDocument : searchResult) {
				Map<String, List<String>> hlMap = highlighting.get(solrDocument.getFieldValue(primaryField));
				if (hlMap == null) {
					continue;
				}
				List<String> highlightText = hlMap.get(textField);
				if (highlightText == null || highlighting.isEmpty()) {
					continue;
				}
				if (!isMultiValue) {
					solrDocument.setField(textField, highlightText.get(0));	
				}else{
					solrDocument.setField(textField, highlightText);
				}
				
			}
		}
	}

    public static void setHl(SolrDocumentList searchResult,String keyword,String... fieldName){
        if (searchResult == null || searchResult.isEmpty() ) {
            return;
        }

        String hlPre = "<em class=\"search_highlight\">";
        String hlPost = "</em>";
        for (String field : fieldName){
            for (SolrDocument solrDocument : searchResult){
                Object docment = solrDocument.get(field);
                if (docment instanceof List){
                    solrDocument.setField(field, setListFieldHl(docment, hlPre, hlPost, keyword.toLowerCase().toCharArray()));
                }else{
                    solrDocument.setField(field,setStringFieldHl(docment,hlPre,hlPost,keyword.toLowerCase().toCharArray()));
                }
            }
        }
    }

    private static String setStringFieldHl(Object string,String hlPre,String hlPost,char[] keyword){
        if (string == null || StringUtils.isBlank(string.toString())){
            return "";
        }
        String newStr = ObjectUtils.toString(string).trim();

        HashSet keywordSet = new HashSet(CollectionUtils.arrayToList(keyword));
        List<String> newStrList = new ArrayList<String>();
        for (char c : newStr.toCharArray()){
            if (keywordSet.contains(Character.toLowerCase(c))){
                newStrList.add(hlPre + CharUtils.toString(c) + hlPost);
            }else{
                newStrList.add(CharUtils.toString(c));
            }
        }

        return StringUtils.join(newStrList,"").replaceAll(hlPost + hlPre, "");
    }
    private static List setListFieldHl(Object list,String hlPre,String hlPost,char[] keyword){
        List newDocment = new ArrayList();
        if (list == null){
            return newDocment;
        }
        List newList = (List)list;
        if (CollectionUtils.isEmpty(newList)){
            return newDocment;
        }
        for(Object str : newList){
            newDocment.add(setStringFieldHl(str,hlPre,hlPost,keyword));
        }
        return newDocment;
    }
	
	/**
	 * 设置单字搜索时的高亮字段
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setSingleKeywordHl(QueryResponse queryResponse,String queryStr, String textField,boolean isMultiValue){
		SolrDocumentList searchResult = queryResponse.getResults();		
		String hlPre =  ObjectUtils.toString(((NamedList)queryResponse.getHeader().get("params")).get("hl.simple.pre"));
		String hlPost =  ObjectUtils.toString(((NamedList)queryResponse.getHeader().get("params")).get("hl.simple.post"));
		
		if (searchResult != null && !searchResult.isEmpty()) {
			for (SolrDocument solrDocument : searchResult) {
				if (isMultiValue) {
					List listDocment = (List)solrDocument.get(textField);
					if (listDocment == null) {
						continue;
					}
					List newDocment = new ArrayList();
					for (Object object : listDocment) {
						newDocment.add(ignoreCaseReplace(ObjectUtils.toString(object), queryStr, hlPre + "{0}" + hlPost));
					}
					solrDocument.setField(textField, newDocment);
				}else{
					Object textDocment = solrDocument.get(textField);
					
					solrDocument.setField(textField, ignoreCaseReplace(ObjectUtils.toString(textDocment), queryStr, hlPre + "{0}" + hlPost));
				}
			}
		}
	}

	/**
	 * 获取中文的搜索建议
	 */
	public static Map<String, Long> getChineseSuggest(SolrServer solrServer, String keyword, String field,
			int termsLimit) {
		Map<String, String[]> nameChineseParamMap = new HashMap<String, String[]>();
		nameChineseParamMap.put("terms.fl", new String[] { field });
		nameChineseParamMap.put("terms.regex", new String[] { ".*" +keyword + ".*" });
		nameChineseParamMap.put("terms.limit", new String[] { termsLimit + "" });
		SolrParams spNameChinese = new MultiMapSolrParams(nameChineseParamMap);
		QueryRequest qrNameChinese = new QueryRequest(spNameChinese);
		qrNameChinese.setPath("/terms");

		QueryResponse qrepNameChinese = new QueryResponse();
		try {
			qrepNameChinese = qrNameChinese.process(solrServer);

		} catch (SolrServerException e) {
            logger.error("获取" + field + "中文搜索建议错误 SolrServerException", e);
        } catch (Exception e) {
            logger.error("获取" + field + "中文搜索建议错误", e);
        }
        return getSuggestionTerms(qrepNameChinese);
	}

	/**
	 * 获取拼音的搜索建议
	 */
	public static Map<String, Long> getPinyinSuggest(SolrServer solrServer, String keyword, String field,
			String showField, int row) {
		Map<String, Long> namePinyinMap = new HashMap<String, Long>();
		SolrQuery sqNamePinyin = new SolrQuery();
		sqNamePinyin.setQuery(field + ":" + StringUtils.lowerCase(keyword) + "*").setRows(0).addFacetField(showField)
				.setFacetLimit(row).setFacetMinCount(1);
		QueryResponse qrepNamePinyin = new QueryResponse();
		try {
			qrepNamePinyin = solrServer.query(sqNamePinyin);
        } catch (SolrServerException e) {
            logger.error("获取" + field + "拼音搜索建议错误 SolrServerException", e);
        } catch (Exception e) {
            logger.error("获取" + field + "拼音搜索建议错误", e);
        }

		FacetField resultNamePinyin = qrepNamePinyin.getFacetField(showField);
		if (resultNamePinyin != null && resultNamePinyin.getValues() != null && !resultNamePinyin.getValues().isEmpty()) {
			for (Count value : resultNamePinyin.getValues()) {
				namePinyinMap.put(value.getName(), value.getCount());
			}
		}

		return namePinyinMap;
	}

	/**
	 * 获取搜索结果3级分类
	 */
	public static List<SearchCategoryVO> getResultLvl3Category(OperationCategoryService operationCategoryService,
			List<FacetField> facetFields,DataType dataType,String lvl2Code,String lvl1Code) {
		List<Count> facetResult = new ArrayList<Count>();
		if (facetFields == null || facetFields.isEmpty()) {
			return new ArrayList<SearchCategoryVO>();
		}
		// 得到facet的统计结果
		for (FacetField facetField : facetFields) {

			if ("operationCategoryCode3".equals(facetField.getName())) {
				facetResult = facetField.getValues();
			}
		}

		List<String> codes = new ArrayList<String>();

		Map<String, Long> codesMap = new HashMap<String, Long>();
		for (Count lvl : facetResult) {
			codes.add(lvl.getName());
			codesMap.put(lvl.getName(), lvl.getCount());
		}
				
		List<SearchCategoryVO> level3BasicCategory = basicCategoryToSearchCategoryVO(dataType,
				operationCategoryService.getCategoriesByCodes(dataType, codes), codesMap);

		Iterator<SearchCategoryVO> lvl3Iterator = level3BasicCategory.iterator();
		while(lvl3Iterator.hasNext()){
			SearchCategoryVO searchCategoryVO = lvl3Iterator.next();
			// 剔除facet结果中不在当前二级三级分类查询条件下的分类
			if (!StringUtils.isBlank(lvl2Code)) {
				if (!lvl2Code.equals(searchCategoryVO.getParentCode())) {
					lvl3Iterator.remove();
				}
			}else{
				if (!StringUtils.isBlank(lvl1Code)) {
					BasicCategory lvl2Category = operationCategoryService.getCategoryByCode(dataType, searchCategoryVO.getParentCode());
					if (lvl2Category != null && !lvl1Code.equals(lvl2Category.getParentCode())) {
						lvl3Iterator.remove();
					}
				}
			}
		}
		
		Collections.sort(level3BasicCategory, new Comparator<SearchCategoryVO>() {

			public int compare(SearchCategoryVO o1, SearchCategoryVO o2) {
				return o1.getCount() > o2.getCount() ? -1 : 1;

			}

		});
		return level3BasicCategory;
	}

	/**
	 * 得到facet统计的资质列表
	 */
	public static List<SearchCategoryVO> getFacetQualifications(QualificationService qualificationService,
			QualificationLevelService qualificationLevelService, List<FacetField> facetFields) {
		List<Count> level1FacetResult = new ArrayList<Count>();
		if (facetFields == null || facetFields.isEmpty()) {
			return new ArrayList<SearchCategoryVO>();
		}
		// 得到facet的统计结果
		for (FacetField facetField : facetFields) {

			if ("qualificationCode_Level".equals(facetField.getName())) {
				level1FacetResult = facetField.getValues();
			}
		}

		List<String> level1Codes = new ArrayList<String>();

		Map<String, Long> level1CodesMap = new HashMap<String, Long>();
		for (Count lvl1 : level1FacetResult) {
			level1Codes.add(lvl1.getName());
			level1CodesMap.put(lvl1.getName(), lvl1.getCount());
		}

		List<SearchCategoryVO> searchCategoryVOs = new ArrayList<SearchCategoryVO>();

		for (String key : level1CodesMap.keySet()) {
			String[] valueArray = key.split("_");
			if (ArrayUtils.isEmpty(valueArray) || valueArray.length != 2) {
				continue;
			}
			String qualCode = valueArray[0];
			String qualLevelCode = valueArray[1];
			Qualification qualification = qualificationService.getQualificationByCode(qualCode);
			if (qualification == null) {
				continue;
			}
			QualificationLevel qualificationLevel = qualificationLevelService.getQualificationLevelByCode(qualLevelCode);
			if (qualificationLevel == null) {
				continue;
			}
			SearchCategoryVO searchCategoryVO = new SearchCategoryVO();
			searchCategoryVO.setCode(qualification.getQualificationCode());
			searchCategoryVO.setName(qualificationLevel.getLevelName());
			searchCategoryVO.setPriority(qualificationLevel.getPriority());
			searchCategoryVO.setCount(level1CodesMap.get(key).intValue());
			searchCategoryVOs.add(searchCategoryVO);
		}

		Collections.sort(searchCategoryVOs, new Comparator<SearchCategoryVO>() {

			public int compare(SearchCategoryVO o1, SearchCategoryVO o2) {
				int flag = o2.getCode().compareTo(o1.getCode());
				if (flag == 0) {
					return o2.getPriority() > o1.getPriority() ? 1 : -1;
				} else {
					return flag;
				}

			}

		});

		return searchCategoryVOs;
	}

	private static boolean chekcExistsQualification(List<Qualification> qualifications, Qualification current) {
		for (Qualification qualification : qualifications) {
			if (current.getQualificationCode().equals(qualification.getQualificationCode())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将输入的搜索关键字分词
	 */
	public static Set<String> getAnalysisWord(SolrServer solrServer,String keyword) throws SolrServerException, IOException{
		String analysisFieldName = "analysisField";
		FieldAnalysisRequest request = new FieldAnalysisRequest("/analysis/field");
        //request.setQuery(keyword);

		request.addFieldName(analysisFieldName);
		request.setFieldValue(keyword);
		request.setMethod(METHOD.POST);
		FieldAnalysisResponse response = request.process(solrServer);

		Iterator<AnalysisPhase> it = response.getFieldNameAnalysis(analysisFieldName).getIndexPhases().iterator();// 分词结果
        //Iterator<AnalysisPhase> it = response.getFieldNameAnalysis(analysisFieldName).getQueryPhases().iterator();// 分词结果
		List<TokenInfo> list = null;
		while (it.hasNext()) {
			AnalysisPhase phase = it.next();
			list = phase.getTokens();
		}
		Set<String> words = new LinkedHashSet<String>();
		if (list != null && list.size() > 0) {
			for (TokenInfo ti : list) {
				String word = ti.getText();
				words.add(word);
			}
		}
		if (words.size() == 1 && keyword.length() == 1) {
			words.remove(keyword.toLowerCase());
			words.add("*" + keyword.toLowerCase() + "*");
		}
		return words;
	}

    /**
     * 合并集合中相邻的单字成词
     */
    public static Set<String> mergeAdjacentString(List<String> wordSet){
        if (CollectionUtils.isEmpty(wordSet)){
            return null;
        }
        List<String> wordResult = new ArrayList<String>();
        boolean preNotSingle = false;
        for (int i = 0; i < wordSet.size(); i++) {
            String word = wordSet.get(i);
            if (word.length() != 1){
                wordResult.add(word);
                preNotSingle = true;
            }else{
                if (wordResult.size() == 0){
                    wordResult.add(word);
                }else{
                    if (preNotSingle){
                        wordResult.add(word);
                        preNotSingle = false;
                    }else {
                        wordResult.set(wordResult.size() - 1, wordResult.get(wordResult.size() - 1) + word);
                    }
                }
            }
        }
        return new HashSet<String>(wordResult);
    }
	/**
	 * 将资质列表去重
	 */
	public static List<Qualification> mergeBidQualification(List<Qualification> qualifications) {

		List<Qualification> list = new ArrayList<Qualification>();
		for (Qualification qualification : qualifications) {
			if (!chekcExistsQualification(list, qualification)) {
				list.add(qualification);
			}
		}
		return list;
	}

	/**
	 * 将一个map按值排序
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map sortByValue(Map map, final boolean reverse) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {

			public int compare(Object o1, Object o2) {
				if (reverse) {
					return -((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
				}
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Map result = new LinkedHashMap();
        Iterator it = list.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * 删除一个map的item 只保留count 之前的数据
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map deleteMapItemByCount(Map map, int count) {
		int i = 0;
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			it.next();
			if (i + 1 > count) {
				it.remove();
			}
			i++;
		}
		return map;
	}
	
	/**
	 * 不区分大小写替换字符
	 */
	public static String ignoreCaseReplace(String source, String oldstring, String newstring) {
			
		
		List<String> newText = new ArrayList<String>();
		for (int i = 0;i<source.toCharArray().length;i++) {
			char c = source.toCharArray()[i];
			if (CharUtils.toString(c).equalsIgnoreCase(oldstring)) {
				newText.add(MessageFormat.format(newstring, c));
			}else{
				newText.add(CharUtils.toString(c));
			}
				
		}
		
		return StringUtils.join(newText,"");
	}
	
	/**
	 * 获取一个日期距今多少年
	 */
	public static int getYearBetween(String year){

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		return now.get(Calendar.YEAR) - NumberUtils.toInt(year);
	}
	
	/**
	 * 设置招标排序函数的bo
	 */
	public static SolrQueryBO setBiddingBfSortBo(){
		Map<String, String[]> paramMap = new HashMap<String, String[]>();
		paramMap.put("defType", new String []{"edismax"});
		paramMap.put("bf", new String []{"sum(if(sub(state,2),if(sub(state,5),0.7,0.8),0.9),scale(ms(publishTime),0.01,0.1))"});
		
		SolrParams solrParams = new MultiMapSolrParams(paramMap);

		return new SolrQueryBO().setSolrParams(solrParams);
	}

    /**
     * 设置招募排序函数的bo
     */
    public static SolrQueryBO setRecruitBfSortBo(){
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.put("defType", new String []{"edismax"});
        paramMap.put("bf", new String []{"sum(if(sub(state,1),if(sub(state,2),0.7,0.8),0.9),scale(ms(publishTime),0.01,0.1))"});

        SolrParams solrParams = new MultiMapSolrParams(paramMap);

        return new SolrQueryBO().setSolrParams(solrParams);
    }



    public static void main(String[] args) {
       System.out.println(setStringFieldHl("深圳阿斯蒂芬sad奋斗sag公司","<em class=\"search_highlight\">","</em>","深圳sg".toCharArray()));
char a = '我';
    }



}
