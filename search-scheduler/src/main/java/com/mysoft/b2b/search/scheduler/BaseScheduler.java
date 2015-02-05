package com.mysoft.b2b.search.scheduler;

import com.mysoft.b2b.commons.scheduler.MysoftJob;
import com.mysoft.b2b.search.scheduler.helper.SchedulerThreadData;
import com.mysoft.b2b.search.scheduler.helper.SearchHelper;
import com.mysoft.b2b.search.spi.SearchModel;
import com.mysoft.b2b.search.spi.SearchSPIService;
import com.mysoft.b2b.search.utils.PropertiesUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 搜索定时任务基类
 * @author ganq
 */
public abstract class BaseScheduler<T extends SearchModel> extends MysoftJob implements SchedulerThreadData {

    /**
     * 是否首次导入
     */
    private boolean isFirstImport;

    /**
     * 最后修改时间
     */
    private Long lastModifyTime;

    /**
     * 时间key
     */
    private String dateKey;

    private SolrServer solrServer;

    private SearchSPIService<T> searchSPIService;

    private SearchHelper searchHelper;

    abstract void init();
    abstract List<SolrInputDocument> buildIndexList(List<T> dataList);

    @Override
    public void run() {
        try {
            // 构建索引
            buildIndexes();
            // 删除失效的索引
            deleteInvalidIndex();
        } catch (SolrServerException e) {
            logger.error(getJobName() + "构建SolrServer异常:", e);
        } catch (IOException e) {
            logger.error(getJobName() + "构建IO异常:", e);
        } catch (Exception e){
            logger.error(getJobName() + "构建异常:", e);
        }
    }

    @Override
    public T getSingleDataObj(String id) {
        T t = searchSPIService.getSearchModelById(id);
        if (t == null) {
            logger.error(getJobName() + "Id 为“"+id+"”通过spi接口获取数据异常，将不导入索引库！！！");
            return null;
        }
        return t;
    }

    /**
     * 删除失效索引
     * @throws SolrServerException
     * @throws IOException
     */
    void deleteInvalidIndex() throws SolrServerException, IOException{
        if (lastModifyTime == null) {
            logger.error("lastModifyTime获取异常.....将不执行删除失效索引方法");
            return;
        }
        Set<String> invalidIds = searchSPIService.getInvalidIdsByLastModifyTime(lastModifyTime);
        if (CollectionUtils.isEmpty(invalidIds)) {
            return;
        }
        solrServer.deleteById(new ArrayList<String>(invalidIds));
        solrServer.commit();
        logger.info(getJobName() +"删除不可用的索引" + invalidIds.size() + "条,他们的id为：" + StringUtils.join(invalidIds,","));
    }

    /**
     * 构建索引
     * @throws SolrServerException
     * @throws IOException
     */
    void buildIndexes() throws SolrServerException, IOException {
        List<T> dataList = getInfo();
        if (CollectionUtils.isEmpty(dataList)) {
            logger.info(getJobName() + "此次构建没有数据更新！");
            return;
        }

        List<SolrInputDocument> indexList = buildIndexList(dataList);

        sendToSolr(indexList);
    }

    /**
     * 发送到solr
     * @param indexList
     * @throws SolrServerException
     * @throws IOException
     */
    void sendToSolr(List<SolrInputDocument> indexList)  throws SolrServerException, IOException{
        if (CollectionUtils.isEmpty(indexList)){
            logger.info(getJobName() + "本次没有索引数据更新！！！");
            return ;
        }

        if (isFirstImport) {
            solrServer.deleteByQuery("*:*");
        }
        solrServer.add(indexList);
        solrServer.commit();

        logger.info("成功更新"+getJobName() + "索引" + getJobName() + indexList.size() + "条数据！！！");
    }

    /**
     * 获取索引信息
     * @return
     */
    List<T> getInfo(){
        String date = PropertiesUtil.getKey(PropertiesUtil.solrDateProp,dateKey);

        Set<String> ids;

        if ("".equals(date)) {
            isFirstImport = true;
            ids = searchSPIService.getAllId();
        } else {
            isFirstImport = false;
            ids = searchSPIService.getIdsByLastModifyTime(NumberUtils.toLong(date));
            if (!CollectionUtils.isEmpty(ids)) {
                logger.info(getJobName() + "本次增量更新的Id为：" + StringUtils.join(ids, ","));
            }
        }

        // 根据lastmodifydate 判断是否要增量更新
        lastModifyTime = searchSPIService.getNewestLastModifyTime();
        PropertiesUtil.setSolrDateKey(dateKey, ObjectUtils.toString(lastModifyTime));

        if (ids == null) {
            logger.info(getJobName() + "通过spi接口获取id数据异常");
            return null;
        }

        List<T> list = searchHelper.getDataByThread(ids, 10, this);

        logger.info(getJobName() + "本次待插入索引数据" + list.size() + "条");
        return list;
    }

    public String getDateKey() {
        return dateKey;
    }

    public void setDateKey(String dateKey) {
        this.dateKey = dateKey;
    }

    public SolrServer getSolrServer() {
        return solrServer;
    }

    public void setSolrServer(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public SearchSPIService getSearchSPIService() {
        return searchSPIService;
    }

    public void setSearchSPIService(SearchSPIService searchSPIService) {
        this.searchSPIService = searchSPIService;
    }

    public SearchHelper getSearchHelper() {
        return searchHelper;
    }

    public void setSearchHelper(SearchHelper searchHelper) {
        this.searchHelper = searchHelper;
    }
}
