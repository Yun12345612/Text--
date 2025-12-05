package com.cykj.mapper;


import com.cykj.pojo.Order;
import org.apache.ibatis.annotations.Param;

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


    /**
     * 更新订单状态
     * @param order 订单对象，包含id和status
     * @return 影响的行数
     */
    int updateOrderStatus(Order order);

    /**
     * 根据ID硬删除订单
     * @param orderId 订单ID
     * @return 影响的行数
     */
    int deleteOrderById(Long orderId);
    
    /**
     * 根据追踪号更新订单状态
     * @param traceNo 追踪号
     * @param status 状态
     * @return 影响的行数
     */
    int updateOrderStatusByTraceNo(@Param("traceNo") String traceNo, @Param("status") Long status);
}
