package org.example.springboot_hd.controller;
/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-11-03 13:44:29
 * @Description: 订单控制层
 * @Version: 1.0
 */
import org.example.springboot_hd.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.springboot_hd.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    // 添加状态查询接口
    @GetMapping("/status")
    public ResponseEntity<?> getOrderStatus(@RequestParam String traceNo) {
        try {
            System.out.println("查询订单状态，订单号: " + traceNo);

            // TODO: 调用service查询订单状态
            Order order = orderService.getOrderByTraceNo(traceNo);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("traceNo", traceNo);
            result.put("status", "UNPAID"); // 实际应该从数据库查询
            result.put("message", "查询成功");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("查询订单状态异常: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // 根据类型查询
    @GetMapping("/type/{orderType}")
    public List<Order> getByType(@PathVariable String orderType) {
        return orderService.getOrdersByType(orderType);
    }

    //查复杂条件查询
    @PostMapping("/search")
    public List<Order> getList(@RequestBody Order order) {
        return orderService.getOrderList(order);
    }

    //按简单的条件查询
    @GetMapping("/list")
    public List<Order> getList(@RequestParam(required = false) String orderType,
                               @RequestParam(required = false) Long status) {
        Order order = new Order();
        order.setOrderType(orderType);
        order.setStatus(status);
        return orderService.getOrderList(order);
    }
    //创建订单
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData) {
        try {
            System.out.println("接收到的订单数据: " + orderData); // 添加日志

            // 从请求参数中获取数据
            Double totalAmount = Double.valueOf(orderData.get("totalAmount").toString());
            String subject = (String) orderData.get("subject");
            String itemType = (String) orderData.get("itemType");
            Integer quantity = Integer.valueOf(orderData.get("quantity").toString());
            String phone = (String) orderData.get("userPhone");
            String userAge = (String) orderData.get("userAge");
            String userGender = (String) orderData.get("userGender");
            Long itemId = Long.valueOf(orderData.get("itemId").toString());

            // 获取用户信息
            Long userId = null;
            if (orderData.get("userId") != null) {
                userId = Long.valueOf(orderData.get("userId").toString());
            }
            String userName = (String) orderData.get("userName");

            System.out.println("用户信息 - ID: " + userId + ", 姓名: " + userName + ", 手机: " + phone);

            // 创建订单对象
            Order order = new Order();
            order.setTotalAmount(new BigDecimal(totalAmount));
            order.setOrderType("package".equals(itemType) ? "体检套餐" : "体检项目");
            order.setQuantity(String.valueOf(quantity));
            order.setPrice(totalAmount / quantity);
            order.setUserPhone(phone);
            order.setUserName(userName);
            order.setUserAge(userAge);
            order.setUserGender(userGender);
            order.setProductName(subject);
            order.setStatus(0L); // 0-未支付
            order.setProductId(itemId);

            // 设置用户信息 - 确保不为null
            order.setUserId(userId != null ? userId : 0L); // 设置默认值
            order.setUserName(userName != null ? userName : "匿名用户");

            System.out.println("准备保存的订单: " + order);

            // 调用service保存订单
            boolean success = orderService.createOrder(order);

            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("success", true);
                result.put("message", "订单创建成功");
                result.put("orderNo", order.getOrderNo());
                result.put("traceNo", order.getTraceNo());
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "订单创建失败");
                return ResponseEntity.status(500).body(result);
            }

        } catch (Exception e) {
            System.err.println("创建订单异常: " + e.getMessage());
            e.printStackTrace(); // 添加完整堆栈跟踪
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "创建订单失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // 获取所有订单列表
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.err.println("获取订单列表异常: " + e.getMessage());
            return ResponseEntity.status(500).body("获取订单列表失败");
        }
    }
}