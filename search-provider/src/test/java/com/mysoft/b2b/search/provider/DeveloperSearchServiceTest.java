package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.search.api.DeveloperSearchService;
import com.mysoft.b2b.search.param.DeveloperParam;
import com.mysoft.b2b.search.test.BaseTestCase;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class DeveloperSearchServiceTest extends BaseTestCase {
	private static final Logger logger = Logger.getLogger(DeveloperSearchServiceTest.class);

	@Autowired
	private DeveloperSearchService developerSearchService ;


	@Test
	public void testGetSearchResult() {
		logger.info("---------------testGetSearchResult begin ------------------------");
		DeveloperParam developerParam = new DeveloperParam();

		developerParam.setKeyword("公司");
	
		long a1 = System.currentTimeMillis();
		Map<String, Object> searchResult = developerSearchService.getSearchResult(developerParam);
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		logger.info("---------------testGetSearchResult begin ------------------------");
		
		System.out.println(searchResult);
	}
}
