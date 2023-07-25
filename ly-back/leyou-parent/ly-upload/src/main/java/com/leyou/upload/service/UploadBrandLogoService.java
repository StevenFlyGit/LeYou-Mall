package com.leyou.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.constants.LyConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.upload.config.OssProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 创建时间：2020/12/13
 * 上传品牌logo的控业务类
 * @author wpf
 */
@Service
public class UploadBrandLogoService {

    /**
     * 上传logo图片到本地服务器
     * 此方法并没有将图片路径保存到数据库中，只是上传了图片到本地的Nginx服务器中
     * @param logoImageFile 前端传输的文件对象
     * @return 回显的Url地址
     */
    public String uploadLogoInLocalhost(MultipartFile logoImageFile) {
        //判断上传的文件对象是否为图片
        try {
            BufferedImage imageStream = ImageIO.read(logoImageFile.getInputStream());
            if (imageStream == null) {
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }

        //获取文件的原始名称
        String originalFilename = logoImageFile.getOriginalFilename();
        //截取文件后缀名
        String fileEnd = originalFilename.substring(originalFilename.lastIndexOf("."));
        //得到随机生成的文件名
        String fileName = UUID.randomUUID().toString() + "." + fileEnd;

        File uploadFile = new File(LyConstants.LOGO_IMAGE_PATH, fileName);

        //上传文件到服务器
        try {
            logoImageFile.transferTo(uploadFile);
            //返回图片访问路径
            return LyConstants.LOGO_IMAGE_URL + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
    }

    @Autowired //注入OSS对象
    private OSS ossClient;

    @Autowired //注入配置对象
    private OssProperties ossProperty;

    /**
     * 获取阿里云的签名
     */
    public Map<String, String> getOosSignature() {

//        String accessId = "<yourAccessKeyId>"; // 请填写您的AccessKeyId。
//        String accessKey = "<yourAccessKeySecret>"; // 请填写您的AccessKeySecret。
//        String endpoint = "oss-cn-hangzhou.aliyuncs.com"; // 请填写您的 endpoint。
//        String bucket = "bucket-name"; // 请填写您的 bucketname 。
//        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
//        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
//        String callbackUrl = "http://88.88.88.88:8888";
//        String dir = "user-dir-prefix/"; // 用户上传文件时指定的前缀。

        // 创建OSSClient实例。
        try {
            long expireTime = ossProperty.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, ossProperty.getDir());

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessId", ossProperty.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", ossProperty.getDir());
            respMap.put("host", ossProperty.getHost());
            respMap.put("expire", String.valueOf(expireEndTime));
            // respMap.put("expire", formatISO8601Date(expiration));

//            JSONObject jasonCallback = new JSONObject();
//            jasonCallback.put("callbackUrl", callbackUrl);
//            jasonCallback.put("callbackBody",
//                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
//            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
//            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
//            respMap.put("callback", base64CallbackBody);
//
//            JSONObject ja1 = JSONObject.fromObject(respMap);
//            // System.out.println(ja1.toString());
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            response.setHeader("Access-Control-Allow-Methods", "GET, POST");
//            response(request, response, ja1.toString());
            return respMap;
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            // System.out.println(e.getMessage());
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_SIGN);
        } finally {
            ossClient.shutdown();
        }
    }
}
