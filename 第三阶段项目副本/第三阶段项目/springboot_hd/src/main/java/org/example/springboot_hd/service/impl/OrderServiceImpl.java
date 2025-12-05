package org.example.springboot_hd.service.impl;

import org.example.springboot_hd.mapper.OrderMapper;
import org.example.springboot_hd.mapper.OrderItemRelationMapper;
import org.example.springboot_hd.service.OrderService;
import org.example.springboot_hd.utils.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.springboot_hd.entity.Order;
import org.example.springboot_hd.entity.OrderItemRelation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final Snowflake snowflake = Snowflake.INSTANCE;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemRelationMapper orderItemRelationMapper;

    // 创建订单
    @Override
    @Transactional
    public boolean createOrder(Order order) {
        try {
            System.out.println("开始创建订单，产品ID: " + order.getProductId());

            // 生成订单号和追踪号
            if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
                String orderNo = generateOrderNo();
                order.setOrderNo(orderNo);
            }

            if (order.getTraceNo() == null || order.getTraceNo().isEmpty()) {
                String traceNo = generateTraceNo();
                order.setTraceNo(traceNo);
            }

            // 1. 创建订单
            int result = orderMapper.insertOrder(order);
            System.out.println("订单创建结果: " + result + ", 生成的订单ID: " + order.getId());

            if (result > 0) {
                // 2. 自动创建订单项目关联记录
                boolean relationCreated = createOrderItemRelation(order);
                System.out.println("关联记录创建结果: " + relationCreated);
                return relationCreated;
            }

            return false;
        } catch (Exception e) {
            System.err.println("创建订单失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("创建订单失败", e); // 抛出异常让事务回滚
        }
    }

    /**
     * 创建订单项目关联记录
     */
    private boolean createOrderItemRelation(Order order) {
        try {
            // 检查是否有产品ID
            if (order.getProductId() == null) {
                System.out.println("⚠️ 订单没有产品ID，跳过创建关联记录");
                return true; // 没有产品ID也算成功
            }

            OrderItemRelation relation = new OrderItemRelation();
            relation.setOrderId(order.getId());
            relation.setItemId(order.getProductId());
            relation.setOrderNo(order.getOrderNo());


            int relationResult = orderItemRelationMapper.insertOrderItemRelation(relation);
            System.out.println("创建关联记录: orderId=" + order.getId() + ", itemId=" + order.getProductId());

            return relationResult > 0;
        } catch (Exception e) {
            System.err.println("创建订单项目关联记录失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public List<Order> getOrdersByType(String orderType) {
        return orderMapper.selectByOrderType(orderType);
    }

    @Override
    public List<Order> getOrderList(Order order) {
        return orderMapper.selectOrderList(order);
    }

    @Override
    public Order getOrderByTraceNo(String traceNo) {
        return orderMapper.selectByTraceNo(traceNo);
    }

    private String generateOrderNo() {
        return "ORD" + snowflake.nextId();
    }

    private String generateTraceNo() {
        return "TRACE" + snowflake.nextId();
    }

    private String generateOrderNoWithDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new java.util.Date());
        return "ORD" + dateStr + snowflake.nextId();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderMapper.selectAllOrders();
    }
}