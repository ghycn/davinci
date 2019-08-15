

package com.huilan.zhihui.runner;

import com.huilan.zhihui.service.CronJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
@Slf4j
public class CronJobRunner implements ApplicationRunner {

    @Autowired
    private CronJobService cronJobService;

    /**
     * 应用启动后开启已启动的cronjob
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            cronJobService.startAllJobs();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("Load cron job finish");
    }
}
