package com.leyou.common.exception;

import com.leyou.common.exception.pojo.ExceptionResult;
import com.leyou.common.exception.pojo.LyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 创建时间：2020/12/10
 * 全局异常拦截器，让SpringMVC能处理自定义的异常类
 * 利用了AOP的异常通知，进行全局拦截
 * @author wpf
 */
@ControllerAdvice //该注解会把所有Controller的异常拦截
public class LyExceptionController {

    /**
     * 捕获异常的方法
     * @param e 需要捕获的异常
     * @return ResponseEntity返回值处理对象
     */
//    @ExceptionHandler(LyException.class) //注明需要捕获的异常
//    @ResponseBody //将结果转为Json字符串
//    public ResponseEntity<LyException> catchAllLyException(LyException e) {
//        //将业务方法中抛出的自定义异常返回
//        return ResponseEntity.status(e.getStatusNum()).body(e);
//    }

    /**
     * 捕获异常的方法
     * @param e 需要捕获的异常
     * @return ResponseEntity返回值处理对象
     */
    @ExceptionHandler(LyException.class) //注明需要捕获的异常
    @ResponseBody //将结果转为Json字符串
    public ResponseEntity<ExceptionResult> catchAllLyExceptionByResult(LyException e) {
        //将业务方法中抛出的自定义异常中设置到定义好的实体类中返回
        return ResponseEntity.status(e.getStatusNum()).body(new ExceptionResult(e));
    }

}
