package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.search.api.SearchSuggestService;
import com.mysoft.b2b.search.test.BaseTestCase;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class SearchSuggestServiceTest extends BaseTestCase {
	private static final Logger logger = Logger.getLogger(SearchSuggestServiceTest.class);

	@Autowired
	private SearchSuggestService searchSuggestService;

	
	@Test
	public void test() {
		logger.info("---------------test begin ------------------------");
		
		long a1 = System.currentTimeMillis();
		Map<String, Long> searchResult = searchSuggestService.getSearchSuggestion("央","developer,supplier");
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		
		System.out.println(searchResult);
		
		logger.info("---------------test end ------------------------");

	}
	
	
}
