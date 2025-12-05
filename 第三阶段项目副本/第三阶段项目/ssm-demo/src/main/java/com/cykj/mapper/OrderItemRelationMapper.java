package com.cykj.mapper;

import com.cykj.pojo.OrderItemRelation;
import org.apache.ibatis.annotations.Param;

/**
 * 订单项目关联Mapper
 */
public interface OrderItemRelationMapper {
    
    /**
     * 插入订单项目关联记录
     * @param relation 关联对象
     * @return 影响的行数
     */
    int insertOrderItemRelation(OrderItemRelation relation);
    
    /**
     * 根据套餐ID获取所有体检项目ID
     * @param packageId 套餐ID
     * @return 体检项目ID列表
     */
    java.util.List<Long> selectItemIdsByPackageId(@Param("packageId") Long packageId);
}

