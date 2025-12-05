package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot_hd.entity.OrderItemRelation;
import org.example.springboot_hd.vo.OrderItemDetailVO;
import java.util.List;

/**
 * 订单项目关联Mapper接口
 * 负责订单细项相关数据的数据库操作
 *
 * @author 夏日花店
 * @CreateTime 2025-11-07
 * @Description 订单细项数据访问层接口
 */
@Mapper
public interface OrderItemRelationMapper {

    /**
     * 根据订单编号查询细项详情
     * 通过关联查询获取订单对应的所有体检项目和细项信息
     *
     * @param orderNo 订单编号
     * @return List<OrderDetailVO> 订单细项详情列表
     * @apiNote 查询涉及多表关联：order_item_relation, order, examination_item, examination_item_detail, department
     */
    List<OrderItemDetailVO> selectDetailsByOrderNo(@Param("orderNo") String orderNo);
    //插入订单项目关联记录
    int insertOrderItemRelation(OrderItemRelation relation);

}