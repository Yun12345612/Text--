package org.example.springboot_hd.mapper;

import org.example.springboot_hd.entity.Order;

import java.util.List;

public interface OrderMapper {
    // 根据类型查询订单列表
    List<Order> selectOrderList(Order order);

    // 或者专门按类型查询的方法
    List<Order> selectByOrderType(String orderType);
    // 根据订单号查询
    Order selectByTraceNo(String traceNo);
    //创建订单
    int insertOrder(Order order);
    // 获取所有订单
    List<Order> selectAllOrders();

}
