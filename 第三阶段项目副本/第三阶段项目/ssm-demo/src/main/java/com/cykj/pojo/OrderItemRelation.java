package com.cykj.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 订单项目关联表实体类
 */
@Data
public class OrderItemRelation {
    private Long id;
    private Long orderId;
    private Long itemId;
    private Long detailId;
    private Date createTime;
}

