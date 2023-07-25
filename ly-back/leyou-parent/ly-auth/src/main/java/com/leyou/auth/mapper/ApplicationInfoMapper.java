package com.leyou.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.pojo.auth.ApplicationTablePojo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 创建时间：2020/12/26
 * Application实体类的Dao接口
 * @author wpf
 */
@Mapper
public interface ApplicationInfoMapper extends BaseMapper<ApplicationTablePojo> {
    List<String> selectTargetAppNameListByServiceName(String serviceName);
}
