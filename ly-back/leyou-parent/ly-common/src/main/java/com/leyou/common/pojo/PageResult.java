package com.leyou.common.pojo;

import lombok.Data;

import java.util.List;

/**
 * 创建时间：2020/12/12
 * 通用结果分页类
 * @author wpf
 */
@Data
public class PageResult<T> {

    private List<T> items; //数据列表
    private Long total;//总记录数
    private Long totalPage;//总页数

    public PageResult(List<T> items, Long total, Long totalPage) {
        this.items = items;
        this.total = total;
        this.totalPage = totalPage;
    }

    public PageResult() {}
}
