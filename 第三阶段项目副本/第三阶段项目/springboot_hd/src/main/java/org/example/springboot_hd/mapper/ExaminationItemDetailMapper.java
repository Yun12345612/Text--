package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springboot_hd.entity.ExaminationItemDetail;
import java.util.List;
import java.util.Map;

@Mapper
public interface ExaminationItemDetailMapper {

    // 新增
    int insert(ExaminationItemDetail detail);

    // 软删除
    int softDeleteById(Long detailId);

    // 根据ID查询
    ExaminationItemDetail selectById(Long detailId);

    // 查询所有
    List<ExaminationItemDetail> selectAll();

    // 分页查询
    List<ExaminationItemDetail> selectPage(Map<String, Object> params);

    // 统计总数
    Long count();

    // 更新
    int update(ExaminationItemDetail detail);

    // 根据条件查询
    List<ExaminationItemDetail> selectByCondition(ExaminationItemDetail condition);
}