package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.search.test.BaseTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
public class SupplierSchedulerTest extends BaseTestCase {
	
	@Autowired
	SupplierScheduler supplierScheduler;
	
	@Test
	public void testSolrScheduler() {
		supplierScheduler.run();
		Assert.assertTrue(true);
		
	}
}