package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springboot_hd.entity.Examination;
import java.util.List;

@Mapper
public interface ExaminationMapper {

    /**
     * 查询所有体检套餐
     */
    List<Examination> selectAll();

    /**
     * 根据ID查询体检套餐
     */
    Examination selectById(Long id);

    /**
     * 新增体检套餐
     */
    int insert(Examination examination);

    /**
     * 修改体检套餐
     */
    int update(Examination examination);

    /**
     * 删除体检套餐
     */
    int deleteById(Long id);

    /**
     * 根据名称查询数量（用于查重）
     */
    int countByName(String name);

    /**
     * 分页查询
     */
    List<Examination> selectByPage(Integer offset, Integer pageSize);

    /**
     * 查询总数
     */
    int selectCount();
}