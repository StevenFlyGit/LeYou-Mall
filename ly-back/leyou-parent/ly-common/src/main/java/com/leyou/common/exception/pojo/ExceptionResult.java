package com.leyou.common.exception.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * 创建时间：2020/12/10
 * 异常同意处理后的返回值对象
 * @author wpf
 */
@Getter
@NoArgsConstructor
public class ExceptionResult {
    private Integer status;
    private String message;
    private String timestamp;

    /**
     * 传入自定义异常的构造方法
     * @param e 自定义异常
     */
    public ExceptionResult(LyException e) {
        this.status = e.getStatusNum();
        this.message = e.getMessage();
        //利用DateTime时间工具类来创建当前时间的格式
        this.timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }
}
