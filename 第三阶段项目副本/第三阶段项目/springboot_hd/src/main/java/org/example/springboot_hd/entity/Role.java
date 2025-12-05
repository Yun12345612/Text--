package org.example.springboot_hd.entity;

import lombok.Data;

@Data
public class Role {

    private Long roleId;
    private String roleName;
    private Integer roleStatus;
    private String roleDesc;
    private String roleIsDelete;
    private java.sql.Timestamp updateTime;
    private java.sql.Timestamp createTime;
    private Integer pageNum;  // 页码（从1开始）
    private Integer pageSize;

}
