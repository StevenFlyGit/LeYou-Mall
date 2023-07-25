package com.leyou.gateway.scheduler;

import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 创建时间：2020/12/27
 * SpringTask时间调度案例类
 * @author wpf
 */
//@Component
public class SchedulingDemo {


    @Scheduled(fixedRate = 5000) //表示从上一个任务开始到下一个任务开始的间隔，单位是毫秒
    public void testFixedRate() {
        System.out.println("testFixedRate任务已执行，执行时间：" + DateTime.now());
    }

    @Scheduled(fixedDelay = 5000) //表示从上一个任务完成开始到下一个任务开始的间隔，单位是毫秒
    public void testFixedDelay() throws InterruptedException {
        System.out.println("testFixedDelay任务已执行，执行时间：" + DateTime.now());
        Thread.sleep(2000);
    }

    /**
     * 周：？ * ，- /
     * 月：* ， - /
     * 日：？ * ，- /
     * 时：* ， - /
     * 分：* ， - /
     * 秒：* ， - /
     */
    @Scheduled(cron = "0/7 * * * * ?") //代表秒数为7的倍数时执行一次，日/周中至少一个为？
    public void testCorn() {
        System.out.println("testCorn任务已执行，执行时间：" + DateTime.now());
    }

}
