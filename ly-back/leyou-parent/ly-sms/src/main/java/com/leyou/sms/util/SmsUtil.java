package com.leyou.sms.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.leyou.common.constants.SmsConstants;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 创建时间：2020/12/23
 * 利用阿里云的API来发送短信的工具类
 * @author wpf
 */
@Component
public class SmsUtil {

    @Autowired
    private SmsProperty smsProperty;

    @Autowired
    private IAcsClient iAcsClient;

    /**
     * 利用阿里云短信发送短信验证码的方法
     * @param phoneNum 需要发送短信的手机号
     * @param randomCode 需要发送的随机验证码
     * @return 阿里云的相应结果，是一个Json字符串，用工具类转成Map结构在返回
     */
    public Map<String, String> sendSms(String phoneNum, String randomCode) {

        CommonRequest request = new CommonRequest();

        request.setMethod(MethodType.POST);
        request.setDomain(smsProperty.getDomain());
        request.setVersion(smsProperty.getVersion());
        request.setAction(smsProperty.getAction());
        request.putQueryParameter(SmsConstants.SMS_PARAM_REGION_ID, smsProperty.getRegionID());
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_PHONE, phoneNum);
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_SIGN_NAME, smsProperty.getSignName());
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_TEMPLATE_CODE, smsProperty.getVerifyCodeTemplate());
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_TEMPLATE_PARAM,
                "{\""+ smsProperty.getCode() +"\":\""+ randomCode +"\"}");
        try {
            CommonResponse response = iAcsClient.getCommonResponse(request);
//            System.out.println(response.getData());
            String resultData = response.getData();
            //利用工具类将Json字符串转为Map集合并返回
            return JsonUtils.toMap(resultData, String.class, String.class);
        } catch (ClientException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
