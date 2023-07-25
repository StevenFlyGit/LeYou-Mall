package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.pojo.item.Brand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 创建时间：2020/12/12
 * 品牌表业务Dao层接口
 * @author wpf
 */

public interface BrandMapper extends BaseMapper<Brand> {

    void insertCategoryIdsForBrand(@Param("cids") List<Long> categoryIds, @Param("bId") Long brandId);
}
