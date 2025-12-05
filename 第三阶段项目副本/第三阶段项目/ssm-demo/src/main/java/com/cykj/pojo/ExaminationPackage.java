package com.cykj.pojo;

import lombok.Data;

@Data
public class ExaminationPackage {

    private Long packageId;
    private String packageName;
    private Double packagePrice;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;
    private String itemName;
    private String packageStatus;
    private String packageIsDelete;
    private String packageDesc;

}
