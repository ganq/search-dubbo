package com.mysoft.b2b.search.scheduler.helper;

import com.mysoft.b2b.bizsupport.api.*;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.search.spi.SearchModel;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


@Component("searchHelper")
public class SearchHelper {

	private final Logger logger = Logger.getLogger(SearchHelper.class);
	
	@Autowired
	private BasicCategoryService basicCategoryService;
	
	@Autowired
	private OperationCategoryService operationCategoryService;
	
	@Autowired
	private CategoryDataComponent categoryDataComponent;

    /**
     * 修改document字段的类型为Set
     */
	public void addDefaultSet(Map<String,Object> map,String [] mergeFields){
		//将需要设置为multiValued的索引字段设置为Set类型
		for (String field : mergeFields) {
			Set<String> typeSet = new HashSet<String>();
			String currentFieldValue = ObjectUtils.toString(map.get(field));
			//不添加空值数据
			if (!"".equals(currentFieldValue.trim())) {
				typeSet.add(currentFieldValue);	
			}
			map.put(field, typeSet);
		}
	}
	
	/**
	 * 检查数据list里面有没有已经存在的数据
	 */
	public boolean checkListRecordExists(List<Map<String, Object>> dataList,String bId,String key){
		boolean flag = false;
		for (Map<String, Object> map : dataList) {
			String primaryId = ObjectUtils.toString(map.get(key));
			if (primaryId.equals(bId)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 合并重复将合并字段值归并成Set
	 */
	@SuppressWarnings("unchecked")
	public void mergeListMap(List<Map<String, Object>> dataList,Map<String, Object> dataMap,String [] mergeFields,String key){
		String bId = ObjectUtils.toString(dataMap.get(key));
		for (Map<String, Object> map : dataList) {
			String biddingId = ObjectUtils.toString(map.get(key));
			if (!biddingId.equals(bId)) {
				continue;
			}
			//循环设置合并字段，将相同的合并字段值存到一个Set中
			for (String field : mergeFields) {
				Set<String> oldValue = (Set<String>)map.get(field);
				String newValue = ObjectUtils.toString(dataMap.get(field)).trim();
				if (!oldValue.contains(newValue) && !"".equals(newValue)) {
					oldValue.add(newValue);
					map.put(field, oldValue);	
				}
			}
			
			break;
		}
	}
	
	/**
	 * 获取一个基础分类自上到顶级的各级分类
	 */
	public Map<String, Object> getBasicCategoryAllLevel(String categoryCode) {
		Map<String, Object> map = new HashMap<String, Object>();

		BasicCategoryNode basicCategoryNode = basicCategoryService.getCategorySuperHierarchy(categoryCode);

		if (basicCategoryNode == null) {
			logger.info("根据基础服务产品分类code：" + categoryCode + "没有查询到根节点");
			return map;
		}
		map.put("basicCategoryCode1", basicCategoryNode.getCategoryCode());
		map.put("basicCategoryName1", basicCategoryNode.getCategoryName());

		// 如果当前查询的code等于根节点code，就不需要再往下查询子节点了
		if (categoryCode.equals(basicCategoryNode.getCategoryCode())) {
			map.put("basicCategoryName", basicCategoryNode.getCategoryName());
			return map;
		}

		List<BasicCategoryNode> level2ChildNodes = basicCategoryNode.getChildBasicCategoryNodes();
		if (level2ChildNodes == null || level2ChildNodes.isEmpty()) {
			logger.info("根据顶级基础服务产品分类：" + basicCategoryNode.getCategoryCode() + "没有查询到子节点");
			return map;
		}

		BasicCategoryNode node2Level = level2ChildNodes.get(0);

		if (node2Level != null) {
			map.put("basicCategoryCode2", node2Level.getCategoryCode());
			map.put("basicCategoryName2", node2Level.getCategoryName());
		}
		if (categoryCode.equals(node2Level.getCategoryCode())) {
			map.put("basicCategoryName", node2Level.getCategoryName());
			return map;
		}

		List<BasicCategoryNode> level3ChildNodes = node2Level.getChildBasicCategoryNodes();
		if (level3ChildNodes == null || level3ChildNodes.isEmpty()) {
			logger.info("根据二级基础服务产品分类：" + node2Level.getCategoryCode() + "没有查询到子节点");
			return map;

		}
		BasicCategoryNode node3Level = level3ChildNodes.get(0);
		if (node3Level != null) {
			map.put("basicCategoryCode3", node3Level.getCategoryCode());
			map.put("basicCategoryName3", node3Level.getCategoryName());
		}
		if (categoryCode.equals(node3Level.getCategoryCode())) {
			map.put("basicCategoryName", node3Level.getCategoryName());
			return map;
		}

		BasicCategory node4Level = basicCategoryService.getCategoryByCode(categoryCode);
		map.put("basicCategoryCode4", node4Level.getCategoryCode());
		map.put("basicCategoryName4", node4Level.getCategoryName());
		map.put("basicCategoryName", node4Level.getCategoryName());
		return map;
	}
	
	/**
	 * 获取一个运营分类自上到顶级的各级分类
	 */
	private Map<String, Object> getOperationCategoryAllLevel(String categoryCode, DataType dataType) {
		Map<String, Object> map = new HashMap<String, Object>();

		List<BasicCategory> categoryNodes = operationCategoryService.getCategorySuperNodes(dataType,categoryCode);
		
		if (CollectionUtils.isEmpty(categoryNodes)) {
			logger.info("根据"+(dataType == DataType.BID?"招标预告":"供应商")+"运营分类code：" + categoryCode + "没有查询到根节点");
			return map;
		}
		for (int i = 0; i <= 4; i++) {
			String index = (i==0?"":i+"");
			map.put("operationCategoryCode" + index,stringToSet(""));
			map.put("operationCategoryName" + index,stringToSet(""));	
		}
		
		for (int i = 0; i < categoryNodes.size(); i++) {
			BasicCategory basicCategory = categoryNodes.get(i);
			if (basicCategory == null) {
				continue;
			}
			map.put("operationCategoryCode" + (i+1),stringToSet(basicCategory.getCategoryCode()));
			map.put("operationCategoryName" + (i+1), stringToSet(basicCategory.getCategoryName()));
			
			if (categoryCode.equals(basicCategory.getCategoryCode()) ) {
				map.put("operationCategoryName", stringToSet(basicCategory.getCategoryName()));
				map.put("operationCategoryCode", stringToSet(basicCategory.getCategoryCode()));
			}
		}
		
		return map;
	}
	
	/**
	 * 单字符串转Set
	 */
	private Set<String> stringToSet(String str){
		String [] arrays = {str};
		List<String> list = Arrays.asList(arrays);
		return new HashSet<String>(list);
	}
	/**
	 * 合并运营分类的数据集合，待插入solr
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> mergeCategoryListMap(List<Map<String, Object>> list){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		for (Map<String, Object> map : list) {
			for (String key : map.keySet()) {	
				if (!resultMap.containsKey(key)) {
					resultMap.put(key, map.get(key));
				}else{
					Map<String,Object> oldMap = (Map<String,Object>)resultMap.get(key);
					Map<String,Object> newMap = (Map<String,Object>)map.get(key);
					resultMap.put(key, mergeCategoryMap(oldMap, newMap));
				}
			}
		}
		
		return resultMap;
	}
	
	/**
	 * 合并运营分类的单个map
	 */
	private Map<String,Object> mergeCategoryMap(Map<String, Object> oldMap, Map<String, Object> newMap){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		for (String key : oldMap.keySet()) {
			
			Set<String> oldSet = (Set<String>)oldMap.get(key);
			Set<String> newSet = (Set<String>)newMap.get(key);
					
			Set<String> resultSet = new HashSet<String>();
			resultSet.addAll(oldSet);
			resultSet.addAll(newSet);
			
			resultMap.put(key, resultSet);
		}
		
		return resultMap;
	}
	
	/**
	 * 执行更新运营分类
	 * @param ids					增量更新的id集合
	 * @param dataType
	 * @param solrServer
	 * @param indexList			上一步构建的数据集合
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public int updateOperationCategory(Set<String> ids,DataType dataType,SolrServer solrServer,
			List<Map<String, Object>> indexList,String primaryIdFieldName) throws SolrServerException, IOException{
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		List<BasicCategory> categories = null;
		
		if (DataType.BID == dataType) {
			categories = categoryDataComponent.getBidOperationLastLevelCategories();
			
		}
		if (DataType.SUPPLIER == dataType) {
			categories = categoryDataComponent.getSupplierOperationLastLevelCategories();
		}
		for (BasicCategory basicCategory : categories ) {
			List<String> basicIds = new ArrayList<String>();
			if (DataType.BID == dataType) {
				BidOperationCategory bidOperationCategory = (BidOperationCategory)basicCategory;	
				// 得到运营分类下绑定的基础分类
				basicIds = bidOperationCategory.getBindBasicCategoryIds();
				if (CollectionUtils.isEmpty(basicIds)) {
					continue;
				}
				
			}
			if (DataType.SUPPLIER == dataType) {
				SupplierOperationCategory supplierOperationCategory = (SupplierOperationCategory)basicCategory;	
				// 得到运营分类下绑定的基础分类
				basicIds = supplierOperationCategory.getBindBasicCategoryIds();
				if (CollectionUtils.isEmpty(basicIds)) {
					continue;
				}
			}
			
			String queryIds = "";
			if (ids != null && !ids.isEmpty()) {
				queryIds = " AND (" + StringUtils.join(ids," OR ") + ")";
			}
			Set<String> primaryIds = new HashSet<String>(); 
			for (String id : basicIds) {
				SolrQuery query = new SolrQuery();
				//查询哪些信息能挂靠到该运营分类下，得到他的id					
				query.setQuery("searchBasicCategoryCode:" + id + queryIds).setRows(100000000).setFields(primaryIdFieldName);
				Set<String> queryBiddingIds = getSolrQueryIds(solrServer.query(query), dataType,primaryIdFieldName);
				if (queryBiddingIds != null) {
					primaryIds.addAll(queryBiddingIds);
				}
				
			}
			if (primaryIds.isEmpty()) {
				continue;
			}
			Map<String, Object> solrData = getOperationCategorySolrData(primaryIds, basicCategory.getCategoryCode(), dataType);
			dataList.add(solrData);
		}
		Map<String, Object> finalSolrData = mergeCategoryListMap(dataList);
		updateOperationCategorySolrData(solrServer,finalSolrData,dataType,indexList,primaryIdFieldName);
		
		return finalSolrData.keySet().size();
	}
	
	/**
	 * 更新运营分类到solr
	 */
	private void updateOperationCategorySolrData(SolrServer solrServer,Map<String,Object> solrData,DataType dataType
			,List<Map<String, Object>> solrIndexList,String primaryIdFieldName) throws SolrServerException, IOException{
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		
		for (String key : solrData.keySet()) {
			SolrInputDocument document=new SolrInputDocument();
			if (DataType.BID == dataType) {
				document.setField(primaryIdFieldName, key);  

				setUpdateDocument(document, solrIndexList, primaryIdFieldName, key);
			}
			if (DataType.SUPPLIER == dataType) {
				document.setField(primaryIdFieldName, key);

				setUpdateDocument(document, solrIndexList, primaryIdFieldName, key);
			}
			
			Map<String, Object> opCategories = (Map<String,Object>)solrData.get(key);
			for (String opKey : opCategories.keySet()) {
				document.setField(opKey, opCategories.get(opKey));  
			}
			
			docs.add(document);
		}
		if (docs.isEmpty()) {
			return;
		}
        solrServer.add(docs);  
        
        solrServer.commit();  
	}
	
	/**
	 * 设置需要覆盖更新的solr 文档
	 */
	private void setUpdateDocument(SolrInputDocument document,List<Map<String, Object>> solrIndexList,String primaryId,String primaryValue){
		for (Map<String, Object> map : solrIndexList) {
			if (primaryValue.equals(map.get(primaryId))) {
				for (String key : map.keySet()) {
					document.setField(key, map.get(key));
				}
				break;
			}
		}
	}
	
	/**
	 * 得到某运营分类的所有上下级
	 */
	private Map<String,Object> getOperationCategorySolrData(Set<String> ids,String operationCategoryCode,DataType dataType){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> opCategoryMap = getOperationCategoryAllLevel(operationCategoryCode, dataType);
		
		for (String id : ids) {
			resultMap.put(id, opCategoryMap);
		}
		
		return resultMap;
	}
	
	/**
	 * 通过运营分类查询到其下的招标预告id集合
	 */
	private Set<String> getSolrQueryIds(QueryResponse queryResponse,DataType dataType,String primaryIdFieldName){
		SolrDocumentList result = queryResponse.getResults();
		if (result == null || result.isEmpty()) {
			return null;
		}
		Set<String> idSet = new HashSet<String>();
		for (SolrDocument solrDocument : result) {
			if (DataType.BID == dataType) {
				idSet.add(ObjectUtils.toString(solrDocument.getFieldValue(primaryIdFieldName)));
			}
			if (DataType.SUPPLIER == dataType) {
				idSet.add(ObjectUtils.toString(solrDocument.getFieldValue(primaryIdFieldName)));
			}
		}
		return idSet;
	}

    /**
     * 启动多线程完成数据填充
     */
	public  <T extends SearchModel> List<T> getDataByThread(Set<String> ids,int threadPoolCount,SchedulerThreadData scheduler,Class<T> t) {
		List<SchedulerIndexTask> tasks = new ArrayList<SchedulerIndexTask>();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, threadPoolCount, 600, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100), new ThreadPoolExecutor.CallerRunsPolicy());
		TaskMonitor monitor = new TaskMonitor();
		 
		for (String id : ids) {

			SchedulerIndexTask task = new SchedulerIndexTask();
			task.setId(id);
			task.setScheduler(scheduler);
			task.setMonitor(monitor);
			tasks.add(task);
		}
				
		List<T> newSolrIndexList = new ArrayList<T>();
		try {
			monitor.setTotal(tasks.size());
			monitor.start();
			
			List<Future<SchedulerIndexTask>> results = threadPoolExecutor.invokeAll(tasks);
			
			monitor.stop();
			
			for(Future<SchedulerIndexTask> future:results) {
				SchedulerIndexTask temp;
				try {
					temp = future.get();
				} catch (ExecutionException e) {
					e.printStackTrace();
					continue;
				}
				if(temp == null || temp.getNewObject() == null) {
					continue;
				}
				newSolrIndexList.add((T) temp.getNewObject());
			}
			
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			threadPoolExecutor.shutdown();
		}
		return newSolrIndexList;
	}
}