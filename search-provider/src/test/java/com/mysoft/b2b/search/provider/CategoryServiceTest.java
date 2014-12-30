package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.search.api.CategoryService;
import com.mysoft.b2b.search.test.BaseTestCase;
import com.mysoft.b2b.search.vo.SearchCategoryVO;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class CategoryServiceTest extends BaseTestCase {
	private static final Logger logger = Logger.getLogger(CategoryServiceTest.class);

	@Autowired
	private CategoryService categoryService;

	@Test
	public void testGetSearchResult()  {
		logger.info("---------------testGetSearchResult begin ------------------------");

		long a1 = System.currentTimeMillis();
        List<SearchCategoryVO> searchResult = categoryService.getNavigateTree(CategoryService.CategoryType.SUPPLIER);
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		logger.info("---------------testGetSearchResult searchResult ------------------------\n");
		
        System.out.println(searchResult);
		
		logger.info("---------------testGetSearchResult end ------------------------");

	}
}
