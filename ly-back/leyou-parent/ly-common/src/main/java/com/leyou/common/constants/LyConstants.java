package com.leyou.common.constants;

/**
 * 创建时间：2020/12/13
 * 项目中使用到的常量
 * @author wpf
 */

public class LyConstants {
    //上传品牌Logo图片到Ngnix服务器的地址
    public static final String LOGO_IMAGE_PATH = "G:\\Web_Server\\nginx-1.15.3\\html\\brand-logo";

    //返回给前端的Logo图片回显地址
    public static final String LOGO_IMAGE_URL = "http://localhost/brand-logo/";

    /*注册时短信验证码在redis中的key的前缀*/
    public static final String REDIS_KEY_PRE = "REDIS_KEY_PRE";

    /**
     * token刷新间隔
     */
    public static final long TOKEN_REFRESH_INTERVAL = 86400000L; //24小时

    /**
     * token获取失败后重试的间隔
     */
    public static final long TOKEN_RETRY_INTERVAL = 10000L;

    /*服务认证请求头*/
    public static final String APP_TOKEN_HEADER = "APP_TOKEN_HEADER";

}
