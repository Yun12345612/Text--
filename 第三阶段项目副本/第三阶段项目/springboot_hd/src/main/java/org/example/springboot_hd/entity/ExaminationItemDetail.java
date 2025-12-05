package org.example.springboot_hd.entity;

import lombok.Data;

@Data
public class ExaminationItemDetail {

    private Long detailId;
    private String detailName;
    private String detailUnit;
    private Double minValue;
    private Double maxValue;
    private String detailDesc;
    private String detailStatus;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;
    private String departmentName;
    private Integer detailIsDelete;
    private Long itemId;//项目id

}
