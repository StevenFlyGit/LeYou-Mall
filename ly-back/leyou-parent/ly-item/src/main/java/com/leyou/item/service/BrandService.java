package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.pojo.item.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 创建时间：2020/12/12
 * 品牌表相关业务
 * @author wpf
 */
@Service
@Transactional
public class BrandService {

    @Autowired
    BrandMapper brandMapper;

    /**
     * 根据分页条件查询品牌数据
     * @param keyWord 用户输入的关键词
     * @param page 当前页码数
     * @param rows 一页显示的行数
     * @param sortBy 排序依据
     * @param isDesc 是否降序
     * @return 查询结果
     */
    public PageResult<Brand> findBrandByPage(String keyWord, Integer page, Integer rows, String sortBy, Boolean isDesc) {

        //构造查询条件对象和分页条件对象
        //将当前页码和每页显示的数量分装进IPage的实现类对象中
        IPage<Brand> brandIPage = new Page<>(page, rows);
        //因为要使用复杂的单表查询，因此狗仔条件对象时不需要传参
        QueryWrapper<Brand> brandWrapper = Wrappers.query();

        //判断关键词是否为空
        if (StringUtils.isNotEmpty(keyWord)) {
            //若非空则加入关键字查询条件
            brandWrapper.like("name", keyWord).or().eq("letter", keyWord.toUpperCase());
        }
        //判断排序条件是否为空
        if (StringUtils.isNotEmpty(sortBy)) {
            if (isDesc) {
                brandWrapper.orderByDesc(sortBy);
            } else {
                brandWrapper.orderByAsc(sortBy);
            }
        }

        //该方法是Mybatis-plus提供的，底层依然是用的PageHelper插件的相关代码
        //需要两个参数，分别代表查询条件和分页条件
        IPage<Brand> selectedPage = brandMapper.selectPage(brandIPage, brandWrapper);

        //构造并封装结果对象
        PageResult<Brand> brandPageResult = new PageResult<>(
                selectedPage.getRecords(),
                selectedPage.getTotal(),
                selectedPage.getPages()
        );

        return brandPageResult;
    }

    /**
     * 将一条品牌表的数据添加到数据库
     * 并将品牌和分类的关系添加到中间表
     * @param brand 需要添加的品牌信息
     * @param categoryIds 需要添加的品牌所属的类别Id
     */
    public void saveOneBrand(Brand brand, List<Long> categoryIds) {
        //封装创建时间
        brand.setCreateTime(new Date());
        //将brand中的letter属性转为大写字母(已经在前端进行了该操作)
//        brand.setLetter(brand.getLetter().toUpperCase());
        //插入到数据库
        //mybatis-plus自动在insert方法的时候，执行select last_insert_id();语句把数据库自增值赋值给对象的主键属性
        brandMapper.insert(brand);
        /*
        之前插入多条数据都是采用代码中for循环的方式
        现在采用动态sql的方式，用一条sql语句添加多条数据
         */
        brandMapper.insertCategoryIdsForBrand(categoryIds, brand.getId());
    }

    /**
     * 根据id查询品牌对象
     */
    public Brand findBrandById(Long brandId) {
        Brand brand = brandMapper.selectById(brandId);
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    /**
     * 根据品牌id批量查询品牌
     */
    public List<Brand> findBrandsByIds(List<Long> brandIds) {
        List<Brand> brandList = brandMapper.selectBatchIds(brandIds);
        if (CollectionUtils.isEmpty(brandList)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brandList;
    }
}
