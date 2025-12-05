package com.cykj.pojo;

import lombok.Data;
@Data
public class AliPay {
    private String traceNo; // 我们自己生成的订单编号
    private double totalAmount;// 订单的总金额
    private String subject; // 支付的名称
    private String alipayTraceNo;
    public String getTraceNo() {
        return traceNo;
    }
    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getAlipayTraceNo() {
        return alipayTraceNo;
    }
    public void setAlipayTraceNo(String alipayTraceNo) {
        this.alipayTraceNo = alipayTraceNo;
    }
}