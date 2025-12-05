package org.example.springboot_hd.entity;

import lombok.Data;

@Data
public class Department {

    private Long departmentId;
    private String departmentName;
    private String departmentLocation;
    private Integer departmentStatus;
    private String departmentDeptDesc;
    private Integer departmentIsDelete;

    // 查询辅助字段（非数据库必填列）：用于时间范围与分页
    private java.util.Date startTime;
    private java.util.Date endTime;
    private Integer pageNum;    // 偏移量：从第几条开始
    private Integer pageSize;   // 每页数量


}
