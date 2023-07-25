package com.leyou.upload.controller;

import com.leyou.upload.service.UploadBrandLogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 创建时间：2020/12/13
 * 上传品牌logo的控制器
 * @author wpf
 */
@RestController
public class UploadBrandLogoController {

    @Autowired
    UploadBrandLogoService uploadBrandLogoService;

    /**
     * 上传logo图片到本地服务器
     * @param logoImageFile 前端传输的文件对象
     * @return 回显的Url地址
     */
    @PostMapping("/uploadLogo")
    public ResponseEntity<String> uploadLogoInLocalhost(@RequestParam("file") MultipartFile logoImageFile) {

        String resultUrl = uploadBrandLogoService.uploadLogoInLocalhost(logoImageFile);

        return ResponseEntity.ok(resultUrl);
    }

    /**
     * 获取阿里云的签名
     */
    @GetMapping("/signature")
    public ResponseEntity<Map<String, String>> getOosSignature() {
        Map<String, String> signatureMap = uploadBrandLogoService.getOosSignature();
        return ResponseEntity.ok(signatureMap);
    }

}
