package org.example.springboot_hd.entity;

import lombok.Data;

/**
 * 订单项目关联实体类
 * 对应数据库表：order_item_relation
 * 用于存储订单、体检项目、体检细项之间的关联关系
 *
 * @author 夏日花店
 * @CreateTime 2025-11-07
 * @Description 订单项目关联实体
 */
@Data
public class OrderItemRelation {


    private Long id;

    private Long orderId;

    private Long itemId;

    private Long detailId;

    private java.sql.Timestamp createTime;

    private java.sql.Timestamp updateTime;

    private String orderNo;

}