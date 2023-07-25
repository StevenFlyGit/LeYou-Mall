package com.leyou.common.exception.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 创建时间：2020/12/10
 * 项目自定义异常
 * 用于设置出现异常时返回给前端的数据
 * @author wpf
 */
@Getter
@NoArgsConstructor
public class LyException extends RuntimeException{
    //设置相应状态码
    private Integer statusNum;

    public LyException(Integer statusNum, String message) {
        super(message);
        this.statusNum = statusNum;
    }

    /**
     * 通过自定义的枚举类来构造对象
     */
    public LyException(ExceptionEnum eEnum) {
        super(eEnum.getMessage());
        this.statusNum = eEnum.getStatus();
    }
}
