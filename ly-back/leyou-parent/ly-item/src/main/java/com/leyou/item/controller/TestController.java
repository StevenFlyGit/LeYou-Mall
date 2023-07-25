package com.leyou.item.controller;

import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 创建时间：2020/12/10
 * 测试用控制器
 * @author wpf
 */
@RestController
public class TestController {

    /**
     * 运行测试
     */
    @GetMapping("/test")
    public String runTest() {
        return "成功运行！";
    }

    /**
     * 带参运行测试
     */
    @PostMapping("/paramTest")
    public String runTestWithParam(String id) {
        return "id = " + id;
    }

    /**
     * 返回需要的数据并设置参数的固定方法-使用ResponseEntity对象
     */
    @RequestMapping("/statusTest")
    public ResponseEntity<Long> setStatusTest(@RequestParam("id") Long id) {
//        return ResponseEntity.status(200).body(id); //设置status的响应状态码并将返回值设置为响应体
        /*
        常用HttpStatus枚举类进行设置
        OK代表200，一般用于查询正确的返回值
        CREATED代表201，一般用于添加正确的返回值
        NO_CONTENT代表204，一般用于删除和修改正确的返回值
         */
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /**
     * 模拟异常测试
     */
    @RequestMapping("/exceptionTest")
    public ResponseEntity<Long> runWithException(@RequestParam("id") Long id) {
        if (id == 1) {
//            throw new LyException(500, "参数不能为1");
            throw new LyException(ExceptionEnum.INVALID_ORDER_STATUS); //使用自定义枚举类来定义
        }
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }


}
