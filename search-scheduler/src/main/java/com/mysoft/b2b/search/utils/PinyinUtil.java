package com.mysoft.b2b.search.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PinyinUtil {

	/**
	 * 
	 * 方法描述：根据输入的汉语获取拼音 。其中，本方法输出的拼音包括全拼和简拼，并且全拼和简拼都不重复
	 */
	public static Set<String> getPinyin(String str) {
		Set<String> pinyinSet = new HashSet<String>();
		if (str == null || "".equals(str)) {
			return pinyinSet;
		}
		List<Set<String>> pinyinList = getPinyinStr(str);
		if (pinyinList == null || pinyinList.size() < 1) {
			return pinyinSet;
		}

		pinyinSet.addAll(getQuanpinResult(pinyinList, null, 0));// 获取全拼
		pinyinSet.addAll(getJianpinResult(pinyinList, null, 0));// 获取简拼
		 
		return pinyinSet;
	}

	/**
	 * 
	 * 方法描述：获取首字母简拼
	 */
	public static Set<String> getJianpinResult(List<Set<String>> pinyinList, Set<String> currentSet, int index) {
		if (pinyinList == null || pinyinList.size() < 1) {
			return new HashSet<String>();
		}
		Set<String> tempSet = new HashSet<String>();

		Set<String> pinyinSet = pinyinList.get(index);
		if (currentSet == null) {
			currentSet = new HashSet<String>();
			for (String string : pinyinSet) {
				tempSet.add(string.charAt(0) + "");
			}
		} else {
			for (String oldPinyin : currentSet) {
				for (String newPinyin : pinyinSet) {
					tempSet.add(oldPinyin + newPinyin.charAt(0));
				}
			}
		}

		if (index == pinyinList.size() - 1) {
			return tempSet;
		} else {
			return getJianpinResult(pinyinList, tempSet, ++index);
		}
	}

	/**
	 * 
	 * 方法描述： 获取所有的全拼结果
	 */
	public static Set<String> getQuanpinResult(List<Set<String>> pinyinList, Set<String> currentSet, int index) {
		if (pinyinList == null || pinyinList.size() < 1) {
			return new HashSet<String>();
		}
		Set<String> tempList = new HashSet<String>();

		Set<String> pinyinSet = pinyinList.get(index);
		if (currentSet == null) {
			currentSet = new HashSet<String>();
			for (String string : pinyinSet) {
				tempList.add(string);
			}
		} else {
			for (String oldPinyin : currentSet) {
				for (String newPinyin : pinyinSet) {
					tempList.add(oldPinyin + newPinyin);
				}
			}
		}

		if (index == pinyinList.size() - 1) {
			return tempList;
		} else {
			return getQuanpinResult(pinyinList, tempList, ++index);
		}
	}

	/**
	 * 
	 * 方法描述：获取这个字符串的所有的拼音的组合
	 */
	public static List<Set<String>> getPinyinStr(String str) {
		if (str == null) {
			return null;
		}

		char[] chars = str.toCharArray();
		List<Set<String>> pinyinList = new ArrayList<Set<String>>();
		Set<String> pinyinSet;
		for (char c : chars) {// 获取所有的汉字的拼音
			pinyinSet = getCharacterPins(c);
			if (pinyinSet != null) {
				pinyinList.add(pinyinSet);
			}
		}

		return pinyinList;
	}

	/**
	 * 
	 * 方法描述：获取单个字符的拼音
	 */
	public static Set<String> getCharacterPins(char c) {
		HanyuPinyinOutputFormat format = getFormat();
		String[] pinyinArray = null;
		try {
			// 获取拼音
			pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}

		Set<String> pinyinSet = new HashSet<String>();
		// 发生异常或者字符不是拼音的时候，pins都有可能是null，所以，必须进行判断
		if (pinyinArray != null) {
			for (String pinyin : pinyinArray) {
				pinyinSet.add(pinyin);
			}
		}else{
			if (!Character.toString(c).matches("[\u4E00-\u9FA5]+")) {
				pinyinSet.add(StringUtils.lowerCase(Character.toString(c)));
			}
		}
		return pinyinSet;
	}

	/**
	 * 
	 * 方法描述：获取HanyuPinyinOutputFormat的实例
	 */
	private static HanyuPinyinOutputFormat getFormat() {

		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 声调不要
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		return format;
	}

	public static void main(String[] args) {
		System.out.println(getPinyin("创维大厦六期工程水泥采购招标预告"));
	}

}