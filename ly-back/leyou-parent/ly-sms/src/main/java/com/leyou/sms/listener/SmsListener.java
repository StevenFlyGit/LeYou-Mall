package com.leyou.sms.listener;

import com.leyou.common.constants.MQConstants;
import com.leyou.common.constants.SmsConstants;
import com.leyou.sms.util.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 创建时间：2020/12/23
 * 监听发送短信消费队列中的消息
 * @author wpf
 */
@Component
@Slf4j
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MQConstants.Queue.SMS_VERIFY_CODE_QUEUE),
        exchange = @Exchange(name = MQConstants.Exchange.SMS_EXCHANGE_NAME),
        key = MQConstants.RoutingKey.VERIFY_CODE_KEY)
    )
    public void SendRegisterMsg(Map<String, String> map) {
        try {
            //取出验证码和手机号
            String phoneNum = map.get("phoneNum");
            String randomCode = map.get("randomCode");
            //利用工具类对象发送短信
            log.debug("发送短信的参数格式 = " + "{\""+ "code" +"\":\""+ randomCode +"\"}");
//            Map<String, String> msgResult = smsUtil.sendSms(phoneNum, randomCode);
//            //判断发送返回的结果是否正确(此处在后续可以扩展业务)
//            if (SmsConstants.OK.equals(msgResult.get(SmsConstants.SMS_RESPONSE_KEY_MESSAGE)) &&
//                    SmsConstants.OK.equals(msgResult.get(SmsConstants.SMS_RESPONSE_KEY_CODE))) {
//                log.info("【短信业务】发送短信成功");
//            } else {
//                log.error("【短信业务】发送短信失败，原因：" + msgResult.get(SmsConstants.SMS_RESPONSE_KEY_MESSAGE));
//            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【短信业务】发送短信失败，原因："+e.getMessage());
        }
    }

}
