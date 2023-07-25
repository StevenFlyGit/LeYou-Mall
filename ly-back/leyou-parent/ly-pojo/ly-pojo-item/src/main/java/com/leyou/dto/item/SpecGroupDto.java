package com.leyou.dto.item;

import com.leyou.pojo.item.SpecGroup;
import com.leyou.pojo.item.SpecParam;
import lombok.Data;

import java.util.List;

/**
 * 创建时间：2020/12/21
 * 用于前后端封装数据的SpecGroup对象，包含组内参数
 * @author wpf
 */
@Data
public class SpecGroupDto extends SpecGroup {
    List<SpecParam> specParamList;

}
