package org.example.springboot_hd.service;

import org.example.springboot_hd.dto.ResponseDTO;

/**
 * 订单细项查询服务接口
 * 定义订单细项查询相关的业务逻辑方法
 *
 * @author 夏日花店
 * @CreateTime 2025-11-07
 * @Description 订单细项业务逻辑层接口
 */
public interface OrderDetailService {

    /**
     * 根据订单编号查询细项详情
     * 业务逻辑层方法，处理查询逻辑和异常情况
     *
     * @param orderNo 订单编号
     * @return ResponseDTO 统一响应结果，包含状态码、消息和数据
     * @apiNote 该方法会处理查询结果为空和异常情况，返回统一的响应格式
     */
    ResponseDTO getOrderDetailsByOrderNo(String orderNo);

}