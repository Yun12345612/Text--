package com.cykj.pojo;

import lombok.Data;

@Data
public class ExaminationItem {

    private Long itemId;
    private String itemName;
    private Long departmentId;
    private Double itemPrice;
    private String itemDesc;
    private Double minPrice;
    private Double maxPrice;
    private String itemIsDelete;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;
    private String itemStatus;
    private String departmentName;
    private String detailName;
    private String detailDesc;
    private Integer pageNum;  // 分页偏移量（从0开始，对应SQL的LIMIT起始值）
    private Integer pageSize;


}
