package org.example.springboot_hd.service.impl;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.mapper.OrderItemRelationMapper;
import org.example.springboot_hd.service.OrderDetailService;
import org.example.springboot_hd.vo.OrderItemDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单细项查询服务实现类
 * 实现订单细项查询的具体业务逻辑
 *
 * @author 夏日花店
 * @CreateTime 2025-11-07
 * @Description 订单细项业务逻辑层实现
 */
@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderItemRelationMapper orderItemRelationMapper;

    /**
     * 根据订单编号查询细项详情实现
     * 包含业务逻辑处理、异常处理和结果封装
     *
     * @param orderNo 订单编号
     * @return ResponseDTO 统一响应结果
     * @implNote 1.调用Mapper查询数据 2.处理空结果 3.异常捕获 4.返回统一响应
     */
    @Override
    public ResponseDTO getOrderDetailsByOrderNo(String orderNo) {
        try {
            System.out.println("=== 开始查询订单详情 ===");
            System.out.println("订单号: " + orderNo);

            // 执行查询
            List<OrderItemDetailVO> details = orderItemRelationMapper.selectDetailsByOrderNo(orderNo);

            System.out.println("查询完成，结果数量: " + (details != null ? details.size() : "null"));

            if (details == null || details.isEmpty()) {
                System.out.println("⚠️ 查询结果为空，可能原因：");
                System.out.println("   - 订单不存在: " + orderNo);
                System.out.println("   - 订单没有关联的体检项目");
                System.out.println("   - 数据映射问题");
                return ResponseDTO.error("订单不存在或没有详情数据");
            }

            System.out.println("✅ 成功找到 " + details.size() + " 条订单详情记录");
            for (OrderItemDetailVO detail : details) {
                System.out.println("   - " + detail.getItemName() + " | " + detail.getDetailName());
            }
            return ResponseDTO.success(details, "查询成功");

        } catch (Exception e) {
            System.err.println("❌ 查询订单详情异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseDTO.error("查询失败: " + e.getMessage());
        }
    }
}