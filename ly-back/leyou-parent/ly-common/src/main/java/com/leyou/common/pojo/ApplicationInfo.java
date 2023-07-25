package com.leyou.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建时间：2020/12/26
 * token中封装的服务信息
 * @author wpf
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationInfo {
    private Long id;//服务ID
    private String serviceName;//当前服务名称
    private List<String> targetList;//当前服务的目标服务名称列表
}
