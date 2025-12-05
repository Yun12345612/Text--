package com.cykj.Text;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-14 09:54:18
 * @Description: 头部注释
 * @Version: 1.0
 */
@Configuration
public class MyConfig {
@Bean
    public House getHouse(){
        House house = new House();
        return house;
    }
}
