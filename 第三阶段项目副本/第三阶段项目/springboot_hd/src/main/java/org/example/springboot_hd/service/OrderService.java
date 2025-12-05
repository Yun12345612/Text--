package org.example.springboot_hd.service;



import org.example.springboot_hd.entity.Order;

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
}