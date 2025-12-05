package org.example.springboot_hd.entity;

import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data

public class Order {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private Long status;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp payTime;
    private java.sql.Timestamp finishTime;
    private Double price;
    private String quantity;
    private String orderType;
    private Long productId;
    private String productName;
    private String productDesc;
    private String userPhone;
    private String traceNo;
    private String userName;
    private String userAge;
    private String userGender;
    private String userCard;
}