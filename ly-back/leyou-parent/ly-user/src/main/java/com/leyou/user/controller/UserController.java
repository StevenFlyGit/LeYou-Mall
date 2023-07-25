package com.leyou.user.controller;

import com.leyou.common.exception.pojo.LyException;
import com.leyou.pojo.user.Address;
import com.leyou.pojo.user.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 创建时间：2020/12/23
 * 门户网站用户控制器
 * @author wpf
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用于校验用户的数据的唯一性
     * @param data 需要校验的数据
     * @param type 数据类型，1代表用户名，2代表手机号
     * @return 数据是否唯一
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUsernameAndPhone(
            @PathVariable("data") String data, @PathVariable("type") Integer type
    ) {
        return ResponseEntity.ok(userService.checkUserData(data, type));
    }

    /**
     * 发送给用户注册验证码
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendMessage(@RequestParam("phone") String phoneNum) {
        userService.sendMessage(phoneNum);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户注册的方法
     * *@Valid注解用于数据校验(可以在对应的实体类中添加数据校验的规则)
     * @param errorResult 可以用于定义数据校验不通过时返回的错误信息，但必须加在需要校验的数据后方
     * @param user 前端传入的用户参数
     * @param randomCodeInput 用户输入的验证码
     */
    @PostMapping("/register")
    public ResponseEntity<Void> userRegister(@Valid User user, BindingResult errorResult,
                                             @RequestParam("code") String randomCodeInput) {
        if (errorResult.hasErrors()) { //判断是否包含错误(FieldError是DefaultMessageSourceResolvable的子类)
            String errorMsg = errorResult.getFieldErrors().stream().map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("|"));
            //抛出自定义异常
            throw new LyException(500, errorMsg);
        }

        userService.userRegister(user, randomCodeInput);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 根据用户名和密码查询用户
     */
    @GetMapping("/query")
    public ResponseEntity<User> findUserByUsernameAndPassword(@RequestParam("username") String username,
        @RequestParam("password") String password) {
        return ResponseEntity.ok(userService.queryUserByUsernameAndPassword(username, password));
    }

    /**
     * 根据Id查找地址
     */
    @GetMapping("/address")
    public ResponseEntity<Address> findAddressById(
            @RequestParam("userId") Long userId,
            @RequestParam("id") Long addressId
    ) {
        return ResponseEntity.ok(userService.findAddressById(userId, addressId));
    }
}
