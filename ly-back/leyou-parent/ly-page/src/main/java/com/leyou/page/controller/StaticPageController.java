package com.leyou.page.controller;

import com.leyou.page.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 创建时间：2020/12/21
 * 商品详情静态页面控制器
 * @author wpf
 */
@Controller
@RequestMapping("/page")
public class StaticPageController {

    @Autowired
    StaticPageService staticPageService;

    /**
     * 获取渲染静态页面模板所需要的数据
     * @param spuId 需要渲染的Spu的Id值
     * @return 模板页面名称
     */
    @GetMapping("/item/{id}.html")
    public String loadGoodsMsgOnServer(@PathVariable("id") Long spuId, Model model) {

        //获取商品详情需要的数据
        /*
        key：参数的别名
        value：参数值
        */
        Map<String, Object> goodDetailDataMap = staticPageService.getGoodDetailPageData(spuId);
        //给页面传递数据
        model.addAllAttributes(goodDetailDataMap);

        //返回页面模板的名称即可(不用加.html后缀)，Thymeleaf底层会自动根据配置的模板页面路径来寻找对应名称的模板路径
        return "item";
    }

}
