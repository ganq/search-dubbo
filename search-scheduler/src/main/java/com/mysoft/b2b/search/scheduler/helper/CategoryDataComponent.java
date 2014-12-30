package com.mysoft.b2b.search.scheduler.helper;

import com.mysoft.b2b.bizsupport.api.BasicCategory;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("restriction")
@Component("categoryDataComponent")
public class CategoryDataComponent {

	private static final Logger logger = Logger.getLogger(CategoryDataComponent.class);
	@Autowired
	private OperationCategoryService operationCategoryService;
	
	@Autowired
	private SearchHelper searchHelper;
	
	private List<BasicCategory> bidOperationRootCategories ;
	private List<BasicCategory> bidOperationLastLevelCategories;
	
	private List<BasicCategory> supplierOperationRootCategories ;
	private List<BasicCategory> supplierOperationLastLevelCategories;
	
	@PostConstruct
	public void init(){
		
		try {
			logger.info("---------------开始加载运营分类数据---------------------");
			
			bidOperationRootCategories = operationCategoryService.getRootSubCategories(DataType.BID);
			bidOperationLastLevelCategories = getLastLevelCategoryNodes(bidOperationRootCategories);
			logger.info("---------------初始化加载招标运营分类树记录:" + bidOperationRootCategories.size() + "条---------------------");
			logger.info("---------------得到最末级招标运营分类树记录:" + bidOperationLastLevelCategories.size() + "条---------------------");
			

			supplierOperationRootCategories = operationCategoryService.getRootSubCategories(DataType.SUPPLIER);
			supplierOperationLastLevelCategories = getLastLevelCategoryNodes(supplierOperationRootCategories);
			logger.info("---------------初始化加载供应商运营分类树记录:" + supplierOperationRootCategories.size() + "条---------------------");
			logger.info("---------------得到最末级供应商运营分类树记录:" + supplierOperationLastLevelCategories.size() + "条---------------------");
			
			logger.info("---------------结束加载运营分类数据---------------------");
		} catch (Exception e) {
			logger.error("---------------初始化运营分类数据失败" , e);
		}
		
	}

	public List<BasicCategory> getBidOperationRootCategories() {
		return bidOperationRootCategories;
	}

	public List<BasicCategory> getSupplierOperationRootCategories() {
		return supplierOperationRootCategories;
	}

	public List<BasicCategory> getBidOperationLastLevelCategories() {
		return bidOperationLastLevelCategories;
	}

	public List<BasicCategory> getSupplierOperationLastLevelCategories() {
		return supplierOperationLastLevelCategories;
	}

	
	/***
	 * 获取所有有效的末级运营分类
	 */
	private List<BasicCategory> getLastLevelCategoryNodes(List<BasicCategory> rootCategories){
		List<BasicCategory> categories = new ArrayList<BasicCategory>();
		for (BasicCategory root : rootCategories) {
			//节点启用，并且是最末级
			if (root.isLastLevel() && root.getCategoryStatus() == 1) {
				categories.add(root);
			}
		}
		return categories;
	}
}