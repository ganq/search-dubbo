package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.commons.scheduler.MysoftJob;
import com.mysoft.b2b.search.scheduler.helper.CategoryDataComponent;
import com.mysoft.b2b.search.utils.PropertiesUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map.Entry;

/**
 * 清除增量发布时间标志定时任务
 * 
 * @author ganq
 * 
 */
public class ClearTimeFlagScheduler extends MysoftJob{
	private static final Logger logger = Logger.getLogger(ClearTimeFlagScheduler.class);

	@Autowired
	private CategoryDataComponent categoryDataComponent;
	
	@Override
	public void run() {
		clearDateKey();
	}

	/**
	 * 清除时间键值
	 */
	private void clearDateKey(){
		
		PropertiesUtil.setSolrDateKey(PropertiesUtil.SOLR_CORE_ANNOUNCEMENTS_LAST_MODIFY_DATE, "");
		PropertiesUtil.setSolrDateKey(PropertiesUtil.SOLR_CORE_SUPPLIER_LAST_MODIFY_DATE, "");
        PropertiesUtil.setSolrDateKey(PropertiesUtil.SOLR_CORE_DEVELOPER_LAST_MODIFY_DATE, "");
        PropertiesUtil.setSolrDateKey(PropertiesUtil.SOLR_CORE_RECRUIT_LAST_MODIFY_DATE, "");

        logger.info("清除日期标记成功!");
		categoryDataComponent.init();
	}
	
}