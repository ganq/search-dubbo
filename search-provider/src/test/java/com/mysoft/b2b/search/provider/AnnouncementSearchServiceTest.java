package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.search.api.AnnouncementSearchService;
import com.mysoft.b2b.search.param.AnnouncementParam;
import com.mysoft.b2b.search.test.BaseTestCase;
import com.mysoft.b2b.search.vo.AnnouncementsVO;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class AnnouncementSearchServiceTest extends BaseTestCase {
	private static final Logger logger = Logger.getLogger(AnnouncementSearchServiceTest.class);

	@Autowired
	private AnnouncementSearchService announcementSearchService;

	@Test
	public void testGetSearchResult() {
		logger.info("---------------testGetSearchResult begin ------------------------");
		AnnouncementParam announcementParam = new AnnouncementParam();
		announcementParam.setKeyword("招");
		long a1 = System.currentTimeMillis();
		Map<String, Object> searchResult = announcementSearchService.getSearchResult(announcementParam);
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		logger.info("---------------testGetSearchResult searchResult ------------------------\n");
		
		//System.out.println(searchResult.get("level3Category"));
		System.out.println(searchResult.get("searchResult"));
		
		logger.info("---------------testGetSearchResult end ------------------------");

	}
	

	@Test
	public void testGetCommonSearchResult()  {
		logger.info("---------------testGetSearchResult begin ------------------------");
		AnnouncementParam announcementParam = new AnnouncementParam();
		//announcementParam.setQueryAll(true);
		announcementParam.setCategorycode("2");
		//announcementParam.setPdatesort("1");
		/*announcementParam.setState("2");
		announcementParam.setSdatesort("0");*/
		long a1 = System.currentTimeMillis();
		 List<AnnouncementsVO> searchResult = announcementSearchService.getCommonSearchResult(announcementParam);
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		
		logger.info("---------------testGetSearchResult searchResult ------------------------\n");
		
		System.out.println(searchResult);
		
		logger.info("---------------testGetSearchResult end ------------------------");
	}
	
}
