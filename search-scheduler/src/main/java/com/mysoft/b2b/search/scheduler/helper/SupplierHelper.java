package com.mysoft.b2b.search.scheduler.helper;

import com.mysoft.b2b.search.spi.supplier.SupplierScoreItem;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 招标预告定时任务辅助工具
 * @author ganq
 *
 */
@Component("supplierHelper")
public class SupplierHelper extends SearchHelper{

	private static final Logger logger = Logger.getLogger(SupplierHelper.class);

    @Autowired
    private JdbcTemplate jdbcSearch;

    private List<Map<String,Object>> scoreList;

    private void fillSortScore(){
        String sql = "select * from supplier_sort_score_item";
        scoreList = jdbcSearch.queryForList(sql);
    }

	/**
	 * 获取供应商的排序分值
	 */
	public double getSupplierSortScore(SupplierScoreItem supplierScoreItem ){
		double totalScore = 0.0d;
		if (supplierScoreItem == null){
            logger.info("-------供应商得分对象为null,返回0分");
            return totalScore;
        }

        fillSortScore();

        for (Map<String,Object> map : scoreList){
            // 得分项目名称
            String scoreItemName = ObjectUtils.toString(map.get("item"));
            // 该项目单项得分
            Integer score = NumberUtils.toInt(ObjectUtils.toString(map.get("score")));
            // 该项目单项最大分值
            Integer maxScore = NumberUtils.toInt(ObjectUtils.toString(map.get("max_score")));
            // 该项得分基数 (即相乘基数)
            Integer scoreBase = 0;
            try {
                scoreBase = NumberUtils.toInt(ObjectUtils.toString(supplierScoreItem.getItemMap().get(scoreItemName)));
            } catch (Exception e) {
                logger.info("-------获取得分基数失败----");
                e.printStackTrace();
            }

            if (score > 0 && scoreBase > 0){
                Integer temp = score * scoreBase;
                if (temp > maxScore){
                    temp = maxScore;
                }
                totalScore += temp;
            }
        }
        // 最后加上一个0 - 1之间的随机小数，用于打分相等的供应商之间随机展示，打分不等的供应商不影响
        totalScore += NumberUtils.toDouble(String.format("%.2f", new Random().nextDouble()));
		return totalScore;
	}

}
