package com.leyou.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.auth.config.JwtProperty;
import com.leyou.auth.mapper.ApplicationInfoMapper;
import com.leyou.client.user.UserClient;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.pojo.ApplicationInfo;
import com.leyou.common.pojo.Payload;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.pojo.auth.ApplicationTablePojo;
import com.leyou.pojo.user.User;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 创建时间：2020/12/24
 *
 * @author wpf
 */
@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ApplicationInfoMapper appMapper;

    /**
     * 用户登录方法(包含授权过程)
     * @param username 用户名
     * @param password 密码
     * @param response Http响应对象，用于写入Cookie
     */
    public void login(String username, String password, HttpServletResponse response) {
        //调用远程接口，查找用户是否合法
        User user = userClient.findUserByUsernameAndPassword(username, password);
        //封装UserInfo对象
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), "guest");

        //调用写入cookie的方法
        this.getTokenAndWriteToResponse(response, userInfo);

    }

    private void getTokenAndWriteToResponse(HttpServletResponse response, UserInfo userInfo) {
        //利用工具类生成Token
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, jwtProperty.getPrivateKey(),
                jwtProperty.getCookie().getExpire());

        //利用工具类创建Cookie并写入response对象中
        CookieUtils.CookieBuilder cookieBuilder = CookieUtils.newCookieBuilder();
        cookieBuilder.name(jwtProperty.getCookie().getCookieName())
                .value(token)
                .domain(jwtProperty.getCookie().getCookieDomain())
                .response(response).build();
    }

    /**
     * 校验用户信息(检查是否有相应的权限)
     * @return 用户信息
     */
    public UserInfo verify(HttpServletRequest request, HttpServletResponse response) {
        //利用工具类获取并解析token
        Payload<UserInfo> infoFromToken = null;
        try {
            String token = CookieUtils.getCookieValue(request, jwtProperty.getCookie().getCookieName());
            infoFromToken = JwtUtils.getInfoFromToken(token, jwtProperty.getPublicKey(), UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

        //判断当前token是否在黑名单中，如果在，拒绝访问
        if (redisTemplate.hasKey(infoFromToken.getId())) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

        //获取token的过期时间
        Date expiration = infoFromToken.getExpiration();
        //判断是否到达刷新时间
        DateTime refreshDateTime = new DateTime(expiration).minusMinutes(jwtProperty.getCookie().getRefresh());
        if (refreshDateTime.isBeforeNow()) {
            //如果刷新时间在当前时间之前，就重新生成token并写入Cookie
            this.getTokenAndWriteToResponse(response, infoFromToken.getInfo());
            //将之前的token写入Redis黑名单
            log.debug("加入黑名单token的Id值为：" + infoFromToken.getId());
            redisTemplate.opsForValue().set(infoFromToken.getId(), "1",
                    infoFromToken.getExpiration().getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        return infoFromToken.getInfo();
    }

    /**
     * 用户注销账户方法
     * @param request Http请求对象，用于获取Cookie
     */
    public void logout(HttpServletRequest request) {
        //使用工具类获取Cookie值
        String token = CookieUtils.getCookieValue(request, jwtProperty.getCookie().getCookieName());
        //解析token
        Payload<UserInfo> infoFromToken = JwtUtils.getInfoFromToken(token, jwtProperty.getPublicKey(), UserInfo.class);
        //计算token剩余的有效时间的时间毫秒值
        log.debug("加入黑名单token的Id值为：" + infoFromToken.getId());
        long remainTime = infoFromToken.getExpiration().getTime() - System.currentTimeMillis();
        //将Cookie写入Redis的黑名单
        redisTemplate.opsForValue().set(infoFromToken.getId(), "1", remainTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 给微服务分发token的方法
     * @param serviceName 服务名
     * @param secret 给服务分配的密码
     * @return token字符串
     */
    public String applicationAuthorize(String serviceName, String secret) {

        ApplicationTablePojo appPojo = new ApplicationTablePojo();
        appPojo.setServiceName(serviceName);
        //查找给服务名称是否存在
        ApplicationTablePojo queryAppPojo = appMapper.selectOne(Wrappers.query(appPojo));

        if (queryAppPojo != null) {
            //若存在则校验密码是否正确
            if (BCrypt.checkpw(secret, queryAppPojo.getSecret())) {
                /*
                密码校验正确则生成token
                封装applicationInfo
                查询服务的目标权限服务名列表
                 */
                List<String> targetAppNameList = appMapper.selectTargetAppNameListByServiceName(serviceName);

                return JwtUtils.generateTokenExpireInMinutes(new ApplicationInfo(
                        queryAppPojo.getId(), queryAppPojo.getServiceName(), targetAppNameList
                ), jwtProperty.getPrivateKey(), jwtProperty.getApplication().getExpire());
            } else {
                //若密码校验不正确则抛出异常
                throw new LyException(ExceptionEnum.INVALID_SERVER_ID_SECRET);
            }

        } else {
            //不存在则抛出异常
            throw new LyException(ExceptionEnum.INVALID_SERVER_ID_SECRET);
        }

    }
}
