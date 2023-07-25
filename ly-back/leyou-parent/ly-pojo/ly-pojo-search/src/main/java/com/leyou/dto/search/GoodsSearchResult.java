package com.leyou.dto.search;

import com.leyou.common.pojo.PageResult;
import lombok.Data;

import java.util.Map;

/**
 * 创建时间：2020/12/19
 * 商品搜索结果的返回值类
 * @author wpf
 */
@Data
public class GoodsSearchResult<T> extends PageResult<T> {
    private Map<String, Object> filterConditions;
}
