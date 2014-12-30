package com.mysoft.b2b.search.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.SolrParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Solr查询数据对象
 * @author ganq
 *
 */
public class SolrQueryBO {

	/**
	 * 查询字段名称：fieldName 简写fN
	 */
	private String fN;
	
	/**
	 * 查询字段值：fieldValue 简写fV
	 */
	private String fV;
	
	/**
	 * 排序
	 */
	private SolrQuery.ORDER sort;

	/**
	 * 是否查询字段
	 */
	private boolean queryField;
	
	/**
	 * 是否排序字段 
	 */
	private boolean sortField;
	
	/**
	 * 是否facet字段
	 */
	private boolean facetField;
	
	private boolean facetPivotField;
	
	/**
	 * form表单查询参数集合
	 */
	private List<SolrQueryBO> sqd = new ArrayList<SolrQueryBO>(20);
	
	/**
	 * facet查询记录限制数
	 */
	private int facetLimit ;
	
	/**
	 * 是否高亮字段
	 */
	private boolean highlightField;
	
	/**
	 * 高亮字段是否来自多值
	 */
	private boolean highlightPreserveMulti;
	
	/**
	 * 是否必须的查询条件(在条件前面加 '+'符号)
	 */
	private boolean requiredCondition;

	/**
	 * 是否过滤条件
	 */
	private boolean filterQueryField;
	
	/**
	 * 自定义查询字符串
	 */
	private String customQueryStr;
	
	/**
	 * 自定义参数
	 */
	private SolrParams solrParams;
	
	public String getfN() {
		return fN;
	}

	public SolrQueryBO setfN(String fN) {
		this.fN = fN;
		return this;
	}

	public String getfV() {
		return fV;
	}

	public SolrQueryBO setfV(String fV) {
		this.fV = fV;
		return this;
	}

	public SolrQuery.ORDER getSort() {
		return sort;
	}

	public SolrQueryBO setSort(SolrQuery.ORDER sort) {
		this.sort = sort;
		return this;
	}

	public boolean isQueryField() {
		return queryField;
	}

	public SolrQueryBO setQueryField(boolean isQueryField) {
		this.queryField = isQueryField;
		return this;
	}

	public boolean isSortField() {
		return sortField;
	}

	public SolrQueryBO setSortField(boolean isSortField) {
		this.sortField = isSortField;
		return this;
	}

	public boolean isFacetField() {
		return facetField;
	}

	public SolrQueryBO setFacetField(boolean isFacetField) {
		this.facetField = isFacetField;
		return this;
	}

	public List<SolrQueryBO> getSqd() {
		return sqd;
	}

	public SolrQueryBO setSqd(List<SolrQueryBO> sqd) {
		this.sqd = sqd;
		return this;
	}

	public int getFacetLimit() {
		return facetLimit;
	}

	public SolrQueryBO setFacetLimit(int facetLimit) {
		this.facetLimit = facetLimit;
		return this;
	}

	public boolean isHighlightField() {
		return highlightField;
	}

	public SolrQueryBO setHighlightField(boolean isHighlightField) {
		this.highlightField = isHighlightField;
		return this;
	}

	public boolean isRequiredCondition() {
		return requiredCondition;
	}

	public SolrQueryBO setRequiredCondition(boolean isRequiredCondition) {
		this.requiredCondition = isRequiredCondition;
		return this;
	}

	public boolean isFilterQueryField() {
		return filterQueryField;
	}

	public SolrQueryBO setFilterQueryField(boolean isFilterQueryField) {
		this.filterQueryField = isFilterQueryField;
		return this;
	}

	public boolean isFacetPivotField() {
		return facetPivotField;
	}

	public SolrQueryBO setFacetPivotField(boolean isFacetPivotField) {
		this.facetPivotField = isFacetPivotField;
		return this;
	}

	public String getCustomQueryStr() {
		return customQueryStr;
	}

	public SolrQueryBO setCustomQueryStr(String customQueryStr) {
		this.customQueryStr = customQueryStr;
		return this;
	}

	public SolrParams getSolrParams() {
		return solrParams;
	}

	public SolrQueryBO setSolrParams(SolrParams solrParams) {
		this.solrParams = solrParams;
		return this;
	}

	public boolean isHighlightPreserveMulti() {
		return highlightPreserveMulti;
	}

	public SolrQueryBO setHighlightPreserveMulti(boolean highlightPreserveMulti) {
		this.highlightPreserveMulti = highlightPreserveMulti;
		return this;
	}

	@Override
	public String toString() {
		return "SolrQueryBO [fN=" + fN + ", fV=" + fV + ", sort=" + sort + ", isQueryField=" + queryField
				+ ", isSortField=" + sortField + ", isFacetField=" + facetField + ", isFacetPivotField="
				+ facetPivotField + ", sqd=" + sqd + ", facetLimit=" + facetLimit + ", isHighlightField="
				+ highlightField + ", highlightPreserveMulti=" + highlightPreserveMulti + ", isRequiredCondition="
				+ requiredCondition + ", isFilterQueryField=" + filterQueryField + ", customQueryStr="
				+ customQueryStr + ", solrParams=" + solrParams + "]";
	}

	
}
