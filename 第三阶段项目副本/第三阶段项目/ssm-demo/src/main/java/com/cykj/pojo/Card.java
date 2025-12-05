package com.cykj.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-20 19:34:32
 * @Description: 头部注释
 * @Version: 1.0
 */
@Data
public class Card {
    private Integer cardId; // 卡片ID
    private String cardPrefix; // 卡号前缀（对应前端的prefix）
    private String cardNumber; // 单个卡号（用于查询/删除单个卡片）
    //接收前端的起始卡号和截止卡号
    private String startCardNo; // 起始卡号（对应前端startCardNo）
    private String endCardNo;   // 截止卡号（对应前端endCardNo）
    private BigDecimal cardMoney; // 卡内金额
    private Integer cardIsDelete; // 是否删除（0：未删除，1：已删除）
    private Integer cardStatus; // 卡状态（0：可用，1：已用）
    private Date cardCreateTime; // 创建时间
    private Date cardUpdateTime;
}
