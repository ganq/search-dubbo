package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.search.api.RecruitSearchService;
import com.mysoft.b2b.search.param.RecruitParam;
import com.mysoft.b2b.search.test.BaseTestCase;
import com.mysoft.b2b.search.vo.RecruitVO;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class RecruitSearchServiceTest extends BaseTestCase {
	private static final Logger logger = Logger.getLogger(RecruitSearchServiceTest.class);

	@Autowired
	private RecruitSearchService recruitSearchService;

	@Test
	public void testGetSearchResult() {
		logger.info("---------------testGetSearchResult begin ------------------------");
		RecruitParam recruitParam = new RecruitParam();
        recruitParam.setKeyword("招");
		long a1 = System.currentTimeMillis();
		Map<String, Object> searchResult = recruitSearchService.getSearchResult(recruitParam);
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		logger.info("---------------testGetSearchResult searchResult ------------------------\n");
		
		//System.out.println(searchResult.get("level3Category"));
		System.out.println(searchResult.get("searchResult"));
		
		logger.info("---------------testGetSearchResult end ------------------------");

	}
	

	@Test
	public void testGetCommonSearchResult()  {
		logger.info("---------------testGetCommonSearchResult begin ------------------------");
        RecruitParam recruitParam = new RecruitParam();
        recruitParam.setCategorycode("2,3");
		recruitParam.setState("2,3");
        recruitParam.setPdatesort("0");
        recruitParam.setSdatesort("0");
		long a1 = System.currentTimeMillis();
		 List<RecruitVO> searchResult = recruitSearchService.getCommonSearchResult(recruitParam);
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		
		logger.info("---------------testGetCommonSearchResult searchResult ------------------------\n");
		
		System.out.println(searchResult);
		
		logger.info("---------------testGetCommonSearchResult end ------------------------");
	}
	
}
