package org.example.springboot_hd.service;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.ExaminationItemDetail;

public interface ExaminationItemDetailService {

    // 新增
    ResponseDTO addItem(ExaminationItemDetail detail);

    // 软删除
    ResponseDTO deleteItem(Long detailId);

    // 根据ID查询
    ResponseDTO getItemById(Long detailId);

    // 查询所有
    ResponseDTO getAllItems();

    // 分页查询
    ResponseDTO getItemsByPage(Integer pageNum, Integer pageSize);

    // 更新
    ResponseDTO updateItem(ExaminationItemDetail detail);

    // 条件查询
    ResponseDTO getItemsByCondition(ExaminationItemDetail condition);

    // 统计总数
    ResponseDTO getCount();
}