package com.cykj.utils;

/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.utils
 * @Author: 夏日花店
 * @CreateTime: 2025-11-06 09:07:22
 * @Description: 头部注释
 * @Version: 1.0
 */
public class Text {
    public static void main(String[] args) {
        Snowflake snowflake = Snowflake.INSTANCE;
        for (int i = 0; i < 100; i++) {
            System.out.println(snowflake.nextId());
        }
    }
}
