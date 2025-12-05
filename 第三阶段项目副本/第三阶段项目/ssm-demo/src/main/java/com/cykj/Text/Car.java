package com.cykj.Text;

import org.springframework.stereotype.Component;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-14 09:49:42
 * @Description: 头部注释
 * @Version: 1.0
 */
@Component
public class Car {
    private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
