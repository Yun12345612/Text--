package org.example.springboot_hd.service;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.ExaminationItem;

public interface ExaminationItemService {

    // 1. 新增项目
    ResponseDTO addItem(ExaminationItem item);

    // 2. 更新项目
    ResponseDTO updateItem(ExaminationItem item);

    // 3. 软删除项目
    ResponseDTO softDeleteItem(Long itemId);

    // 4. 根据ID查询
    ResponseDTO getItemById(Long itemId);

    // 5. 分页多条件查询（返回列表+总条数，封装到ResponseDTO）
    ResponseDTO getItemList(ExaminationItem query);
}