package com.leyou.user.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.leyou.common.constants.LyConstants;
import com.leyou.common.constants.MQConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.pojo.user.Address;
import com.leyou.user.mapper.UserMapper;
import com.leyou.pojo.user.User;
import org.apache.commons.lang.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 创建时间：2020/12/23
 * 门户网站用户相关业务
 * @author wpf
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate; //spring-data-redis模板类(所有泛型变量都使用String类型时使用)
    /*
    RedisTemplate可以定义两个泛型，分表代表key和value的对象类型(一般key直接用String)
    对应Redis中的五种数据类型
    String：等同于java中的，`Map<String,String>`
    list：等同于java中的`Map<String,List<String>>`
    set：等同于java中的`Map<String,Set<String>>`
    sort_set：可排序的set
    hash：等同于java中的：`Map<String,Map<String,String>>`
     */
//    @Autowired
//    RedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 用于校验用户的数据的唯一性
     * @param data 需要校验的数据
     * @param type 数据类型，1代表用户名，2代表手机号
     * @return 数据是否唯一
     */
    public Boolean checkUserData(String data, Integer type) {

        try {
            User user = new User();

            switch (type) {
                case 1:
                    user.setUsername(data);
                    break;
                case 2:
                    user.setPhone(data);
                    break;
            }
            return userMapper.selectCount(Wrappers.query(user)) == 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.USER_DATA_CHECK_ERROR);
        }
    }

    /**
     * 发送短信验证码的业务
     * 1.将随机生成的验证码存入redis中
     * 2.利用阿里云的工具类发送短信
     * @param phoneNum 需要发送短信的手机号
     */
    public void sendMessage(String phoneNum) {
        //生成验证码
        String randomCode = RandomStringUtils.randomNumeric(6);

        //将验证码存入Redis中，设置过期时间为5分钟
        redisTemplate.opsForValue().set(LyConstants.REDIS_KEY_PRE + phoneNum, randomCode, 5, TimeUnit.MINUTES);

        System.out.println("randomCode = " + randomCode);
        //用map来封装验证码和电话号码
        Map<String, String> map = new HashMap<>();
        map.put("phoneNum", phoneNum);
        map.put("randomCode", randomCode);
        //发送消费内容到消费队列，供消费者发送短信
        amqpTemplate.convertAndSend(MQConstants.Exchange.SMS_EXCHANGE_NAME, MQConstants.RoutingKey.VERIFY_CODE_KEY, map);
    }

    /**
     * 用户注册的方法
     * @param user 前端传入的用户参数
     * @param randomCodeInput 用户输入的验证码
     */
    public void userRegister(User user, String randomCodeInput) {
        //从redis中取出数据
        String realRandomCode = redisTemplate.opsForValue().get(LyConstants.REDIS_KEY_PRE + user.getPhone());
        //判断验证码的一致性
        if (realRandomCode != null && realRandomCode.equals(randomCodeInput)) {
            //将密码加密
            String encodePassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(encodePassword);
            //将用户数据保存到数据库
            userMapper.insert(user);
        } else {
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
    }

    /**
     * 根据用户名和密码查询用户
     */
    public User queryUserByUsernameAndPassword(String username, String password) {
        //验证用户名对应的用户是否存在
        User user = new User();
        user.setUsername(username);

        User queryUser = userMapper.selectOne(Wrappers.query(user));

        if (queryUser == null) {
            throw new LyException(ExceptionEnum.USER_NOT_FOUND);
        }

        //校验密码是否正确
        //第1个参数是待校验的密码，第2个参数是已加密过的真实密码
        if (!BCrypt.checkpw(password, queryUser.getPassword())) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

        return queryUser;
    }

    /**
     * 根据Id查找地址(暂时没连数据库)
     */
    public Address findAddressById(Long userId, Long addressId) {
        Address address = new Address();

        address.setId(addressId);
        address.setStreet("珠吉路58号津安创业园一层黑马程序员");
        address.setCity("广州");
        address.setDistrict("天河区");
        address.setAddressee("小飞飞");
        address.setPhone("15800000000");
        address.setProvince("广东");
        address.setPostcode("510000");
        address.setIsDefault(true);

        return address;
    }
}
