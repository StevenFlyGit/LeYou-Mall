package com.leyou.search.repository;

import com.leyou.pojo.search.GoodsSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 创建时间：2020/12/18
 * ElasticSearch接口，用于搜索商品
 * @author wpf
 */

public interface GoodSearchRepository extends ElasticsearchRepository<GoodsSearch, Long> {

}
