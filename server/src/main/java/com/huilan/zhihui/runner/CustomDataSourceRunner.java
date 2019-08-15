package com.huilan.zhihui.runner;

import com.huilan.core.utils.CustomDataSourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@Slf4j
public class CustomDataSourceRunner implements ApplicationRunner {

    @Value("${custom-datasource-driver-path}")
    private String dataSourceYamlPath;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) {
        try {
            CustomDataSourceUtils.loadAllFromYaml(dataSourceYamlPath);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            SpringApplication.exit(applicationContext);
            log.info("Server shutdown");
        }
        log.info("Load custom datasource finish");
    }
}
