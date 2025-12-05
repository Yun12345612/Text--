package org.example.springboot_hd.controller;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单细项查询控制器
 * 负责接收前端请求，调用服务层处理业务逻辑
 *
 * @author 夏日花店
 * @CreateTime 2025-11-07
 * @Description 订单细项查询相关接口
 */
@RestController
@RequestMapping("/order")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 根据订单编号路径参数查询细项详情
     *
     * @param orderNo 订单编号（路径参数）
     * @return ResponseDTO 统一响应结果，包含细项列表数据
     * @apiNote 通过URL路径传递订单编号，如：/api/order/details/ORD123456
     */
    @GetMapping("/details/{orderNo}")
    public ResponseDTO getOrderDetails(@PathVariable String orderNo) {
        return orderDetailService.getOrderDetailsByOrderNo(orderNo);
    }

    /**
     * 根据订单编号查询参数查询细项详情
     *
     * @param orderNo 订单编号（查询参数）
     * @return ResponseDTO 统一响应结果，包含细项列表数据
     * @apiNote 通过URL查询参数传递订单编号，如：/api/order/details?orderNo=ORD123456
     */
    @GetMapping("/details")
    public ResponseDTO getOrderDetailsByParam(@RequestParam String orderNo) {
        return orderDetailService.getOrderDetailsByOrderNo(orderNo);
    }

    /**
     * 获取订单基本信息
     */
    @GetMapping("/info/{orderNo}")
    public ResponseDTO getOrderInfo(@PathVariable String orderNo) {
        // 这里需要实现获取订单基本信息的逻辑
        // 暂时返回成功响应
        return ResponseDTO.success(null, "获取订单信息成功");
    }
}