package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.pojo.item.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 创建时间：2020/12/12
 * 类别表相关业务
 * @author wpf
 */
//使用Dubbo进行远程业务调用时，必须对业务类进行接口编程，因为是直接调用其他服务的业务类
//但使用SpringCloud时不需要，因为是直接调用其他服务的控制器
@Service
@Transactional //配置事务
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父级类别查询
     */
    public List<Category> queryCategoryByParent(Long pid) {

        /*
        Wrappers.query(): 无参方法，一般是用在单表中比较复杂的查询（同时分页，拼接的条件）
        Wrappers.query(T)： 有参方法，一般是用在单表中简单查询（根据指定字段查询）
         */
        Category category = new Category();
        //使用简单查询时，将需要的条件字段封装到category对象中即可
        category.setParentId(pid);
        QueryWrapper<Category> queryWrapper = Wrappers.query(category);

        //执行查询方法
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);//需要一个queryWrapper的参数，封装了查询条件

        return categoryList;
    }

    /**
     * 根据分类ID集合获取分类集合
     */
    public List<Category> findCategoriesByIds(List<Long> categoryIds) {
        List<Category> categoryList = categoryMapper.selectBatchIds(categoryIds);
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        return categoryList;
    }
}
