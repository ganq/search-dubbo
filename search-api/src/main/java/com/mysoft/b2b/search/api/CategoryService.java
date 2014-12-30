package com.mysoft.b2b.search.api;

import com.mysoft.b2b.search.vo.SearchCategoryVO;

import java.util.List;

/**
 * 分类查询
 * 
 * @author ganq
 * 
 */
public interface CategoryService {

    /**
     * 分类类型
     */
	public enum CategoryType{
        /**
         * 招标
         */
        BIDDING,
        /**
         * 供应商
         */
        SUPPLIER,
        /**
         * 开发商
         */
        DEVELOPER,
        /**
         * 招募
         */
        RECRUIT
    }
	/**
	 * 得到分类导航树（频道页用）
	 * @param categoryType 分类类型
	 */
	List<SearchCategoryVO> getNavigateTree(CategoryType categoryType);
	

}