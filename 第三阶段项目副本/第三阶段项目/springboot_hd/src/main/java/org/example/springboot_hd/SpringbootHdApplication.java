package org.example.springboot_hd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("org.example.springboot_hd.mapper")
@SpringBootApplication
public class SpringbootHdApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootHdApplication.class, args);
    }

}
