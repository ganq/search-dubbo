package com.mysoft.b2b.search.utils;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

/**
 * 属性配置文件操作
 * 
 * @author ganq
 * 
 */
public class PropertiesUtil {

	/**
	 * solr实例：announcements 主键 biddingId
	 */
	public static final String SOLR_CORE_ANNOUNCEMENTS_PRIMARY_KEY = "biddingId";
	
	/** 
	 * solr实例：supplier 主键 supplierId
	 */
	public static final String SOLR_CORE_SUPPLIER_PRIMARY_KEY = "supplierId";
	
	/** 
	 * solr实例：developer 主键 developerId
	 */
	public static final String SOLR_CORE_DEVELOPER_PRIMARY_KEY = "developerId";

    /**
     * solr实例：recruit 主键 recruitId
     */
    public static final String SOLR_CORE_RECRUIT_PRIMARY_KEY = "recruitId";
	
	/**
	 * solr实例： announcements
	 */
	public static final String SOLR_CORE_ANNOUNCEMENTS_ADDRESS = "announcements.address";

    /**
     * solr实例： recruit
     */
    public static final String SOLR_CORE_RECRUIT_ADDRESS = "recruit.address";


    /**
	 * solr实例：category
	 */
	public static final String SOLR_CORE_CATEGORY_ADDRESS = "category.address";
	
	/**
	 * solr实例：supplier
	 */
	public static final String SOLR_CORE_SUPPLIER_ADDRESS = "supplier.address";
	
	/**
	 * solr实例：developer
	 */
	public static final String SOLR_CORE_DEVELOPER_ADDRESS = "developer.address";
	
	/**
	 * 实例 announcements的数据最后修改时间
	 */
	public static final String SOLR_CORE_ANNOUNCEMENTS_LAST_MODIFY_DATE = "announcements.last.modify.date";

	/**
	 * 实例 supplier的数据最后修改时间
	 */
	public static final String SOLR_CORE_SUPPLIER_LAST_MODIFY_DATE = "supplier.last.modify.date";
	
	/**
	 * 实例 developer的数据最后修改时间
	 */
	public static final String SOLR_CORE_DEVELOPER_LAST_MODIFY_DATE = "developer.last.modify.date";

    /**
     * 实例 recruit的数据最后修改时间
     */
    public static final String SOLR_CORE_RECRUIT_LAST_MODIFY_DATE = "recruit.last.modify.date";

    /**
	 * solr 配置文件位置
	 */
	private static final String SOLR_PROPERTIES = "/solr.properties";
    /**
     * solr date配置文件位置
     */
    private static final String SOLR_LAST_MODIFY_DATE_PROPERTIES = "/solr_last_modify_date.properties";

    private static final Logger logger = Logger.getLogger(PropertiesUtil.class);

	public static Properties solrProp = new Properties();
    public static Properties solrDateProp = new Properties();


    /**
	 * 当前是否debug状态
	 */
	public static boolean isDebug = false;
	static {
        initProperties(SOLR_PROPERTIES,solrProp);
        initProperties(SOLR_LAST_MODIFY_DATE_PROPERTIES,solrDateProp);
    }

    /**
     * 初始化properties
     * @param filePath
     * @param prop
     */
   static void initProperties(String filePath,Properties prop){
       InputStream fis = null;
       try {
           fis = PropertiesUtil.class.getResourceAsStream(filePath);
           prop.load(fis);

       } catch (IOException e) {
           logger.info("Not Found properties fil path: [" + filePath + "]",e);
       } catch (Exception e) {
           logger.info("load this path: [" + filePath + "] error" ,e);
       } finally {
           if (fis != null) {
               try {
                   fis.close();
               } catch (IOException e) {
                   logger.info(e.getMessage());
               }
           }
       }
   }

	/**
	 * 根据key获取Value
	 */
	public static String getKey(Properties prop, String key) {
		return prop.getProperty(key, "");
	}

    /**
	 * 更新key的Value
	 */
	public static void setSolrDateKey(String key, String value) {
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(PropertiesUtil.class.getResource(SOLR_LAST_MODIFY_DATE_PROPERTIES).getFile());
			solrDateProp.setProperty(key, value);
            solrDateProp.store(fos, "update solr import time");
		} catch (IOException e) {
			logger.info("update key: " + key + " and value :" + value + " error ,msg:" + e.getMessage());
		} finally {
			try {
                if (fos != null){
                    fos.close();
                }
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
		}

	}

	public static void main(String[] args) {

        setSolrDateKey(SOLR_CORE_ANNOUNCEMENTS_LAST_MODIFY_DATE, new Date().getTime() + "");
		System.out.println(getKey(PropertiesUtil.solrDateProp,SOLR_CORE_ANNOUNCEMENTS_LAST_MODIFY_DATE));
	}
}
