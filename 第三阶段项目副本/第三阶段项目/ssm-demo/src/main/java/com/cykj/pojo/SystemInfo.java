package com.cykj.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-18 11:01:36
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
