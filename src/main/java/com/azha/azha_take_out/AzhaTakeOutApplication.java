package com.azha.azha_take_out;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j//可以使用log
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class AzhaTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzhaTakeOutApplication.class, args);
        log.info("项目，启动！");
    }

}
