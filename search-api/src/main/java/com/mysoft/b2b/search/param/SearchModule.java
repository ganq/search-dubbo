package com.mysoft.b2b.search.param;



/**
 * 
 * 搜索模块
 * @author ganq
 * 
 */
public enum SearchModule  {

	/**
	 *  招标预告  
	 */
	BIDDING(0),
	/**
	 *  供应商
	 */
	SUPPLIER(1),
	/**
	 *  开发商
	 */
	DEVELOPER(2),
    /**
     * 招募
     */
    RECRUIT(3);

	
	

	private int value;

	private SearchModule(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	private static final String[] descriptions = { "招标预告", "供应商", "开发商","招募"};

	public String getDescription() {
		return descriptions[getValue()];
	}

	public static final SearchModule valueOf(int value) {
		SearchModule bs = null;
		for (SearchModule e : SearchModule.values()) {
			if (e.getValue() == value) {
				bs = e;
				break;
			}
		}
		return bs;
	}
}
