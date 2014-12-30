package com.mysoft.b2b.search.vo;

import java.io.Serializable;
import java.util.List;

public class SearchCategoryVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 分类编码
	 */
	private String code;
	
	/**
	 * 分类名称
	 */
	private String name;
	
	/**
	 * 数量
	 */
	private Integer count;
	
	/**
	 * 父级编码
	 */
	private String parentCode;
	/**
	 * 下级分类
	 */
	private List<SearchCategoryVO> childrenList;
	
	/**
	 * 优先级(做排序用)
	 */
	private int priority;
	
	private String pinyin;

    //----------以下属性为导航数专用----------
    /**
     * 有效数量
     */
    private Integer validCount;
    /**
     * 总数量
     */
    private Integer allCount;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<SearchCategoryVO> getChildrenList() {
		return childrenList;
	}

	public void setChildrenList(List<SearchCategoryVO> childrenList) {
		this.childrenList = childrenList;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

    public Integer getValidCount() {
        return validCount;
    }

    public void setValidCount(Integer validCount) {
        this.validCount = validCount;
    }

    public Integer getAllCount() {
        return allCount;
    }

    public void setAllCount(Integer allCount) {
        this.allCount = allCount;
    }

    @Override
    public String toString() {
        return "SearchCategoryVO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", parentCode='" + parentCode + '\'' +
                ", childrenList=" + childrenList +
                ", priority=" + priority +
                ", pinyin='" + pinyin + '\'' +
                ", validCount=" + validCount +
                ", allCount=" + allCount +
                '}';
    }
}
