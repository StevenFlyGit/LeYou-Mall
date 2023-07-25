package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.dto.item.SpecGroupDto;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.pojo.item.SpecGroup;
import com.leyou.pojo.item.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 创建时间：2020/12/15
 * 商品规格表相关业务
 * @author wpf
 */
@Service
@Transactional
public class SpecService {

    @Autowired
    SpecGroupMapper specGroupMapper;
    @Autowired
    SpecParamMapper specParamMapper;

    /**
     * 根据分类查询规格组
     */
    public List<SpecGroup> findSpecGroupByCategoryId(Long categoryId) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(categoryId);

        QueryWrapper<SpecGroup> wrapper = Wrappers.query(specGroup);

        List<SpecGroup> specGroupList = specGroupMapper.selectList(wrapper);

        // if (specGroupList == null || specGroupList.isEmpty()) {} //与下面的方法等价
        if (CollectionUtils.isEmpty(specGroupList)) {
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return specGroupList;
    }

    /**
     * 查询规格的通用方法
     * @param groupId 规格组Id
     * @param categoryId 商品类别Id
     * @param searching 是否搜素
     * @return 查询到的结果集合
     */
    public List<SpecParam> findSpecParam(Long groupId, Long categoryId, Boolean searching) {
        //注意：MyBatis-plus底层会对查询条件的值自动判断空，如果为空则不追加条件
        SpecParam specParam = new SpecParam();
        specParam = new SpecParam();
        specParam.setSearching(searching);
        specParam.setCid(categoryId);
        specParam.setGroupId(groupId);

        QueryWrapper<SpecParam> wrapper = Wrappers.query(specParam);

        List<SpecParam> specParams = specParamMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(specParams)) {
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }

        return specParams;
    }

    /**
     * 根据分类id查询规格组和组内参数
     */
    public List<SpecGroupDto> SpecGroupDtoByCategoryId(Long categoryId) {
        //调用原方法，查询specGroupList
        List<SpecGroup> specGroupList = this.findSpecGroupByCategoryId(categoryId);
        //利用工具类拷贝数据
        List<SpecGroupDto> specGroupDtoList = BeanHelper.copyWithCollection(specGroupList, SpecGroupDto.class);
        //遍历集合，注入组内参数集合
        specGroupDtoList.forEach(specGroupDto -> {
            List<SpecParam> specParam = this.findSpecParam(null, categoryId, null);
            specGroupDto.setSpecParamList(specParam);
        });

        return specGroupDtoList;
    }
}
