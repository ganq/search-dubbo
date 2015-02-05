package com.mysoft.b2b.search.provider;

import com.alibaba.fastjson.JSON;
import com.mysoft.b2b.search.api.SupplierSearchService;
import com.mysoft.b2b.search.param.SupplierParam;
import com.mysoft.b2b.search.test.BaseTestCase;
import com.mysoft.b2b.search.vo.SupplierVO;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class SupplierSearchServiceTest extends BaseTestCase {
    private static final Logger logger = Logger.getLogger(SupplierSearchServiceTest.class);

    @Autowired
    private SupplierSearchService supplierSearchService;

    @Test
    public void testGetSearchResultForDeveloper() {
        logger.info("---------------getSearchResultForDeveloper begin ------------------------");
        SupplierParam developerParam = new SupplierParam();

        developerParam.setKeyword("深景有");

        long a1 = System.currentTimeMillis();
        Map<String, Object> searchResult = supplierSearchService.getSearchResult(developerParam);
        long a2 = System.currentTimeMillis();
        System.out.println("执行时间：----------------" + (a2 - a1));

        logger.info("---------------getSearchResultForDeveloper begin ------------------------");

        List<SupplierVO> s = (List<SupplierVO>) searchResult.get("searchResult");
        for (SupplierVO su : s){
            System.out.println(su.getCompanyName());
            System.out.println(su.getBusinessScope());
            System.out.println(su.getProjectLocation() + "\n");
        }

    }

    @Test
    public void test2() {


        long a1 = System.currentTimeMillis();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        //paramMap.put("category", Arrays.asList("17"));
        List<Map<String, String>> qList = new ArrayList<Map<String, String>>();
        Map<String, String> qmap = new HashMap<String, String>();
        qmap.put("qualCode", "34");
        qmap.put("priority", "4");

        Map<String, String> qmap2 = new HashMap<String, String>();
        qmap2.put("qualCode", "30");
        qmap2.put("priority", "3");

        qList.add(qmap);
        qList.add(qmap2);
        paramMap.put("qualify", qList);
        //paramMap.put("serviceArea", "222");
        //paramMap.put("registerFund", "10000");
        //paramMap.put("buildYears", "5");
        //paramMap.put("caseNum", "3");
        paramMap.put("page", "1");
        paramMap.put("pageSize", "1000");


        List<String> searchResult = supplierSearchService.getPushSupplierIds(paramMap);
        System.out.println(searchResult);
        long a2 = System.currentTimeMillis();
        System.out.println("执行时间：----------------" + (a2 - a1));
    }


    @Test
    public void test3() {
        SupplierVO a  =new SupplierVO();
        a.setAuthTag(true);
        a.setAwardBidCount(11);
        a.setDefaultAward("我擦擦擦啊");

        SupplierVO ab  =new SupplierVO();
        ab.setAuthTag(false);
        ab.setAwardBidCount(22);
        ab.setDefaultAward("发发发咳咳咳");


        List<SupplierVO> supplierVOs = new ArrayList<SupplierVO>();
        supplierVOs.add(a);
        supplierVOs.add(ab);

        String aa = JSON.toJSONString(supplierVOs);

        System.out.println(aa);
        System.out.println(JSON.parseArray(aa,Map.class));

    }
}