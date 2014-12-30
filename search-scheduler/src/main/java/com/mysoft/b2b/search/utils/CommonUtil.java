package com.mysoft.b2b.search.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Date;

public class CommonUtil {
	private static final Logger logger = Logger.getLogger(CommonUtil.class);
	
	public static final int HIGHEST_SPECIAL = '>';
    public static char[][] specialCharactersRepresentation = new char[HIGHEST_SPECIAL + 1][];
    static {
        //specialCharactersRepresentation['&'] = "&amp;".toCharArray();
        specialCharactersRepresentation['<'] = "&lt;".toCharArray();
        specialCharactersRepresentation['>'] = "&gt;".toCharArray();
       // specialCharactersRepresentation['"'] = "&quot;".toCharArray();
        //specialCharactersRepresentation['\''] = "&apos;".toCharArray();
    }
	
	/**
	 * 计算两个日期之间的天数差
	 */
	public static int getDaysBetween(Date startDate, Date endDate) {
		boolean isSwap=false;
		Calendar d1=Calendar.getInstance();
		d1.setTime(startDate);
		Calendar d2=Calendar.getInstance();
		d2.setTime(endDate);
		if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
			java.util.Calendar swap = d1;
			d1 = d2;
			d2 = swap;
			isSwap=true;
		}
		int days = d2.get(java.util.Calendar.DAY_OF_YEAR)- d1.get(java.util.Calendar.DAY_OF_YEAR);
		int y2 = d2.get(java.util.Calendar.YEAR);
		if (d1.get(java.util.Calendar.YEAR)!= y2) {
			d1 = (java.util.Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				d1.add(java.util.Calendar.YEAR, 1);
			} while (d1.get(java.util.Calendar.YEAR) != y2);
		}
		if(isSwap){
			days=-days;
		}
		return days;
	}

	/**
	 * 将一个时间加上8小时，转换成北京时间(Solr的时间类型的数据默认时区是格林尼治时间，需要加8小时转换成北京时间)
	 */
	public static Date convertToBeijingTime(Date date){
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		/*calendar.set(Calendar.HOUR_OF_DAY, 0);// 时
		calendar.set(Calendar.MINUTE, 0);// 分
		calendar.set(Calendar.SECOND, 0);// 秒*/
		
		calendar.add(Calendar.HOUR_OF_DAY, 8);
		
		return calendar.getTime();
	}
	
	/**
	 * 去掉"<" ">"等html标记
	 * @param buffer
	 * @return
	 */
	public static String escapeXml(String buffer) {
		if (StringUtils.isBlank(buffer)) {
			return "";
		}
        int start = 0;
        int length = buffer.length();
        char[] arrayBuffer = buffer.toCharArray();
        StringBuffer escapedBuffer = null;

        for (int i = 0; i < length; i++) {
            char c = arrayBuffer[i];
            if (c <= HIGHEST_SPECIAL) {
                char[] escaped = specialCharactersRepresentation[c];
                if (escaped != null) {
                    // create StringBuffer to hold escaped xml string
                    if (start == 0) {
                        escapedBuffer = new StringBuffer(length + 5);
                    }
                    // add unescaped portion
                    if (start < i) {
                        escapedBuffer.append(arrayBuffer,start,i-start);
                    }
                    start = i + 1;
                    // add escaped xml
                    escapedBuffer.append(escaped);
                }
            }
        }
        // no xml escaping was necessary
        if (start == 0) {
            return buffer;
        }
        // add rest of unescaped portion
        if (start < length) {
            escapedBuffer.append(arrayBuffer,start,length-start);
        }
        return escapedBuffer.toString();
    }
	
	public static void main(String[] args) {
		System.out.println(convertToBeijingTime(new Date()));
	}
}
