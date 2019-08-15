

package com.huilan.zhihui.runner;

import com.alibaba.druid.util.StringUtils;
import com.huilan.zhihui.core.utils.DacChannelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(0)
@Component
@Slf4j
public class CheckConfigRunner implements ApplicationRunner {

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private String mailPort;

    @Value("${spring.mail.username}")
    private String mailUserName;


    @Value("${spring.mail.nickname}")
    private String nickName;


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DacChannelUtil dacChannelUtil;


    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.isEmpty(mailHost)) {
            log.error("**************     Configuration error: mail host connot be EMPTY!      **************");
            SpringApplication.exit(applicationContext);
        }

        if (StringUtils.isEmpty(mailPort)) {
            log.error("**************     Configuration error: mail port connot be EMPTY!      **************");
            SpringApplication.exit(applicationContext);
        }

        if (StringUtils.isEmpty(mailUserName)) {
            log.error("**************     Configuration error: mail username connot be EMPTY!      **************");
            SpringApplication.exit(applicationContext);
        }

        if (StringUtils.isEmpty(mailUserName)) {
            log.error("**************     Configuration error: mail nickname connot be EMPTY!      **************");
            SpringApplication.exit(applicationContext);
        }

        dacChannelUtil.loadDacMap();
    }
}
