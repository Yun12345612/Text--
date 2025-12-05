package org.example.springboot_hd.service;

import org.example.springboot_hd.entity.Examination;
import java.util.List;

public interface ExaminationService {

    /**
     * 查询所有体检套餐
     */
    List<Examination> getAllExaminations();

    /**
     * 根据ID查询体检套餐
     */
    Examination getExaminationById(Long id);

    /**
     * 新增体检套餐
     */
    boolean addExamination(Examination examination);

    /**
     * 修改体检套餐
     */
    boolean updateExamination(Examination examination);

    /**
     * 删除体检套餐
     */
    boolean deleteExamination(Long id);

    /**
     * 分页查询体检套餐
     */
    List<Examination> getExaminationsByPage(Integer page, Integer pageSize);

    /**
     * 获取体检套餐总数
     */
    Integer getExaminationCount();
}