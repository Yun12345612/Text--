package com.cykj.service.Impl;

import com.cykj.mapper.OrderMapper;
import com.cykj.mapper.OrderItemRelationMapper;
import com.cykj.pojo.Order;
import com.cykj.pojo.OrderItemRelation;
import com.cykj.service.OrderService;
import com.cykj.utils.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.service.Impl
 * @Author: 夏日花店
 * @CreateTime: 2025-11-05 11:46:49
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    //1.导入雪花算法的实例
    private final Snowflake snowflake = Snowflake.INSTANCE;

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemRelationMapper orderItemRelationMapper;

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

    //创建订单
    @Override
    @Transactional
    public boolean createOrder(Order order) {
        try {
            System.out.println("=== 开始创建订单 ===");

            // 1.使用雪花算法生成唯一的订单号和追踪号
            if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
                String orderNo = generateOrderNo();
                order.setOrderNo(orderNo);
            }

            if (order.getTraceNo() == null || order.getTraceNo().isEmpty()) {
                String traceNo = generateTraceNo();
                order.setTraceNo(traceNo);
            }

            // 2.插入订单记录
            int result = orderMapper.insertOrder(order);
            if (result <= 0) {
                System.err.println("❌ 订单插入失败");
                return false;
            }
            
            System.out.println("✅ 订单创建成功，订单ID: " + order.getId() + ", 订单号: " + order.getOrderNo());

            // 3.插入订单项目关联记录
            Long orderId = order.getId();
            Long productId = order.getProductId();
            String orderType = order.getOrderType();

            if ("体检套餐".equals(orderType)) {
                // 套餐订单：查询套餐包含的所有体检项目
                System.out.println("📦 套餐订单，查询套餐ID: " + productId + " 包含的体检项目");
                List<Long> itemIds = orderItemRelationMapper.selectItemIdsByPackageId(productId);
                
                if (itemIds == null || itemIds.isEmpty()) {
                    System.err.println("⚠️ 套餐没有关联的体检项目");
                } else {
                    System.out.println("找到 " + itemIds.size() + " 个体检项目，开始插入关联记录");
                    for (Long itemId : itemIds) {
                        OrderItemRelation relation = new OrderItemRelation();
                        relation.setOrderId(orderId);
                        relation.setItemId(itemId);
                        relation.setDetailId(null); // 细项ID可以为空，后续由管理端填写
                        orderItemRelationMapper.insertOrderItemRelation(relation);
                        System.out.println("   ✓ 插入关联记录: 订单ID=" + orderId + ", 项目ID=" + itemId);
                    }
                }
            } else {
                // 单项订单：直接插入一条关联记录
                System.out.println("📝 单项订单，体检项目ID: " + productId);
                OrderItemRelation relation = new OrderItemRelation();
                relation.setOrderId(orderId);
                relation.setItemId(productId);
                relation.setDetailId(null);
                orderItemRelationMapper.insertOrderItemRelation(relation);
                System.out.println("   ✓ 插入关联记录: 订单ID=" + orderId + ", 项目ID=" + productId);
            }

            System.out.println("=== 订单创建完成 ===");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 创建订单失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 生成订单号 - 格式：ORD + 雪花算法ID
     */
    private String generateOrderNo() {
        return "ORD" + snowflake.nextId();
    }

    /**
     * 生成追踪号 - 格式：TRACE + 雪花算法ID
     */
    private String generateTraceNo() {
        return "TRACE" + snowflake.nextId();
    }

    /**
     * 生成带日期的订单号
     */
    private String generateOrderNoWithDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new java.util.Date());
        return "ORD" + dateStr + snowflake.nextId();
    }


    //获取所有订单
    @Override
    public List<Order> getAllOrders() {
        return orderMapper.selectAllOrders();
    }

    @Override
    public boolean updateOrderStatus(Order order) {
        try {
            System.out.println("更新订单状态，订单ID: " + order.getId() + ", 新状态: " + order.getStatus());
            int result = orderMapper.updateOrderStatus(order);
            boolean success = result > 0;
            System.out.println("更新订单状态" + (success ? "成功" : "失败"));
            return success;
        } catch (Exception e) {
            System.err.println("更新订单状态失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteOrderById(Long orderId) {
        try {
            System.out.println("删除订单，订单ID: " + orderId);
            int result = orderMapper.deleteOrderById(orderId);
            boolean success = result > 0;
            System.out.println("删除订单" + (success ? "成功" : "失败"));
            return success;
        } catch (Exception e) {
            System.err.println("删除订单失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateOrderStatusByTraceNo(String traceNo, Long status) {
        try {
            System.out.println("更新订单状态: traceNo=" + traceNo + ", status=" + status);
            int result = orderMapper.updateOrderStatusByTraceNo(traceNo, status);
            System.out.println("更新结果: " + (result > 0 ? "成功" : "失败"));
            return result > 0;
        } catch (Exception e) {
            System.err.println("更新订单状态失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}