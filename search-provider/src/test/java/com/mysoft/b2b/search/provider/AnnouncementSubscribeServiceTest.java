package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.search.api.AnnouncementSubscribeService;
import com.mysoft.b2b.search.param.AnnouncementParam;
import com.mysoft.b2b.search.test.BaseTestCase;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class AnnouncementSubscribeServiceTest extends BaseTestCase {
	private static final Logger logger = Logger.getLogger(AnnouncementSubscribeServiceTest.class);

	@Autowired
	private AnnouncementSubscribeService announcementSubscribeService;

	

	@Test
	public void testGetSearchResult()  {
		logger.info("---------------testGetSearchResult begin ------------------------");
		AnnouncementParam announcementParam = new AnnouncementParam();
		
		Date auditTime = new Date();
		auditTime.setMonth(9);
		auditTime.setDate(10);
		announcementParam.setAuditTime(auditTime );
		announcementParam.setQueryAll(true);
		long a1 = System.currentTimeMillis();
		Map<String, Object> searchResult = announcementSubscribeService.getSearchResult(announcementParam);
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		
		logger.info("---------------testGetSearchResult searchResult ------------------------\n");
		
		System.out.println(searchResult.get("searchResult"));
		
		logger.info("---------------testGetSearchResult end ------------------------");

	}
}
