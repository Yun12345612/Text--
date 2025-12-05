package com.cykj.service;

import com.cykj.pojo.Order;

import java.util.List;

public interface OrderService {

    List<Order> getOrdersByType(String orderType);

    List<Order> getOrderList(Order order);

    //根据订单号查询订单
    Order getOrderByTraceNo(String traceNo);

    // 创建订单
    boolean createOrder(Order order);

    //获取所有订单 - 删除重复的selectAllOrders方法，只保留这个
    List<Order> getAllOrders();

    /**
     * 更新订单状态
     * @param order 订单对象
     * @return 是否成功
     */
    boolean updateOrderStatus(Order order);

    /**
     * 根据ID硬删除订单
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean deleteOrderById(Long orderId);
    
    /**
     * 根据追踪号更新订单状态
     * @param traceNo 追踪号
     * @param status 状态
     * @return 是否成功
     */
    boolean updateOrderStatusByTraceNo(String traceNo, Long status);

}