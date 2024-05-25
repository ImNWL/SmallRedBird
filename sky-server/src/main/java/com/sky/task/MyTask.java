package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyTask {
    /**
     * 定时任务
     */
//    @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask() {
        System.out.println(LocalDateTime.now());
    }
}
