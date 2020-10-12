package cn.charlie166.spring.boot.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定时任务配置
 */
@Slf4j
@Configuration
@EnableScheduling
@Profile(value = "!test")
public class ScheduleConfiguration {

    @Scheduled(cron = "5 * * * * ?")
    public void scheduleTaskUsingCronExpression() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        log.debug("当前时间: {}", dtf.format(LocalDateTime.now()));
    }
}
