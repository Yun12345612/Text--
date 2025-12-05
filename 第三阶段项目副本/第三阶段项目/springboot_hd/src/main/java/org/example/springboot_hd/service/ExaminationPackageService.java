package org.example.springboot_hd.service;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.ExaminationPackage;

public interface ExaminationPackageService {

    /**
     * 根据ID查询套餐
     *
     * @param packageId 套餐ID
     * @return 统一响应DTO（包含查询结果）
     */
    ResponseDTO getById(Long packageId);

    /**
     * 查询所有套餐
     *
     * @return 统一响应DTO（包含所有套餐列表）
     */
    ResponseDTO getAll();

    /**
     * 新增套餐
     *
     * @param examinationPackage 套餐实体（包含新增数据）
     * @return 统一响应DTO（包含新增结果）
     */
    ResponseDTO add(ExaminationPackage examinationPackage);

    /**
     * 更新套餐
     *
     * @param examinationPackage 套餐实体（包含更新数据，需携带ID）
     * @return 统一响应DTO（包含更新结果）
     */
    ResponseDTO update(ExaminationPackage examinationPackage);

    /**
     * 删除套餐
     *
     * @param packageId 套餐ID
     * @return 统一响应DTO（包含删除结果）
     */
    ResponseDTO delete(Long packageId);

    // 分页查询
    ResponseDTO selectPage(
            String packageName,
            String packageStatus,
            Double minPrice,
            Double maxPrice,
            int pageNum,
            int pageSize);
}