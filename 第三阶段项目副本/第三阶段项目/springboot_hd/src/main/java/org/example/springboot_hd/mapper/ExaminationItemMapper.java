package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springboot_hd.entity.ExaminationItem;

import java.util.List;

@Mapper
public interface ExaminationItemMapper {

    // 1. 新增项目
    int insert(ExaminationItem item);

    // 2. 更新项目
    int update(ExaminationItem item);

    // 3. 软删除项目（更新isDelete=1）
    int softDelete(Long itemId);

    // 4. 根据ID查询（只查未删除的）
    ExaminationItem selectById(Long itemId);

    // 5. 多条件查询（支持分页、价格区间、模糊查询）
    List<ExaminationItem> selectList(ExaminationItem query);

    Long count(ExaminationItem query);

    // 6. 查询ong count(ExaminationItem query);
}