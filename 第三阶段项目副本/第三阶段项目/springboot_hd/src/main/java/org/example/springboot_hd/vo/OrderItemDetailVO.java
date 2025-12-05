package org.example.springboot_hd.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDetailVO {
    private String orderNo;
    private String userName;
    private String phone;
    private BigDecimal orderAmount;
    private String orderStatus;
    private String itemName;
    private String detailName;
    private String unit;
    private String department;
    private String referenceMin;
    private String referenceMax;
    private String description;
    private String checkResult;
    private String resultStatus;
    private java.sql.Timestamp updateTime;
}