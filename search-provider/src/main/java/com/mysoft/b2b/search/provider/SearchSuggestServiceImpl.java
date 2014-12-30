package com.mysoft.b2b.search.provider;

import com.mysoft.b2b.search.api.SearchSuggestService;
import com.mysoft.b2b.search.solr.SolrQueryEnhanced;
import com.mysoft.b2b.search.util.BaseUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("searchSuggestService")
public class SearchSuggestServiceImpl implements SearchSuggestService {

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private SolrQueryEnhanced announcementsSolr;
	
	@Autowired
	private SolrQueryEnhanced developerSolr;
	
	@Autowired
	private SolrQueryEnhanced supplierSolr;

    @Autowired
    private SolrQueryEnhanced recruitSolr;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Long> getSearchSuggestion(String keyword, String module) {
		Map<String,Long> resultMap = new HashMap<String, Long>();
        if (StringUtils.isBlank(module)){
            return null;
        }
		if (module.contains(",")){
            String [] modules = module.split(",");
            for (String m : modules){
                resultMap.putAll(getResultByModule(keyword,m));
            }
        }else{
            return getResultByModule(keyword,module);
        }

		return BaseUtil.deleteMapItemByCount(BaseUtil.sortByValue(resultMap,true), 10);
	}


    private Map<String, Long> getResultByModule(String keyword, String module) {
        Map<String,Long> resultMap = new HashMap<String, Long>();

        if ("announcement".equals(module)) {
            //招标预告标题中文和拼音
            resultMap.putAll(BaseUtil.getChineseSuggest(announcementsSolr.getSolrServer(), keyword, "titleSuggest", 10));
            resultMap.putAll(BaseUtil.getPinyinSuggest(announcementsSolr.getSolrServer(), keyword, "titlePinyin","titleSuggest", 10));
        }
        if ("supplier".equals(module)) {
            //供应商名称中文和拼音
            resultMap.putAll(BaseUtil.getChineseSuggest(supplierSolr.getSolrServer(),keyword, "companyNameSuggest",10));
            resultMap.putAll(BaseUtil.getPinyinSuggest(supplierSolr.getSolrServer(),keyword, "companyNamePinyin","companyNameSuggest",10));
        }
        if ("developer".equals(module)) {
            //开发商名称中文和拼音
            resultMap.putAll(BaseUtil.getChineseSuggest(developerSolr.getSolrServer(),keyword, "nameSuggest",10));
            resultMap.putAll(BaseUtil.getPinyinSuggest(developerSolr.getSolrServer(),keyword, "namePinyin","nameSuggest",10));
        }
        if ("recruit".equals(module)) {
            //招募名称中文和拼音
            resultMap.putAll(BaseUtil.getChineseSuggest(recruitSolr.getSolrServer(),keyword, "subjectSuggest",10));
            resultMap.putAll(BaseUtil.getPinyinSuggest(recruitSolr.getSolrServer(),keyword, "subjectPinyin","subjectSuggest",10));
        }


        return BaseUtil.deleteMapItemByCount(BaseUtil.sortByValue(resultMap,true), 10);
    }
	

}
