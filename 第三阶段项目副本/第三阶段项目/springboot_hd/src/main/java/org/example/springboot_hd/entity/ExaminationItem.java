package org.example.springboot_hd.entity;

import lombok.Data;

@Data
public class ExaminationItem {

    private Long itemId;
    private String itemName;
    private Long departmentId;
    private Double itemPrice;
    private String itemDesc;
    private String orderNo;
    private Double minPrice;
    private Double maxPrice;
    private Integer itemIsDelete;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp updateTime;
    private String detailName;
    private String detailDesc;
    private String itemStatus;
    private String departmentName;
    private Integer pageNum;  // 分页偏移量（从0开始，对应SQL的LIMIT起始值）
    private Integer pageSize; // 每页显示的条数（对应SQL的LIMIT条数）



}
