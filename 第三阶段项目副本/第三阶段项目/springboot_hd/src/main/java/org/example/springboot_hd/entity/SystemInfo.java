package org.example.springboot_hd.entity;

import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.entity
 * @Author: 夏日花店
 * @CreateTime: 2025-10-29 09:05:12
 * @Description: 头部注释
 * @Version: 1.0
 */
@Data
public class SystemInfo {
    // 主键ID
    private Integer id;
    // 系统名称
    private String systemName;
    // logo图片URL（相对路径或绝对路径）
    private String logoImageUrl;
    // 记录创建时间
    private Date createTime;
    // 记录更新时间
    private Date updateTime;

}
