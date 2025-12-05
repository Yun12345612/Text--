package org.example.springboot_hd.entity;

import lombok.Data;

@Data
public class ExaminationPackage {

    private Long packageId;
    private String packageName;
    private Double packagePrice;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;
    private String itemName;
    private String packageDesc;
    private String packageStatus;
    private Integer packageIsDelete;
}
