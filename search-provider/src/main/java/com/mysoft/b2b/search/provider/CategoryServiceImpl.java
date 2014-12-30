package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.bizsupport.api.OperationCategoryService;
import com.mysoft.b2b.bizsupport.api.OperationCategoryService.DataType;
import com.mysoft.b2b.search.api.CategoryService;
import com.mysoft.b2b.search.solr.SolrQueryBO;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.util.BaseUtil;
import com.mysoft.b2b.search.vo.SearchCategoryVO;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * CategoryService接口的实现类,提供分类搜索相关服务
 *
 * @author ganq
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private SolrQueryEnhanced announcementsSolr;

    @Autowired
    private SolrQueryEnhanced recruitSolr;

    @Autowired
    private SolrQueryEnhanced supplierSolr;

    @Autowired
    private OperationCategoryService operationCategoryService;

    private static final String LOG_MSG = "分类搜索";


    /**
     * 得到分类导航树（频道页用）
     *
     * @param categoryType 分类类型
     */
    public List<SearchCategoryVO> getNavigateTree(CategoryType categoryType) {

        List<SolrQueryBO> dos = new ArrayList<SolrQueryBO>();

        // 没有关键字，查询全部
        dos.add(new SolrQueryBO().setfN("*").setfV("*").setQueryField(true));
        dos.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode1"));
        dos.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode2"));
        dos.add(new SolrQueryBO().setFacetField(true).setfN("operationCategoryCode3"));
        try {
            if (categoryType == CategoryType.BIDDING) {
                return getResultCategory(DataType.BID, BaseUtil.getQueryResponse(announcementsSolr, dos, 0, 0).getFacetFields());
            }
            if (categoryType == CategoryType.SUPPLIER) {
                return getResultCategory(DataType.SUPPLIER, BaseUtil.getQueryResponse(supplierSolr, dos, 0, 0).getFacetFields());
            }
            if (categoryType == CategoryType.RECRUIT) {
                return getResultCategory(DataType.BID, BaseUtil.getQueryResponse(recruitSolr, dos, 0, 0).getFacetFields());
            }


        } catch (SolrServerException e) {
            logger.error(LOG_MSG + "SolrServerException ", e);
        } catch (Exception e) {
            logger.error(LOG_MSG + "错误", e);
        }
        return Collections.<SearchCategoryVO>emptyList();
    }


    private List<SearchCategoryVO> getResultCategory(DataType dataType,List<FacetField> facetFields) {
        List<FacetField.Count> level1FacetResult = new ArrayList<FacetField.Count>();
        List<FacetField.Count> level2FacetResult = new ArrayList<FacetField.Count>();
        List<FacetField.Count> level3FacetResult = new ArrayList<FacetField.Count>();
        // 得到facet的统计结果
        for (FacetField facetField : facetFields) {

            if ("operationCategoryCode1".equals(facetField.getName())) {
                level1FacetResult = facetField.getValues();
            }
            if ("operationCategoryCode2".equals(facetField.getName())) {
                level2FacetResult = facetField.getValues();
            }
            if ("operationCategoryCode3".equals(facetField.getName())) {
                level3FacetResult = facetField.getValues();
            }
        }

        List<String> level1Codes = new ArrayList<String>();
        List<String> level2Codes = new ArrayList<String>();
        List<String> level3Codes = new ArrayList<String>();

        Map<String, Long> level1CodesMap = new HashMap<String, Long>();
        Map<String, Long> level2CodesMap = new HashMap<String, Long>();
        Map<String, Long> level3CodesMap = new HashMap<String, Long>();

        for (FacetField.Count lvl1 : level1FacetResult) {
            level1Codes.add(lvl1.getName());
            level1CodesMap.put(lvl1.getName(), lvl1.getCount());
        }
        for (FacetField.Count lvl2 : level2FacetResult) {
            level2Codes.add(lvl2.getName());
            level2CodesMap.put(lvl2.getName(), lvl2.getCount());
        }
        for (FacetField.Count lvl3 : level3FacetResult) {
            level3Codes.add(lvl3.getName());
            level3CodesMap.put(lvl3.getName(), lvl3.getCount());
        }

        List<SearchCategoryVO> level1Category = BaseUtil.basicCategoryToSearchCategoryVO(dataType,operationCategoryService.getCategoriesByCodes(dataType, level1Codes), level1CodesMap);
        List<SearchCategoryVO> level2Category = BaseUtil.basicCategoryToSearchCategoryVO(dataType,operationCategoryService.getCategoriesByCodes(dataType, level2Codes), level2CodesMap);
        List<SearchCategoryVO> level3Category = BaseUtil.basicCategoryToSearchCategoryVO(dataType,operationCategoryService.getCategoriesByCodes(dataType, level3Codes), level3CodesMap);

        for (SearchCategoryVO level1 : level1Category) {

            List<SearchCategoryVO> level2Nodes = new ArrayList<SearchCategoryVO>();
            for (SearchCategoryVO level2 : level2Category) {
                if (!level1.getCode().equals(level2.getParentCode())) {
                    continue;
                }
                List<SearchCategoryVO> level3Nodes = new ArrayList<SearchCategoryVO>();
                for (SearchCategoryVO level3 : level3Category){
                    if (level2.getCode().equals(level3.getParentCode())){
                        level3Nodes.add(level3);
                    }
                }
                level2.setChildrenList(level3Nodes);
                if (!level3Nodes.isEmpty()){
                    level2Nodes.add(level2);
                }
            }
            level1.setChildrenList(level2Nodes);
        }

        return level1Category;
    }
}

