package com.mysoft.b2b.search.param;
/**
 * 
 * 搜索来源
 * @author ganq
 * 
 */
public enum SearchSource  {

	/**
	 *  网站 
	 */
	WEBSITE(0),
	/**
	 *  微信
	 */
	WEIXIN(1);

	private int value;

	private SearchSource(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	private static final String[] descriptions = { "网站  ", "微信"};

	public String getDescription() {
		return descriptions[getValue()];
	}

	public static final SearchSource valueOf(int value) {
		SearchSource bs = null;
		for (SearchSource e : SearchSource.values()) {
			if (e.getValue() == value) {
				bs = e;
				break;
			}
		}
		return bs;
	}
}
