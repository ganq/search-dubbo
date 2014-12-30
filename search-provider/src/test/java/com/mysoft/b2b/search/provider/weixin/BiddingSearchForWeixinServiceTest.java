package com.mysoft.b2b.search.provider.weixin;

import com.mysoft.b2b.search.api.weixin.BiddingSearchForWeixinService;
import com.mysoft.b2b.search.param.AnnouncementParam;
import com.mysoft.b2b.search.test.BaseTestCase;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class BiddingSearchForWeixinServiceTest extends BaseTestCase {
	private static final Logger logger = Logger.getLogger(BiddingSearchForWeixinServiceTest.class);

	@Autowired
	private BiddingSearchForWeixinService biddingSearchForWeixinService;

	@Test
	public void testGetSearchResult()  {
		logger.info("---------------testGetSearchResult begin ------------------------");
		AnnouncementParam announcementParam = new AnnouncementParam();
		announcementParam.setKeyword("万万");
		long a1 = System.currentTimeMillis();
		Map<String, Object> searchResult = biddingSearchForWeixinService.getBiddingSearchResult(announcementParam);
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		logger.info("---------------testGetSearchResult searchResult ------------------------\n");
		
		//System.out.println(searchResult.get("level3Category"));
		System.out.println(searchResult.get("searchResult"));
		
		logger.info("---------------testGetSearchResult end ------------------------");

	}
	
	
}
