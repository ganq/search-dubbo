package com.mysoft.b2b.search.provider.weixin;

import com.mysoft.b2b.search.api.weixin.SupplierSearchForWeixinService;
import com.mysoft.b2b.search.param.SupplierParam;
import com.mysoft.b2b.search.test.BaseTestCase;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class SupplierSearchForWeixinServiceTest extends BaseTestCase {
	private static final Logger logger = Logger.getLogger(SupplierSearchForWeixinServiceTest.class);

	@Autowired
	private SupplierSearchForWeixinService supplierSearchForWeixinService;

	@Test
	public void testGetSearchResult()  {
		logger.info("---------------testGetSearchResult begin ------------------------");
		SupplierParam supplierParam = new SupplierParam();
		//supplierParam.setKeyword("万");
		//supplierParam.setProvince("222");
		//supplierParam.setArea("north");
        //supplierParam.setRegisterLocation("222");
        //supplierParam.setCodelevel3("601");
        supplierParam.setRegisteredcapital("500000000");
		long a1 = System.currentTimeMillis();
		
		Map<String, Object> searchResult = supplierSearchForWeixinService.getSupplierSearchResult(supplierParam);
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		logger.info("---------------testGetSearchResult searchResult ------------------------\n");
		
		System.out.println(searchResult.get("searchResult"));
		
		logger.info("---------------testGetSearchResult end ------------------------");

	}
	
	@Test
	public void testGetSearchStat()  {
		logger.info("---------------testGetSearchStat begin ------------------------");
		SupplierParam supplierParam = new SupplierParam();
		supplierParam.setKeyword("万");
		long a1 = System.currentTimeMillis();
		
		Map<String, Object> searchResult = supplierSearchForWeixinService.getSupplierStat();
		long a2 = System.currentTimeMillis();
		System.out.println("执行时间：----------------"+(a2-a1));
		
		logger.info("---------------testGetSearchResult searchResult ------------------------\n");
		
		System.out.println(searchResult);
		
		logger.info("---------------testGetSearchStat end ------------------------");

	}
}
