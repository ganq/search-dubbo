package com.mysoft.b2b.search.param;



/**
 * 
 * 搜索位置
 * @author ganq
 * 
 */
public enum SearchLocation  {

	/**
	 *  华北  
	 */
	NORTH(0),
	/**
	 *  华南
	 */
	SOUTH(1),
	/**
	 *  华东
	 */
	EAST(2),
	/**
	 * 华中
	 */
	CENTER(3),
	/**
	 * 东北
	 */
	NORTHEAST(4),
	/**
	 * 西北
	 */
	NORTHWEST(5),
	/**
	 * 西南
	 */
	SOUTHWEST(6);

	private int value;

	private SearchLocation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	private static final String [] locationCode = {"north","south","east","center","northeast","northwest","southwest"};
	private static final String [] locationChinese = {"华北","华南","华东","华中","东北","西北","西南"};
	
	public String getLocationCode() {
		return locationCode[getValue()];
	}
	
	public String getLocationName() {
		return locationChinese[getValue()];
	}

	public static final SearchLocation valueOf(int value) {
		SearchLocation bs = null;
		for (SearchLocation e : SearchLocation.values()) {
			if (e.getValue() == value) {
				bs = e;
				break;
			}
		}
		return bs;
	}
	
	public static final String getLocationNameByCode(String code) {
		for (SearchLocation e : SearchLocation.values()) {
			if (e.getLocationCode().equals(code)) {
				return e.getLocationName();
			}
		}
		return "";
	}
}
