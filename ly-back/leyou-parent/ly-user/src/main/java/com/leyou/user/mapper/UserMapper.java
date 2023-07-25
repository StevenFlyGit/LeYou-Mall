package com.leyou.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.pojo.user.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 创建时间：2020/12/23
 * 查询用户表的接口
 * @author wpf
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
