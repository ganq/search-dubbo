package com.mysoft.b2b.search.param;

import java.io.Serializable;

/**
 * 开发商搜索查询参数
 * 
 * @author ganq
 * 
 */
public class DeveloperParam extends BaseParam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeveloperParam() {
		super();
		super.setSearchModule(SearchModule.DEVELOPER);
		super.setSearchSource(SearchSource.WEBSITE);
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
	
}
