package com.leyou.page.service;

import com.leyou.client.item.ItemClient;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.dto.item.SpecGroupDto;
import com.leyou.dto.item.SpuDto;
import com.leyou.pojo.item.Brand;
import com.leyou.pojo.item.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建时间：2020/12/22
 * 商品详情静态化页面的相关业务方法
 * @author wpf
 */
@Service
public class StaticPageService {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private SpringTemplateEngine templateEngine; //模板引擎对象

    //获取配置文件中的静态文件的存放地址和名称
    @Value("${ly.staticPage.itemDir}")
    private String staticPagePath;
    @Value("${ly.staticPage.itemTemplate}")
    private String staticPageName;

    /**
     * 获取渲染静态页面模板所需要的数据
     * @param spuId 需要渲染的Spu的Id值
     */
    public Map<String, Object> getGoodDetailPageData(Long spuId) {
        //调用远程接口，查询SpuDto对象
        SpuDto spuDto = itemClient.getSpuDtoBySpuId(spuId);
        //调用远程接口，查询brand对象
        Brand brand = itemClient.findBrandById(spuDto.getBrandId());
        //调用远程接口，查询三层分类的对象集合
        List<Category> categoryList = itemClient.findCategoriesByIds(
            Arrays.asList(spuDto.getCid1(), spuDto.getCid2(), spuDto.getCid3()));
        List<SpecGroupDto> specGroupDtoList = itemClient.getSpecGroupDtoByCategoryId(spuDto.getCid3());

        //封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("categoryList", categoryList);
        map.put("brand", brand);
        map.put("spuName", spuDto.getName());
        map.put("spuSubTitle", spuDto.getSubTitle());
        map.put("spuDetail", spuDto.getSpuDetail());
        map.put("skuList", spuDto.getSkuList());
        map.put("specGroupList", specGroupDtoList);

        return map;
    }

    /**
     * 为某个spu商品创建静态文件的方法
     * 1、创建Context对象：用于存储动态数据
     * 2、读取模板文件（ok） 注意： 程序会自动Thymeleaf的前缀(templates)的目录查询该文件
     * 3、使用模板引擎对象生成静态文件，必须关联输出流(在finally语句块中)，否则后续无法删除该文件
     */
    public void createStaticPage(Long spuId) {
        //1.用Context对象存储对象
        Context context = new Context();
        context.setVariables(this.getGoodDetailPageData(spuId));

        //2.创建输出流用于输出文件
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = Files.newBufferedWriter(Paths.get(staticPagePath + "\\" + spuId + ".html"));

            //使用模板引擎对象生成静态文件
            /*
            参数一：模板文件名称
            参数二：设置Context对象
            参数三：指定输出流
             */
            templateEngine.process(staticPageName + ".html", context, bufferedWriter);

        } catch (IOException e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.PAGE_CREATE_ERROR);
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 删除某个spu商品的详情静态页面
     * @param spuId 要删除的spuId
     */
    public void deleteStaticPage(Long spuId) {
        //读取静态页面文件
        try {
            File file = new File(staticPagePath,spuId + ".html");
            //判空并删除文件
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.PAGE_DELETE_ERROR);
        }
    }
}
