package com.leyou.pojo.search;

import lombok.Data;

import java.util.Map;

/**
 * 创建时间：2020/12/19
 * 接受商品搜素请求传递到后端的参数
 * @author wpf
 */
@Data
public class GoodSearchRequest {

    private String key;// 搜索条件
    private Integer pageSize;
    private Integer page;// 当前页
    private Map<String, Object> filterParams; //用户选择的过滤条件

    private static final Integer DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    //对页面传递过来的page参数进行一定的处理
    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        /*
        public static int max(int a, int b) {
        return (a >= b) ? a : b;
        }
         */
        return Math.max(page, DEFAULT_PAGE); //获取较大值
    }

    public Integer getPageSize() {
        return DEFAULT_SIZE;
    }
}
