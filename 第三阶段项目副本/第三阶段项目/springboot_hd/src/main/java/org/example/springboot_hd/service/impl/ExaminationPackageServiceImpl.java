package org.example.springboot_hd.service.impl;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.ExaminationPackage;
import org.example.springboot_hd.mapper.ExaminationPackageMapper;
import org.example.springboot_hd.service.ExaminationPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExaminationPackageServiceImpl implements ExaminationPackageService {

    @Autowired
    private ExaminationPackageMapper examinationPackageMapper;

    @Override
    public ResponseDTO getById(Long packageId) {
        // 参数校验
        if (packageId == null || packageId <= 0) {
            return ResponseDTO.error("套餐ID不能为空或小于等于0");
        }
        // 查询数据
        ExaminationPackage examinationPackage = examinationPackageMapper.selectById(packageId);
        if (examinationPackage != null) {
            return ResponseDTO.success(examinationPackage, "查询成功");
        } else {
            return ResponseDTO.error("未找到ID为" + packageId + "的套餐");
        }
    }

    @Override
    public ResponseDTO getAll() {
        List<ExaminationPackage> packageList = examinationPackageMapper.selectAll();
        return ResponseDTO.success(packageList, "查询所有套餐成功");
    }

    @Override
    public ResponseDTO add(ExaminationPackage examinationPackage) {
        // 参数校验
        if (examinationPackage == null) {
            return ResponseDTO.error("套餐信息不能为空");
        }
        if (examinationPackage.getPackageName() == null || examinationPackage.getPackageName().trim().isEmpty()) {
            return ResponseDTO.error("套餐名称不能为空");
        }
        // 自动填充时间（创建时间和更新时间）
        Timestamp now = new Timestamp(System.currentTimeMillis());
        examinationPackage.setCreateTime(now);
        examinationPackage.setUpdateTime(now);
        // 执行新增
        int rows = examinationPackageMapper.insert(examinationPackage);
        return rows > 0 ? ResponseDTO.success("新增套餐成功") : ResponseDTO.error("新增套餐失败");
    }

    @Override
    public ResponseDTO update(ExaminationPackage examinationPackage) {
        // 参数校验
        if (examinationPackage == null || examinationPackage.getPackageId() == null || examinationPackage.getPackageId() <= 0) {
            return ResponseDTO.error("套餐ID不能为空或小于等于0");
        }
        // 校验套餐是否存在
        ExaminationPackage existingPackage = examinationPackageMapper.selectById(examinationPackage.getPackageId());
        if (existingPackage == null) {
            return ResponseDTO.error("未找到ID为" + examinationPackage.getPackageId() + "的套餐，无法更新");
        }
        // 自动更新时间
        examinationPackage.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        // 执行更新
        int rows = examinationPackageMapper.updateById(examinationPackage);
        return rows > 0 ? ResponseDTO.success("更新套餐成功") : ResponseDTO.error("更新套餐失败");
    }

    @Override
    public ResponseDTO delete(Long packageId) {
        // 参数校验
        if (packageId == null || packageId <= 0) {
            return ResponseDTO.error("套餐ID不能为空或小于等于0");
        }
        // 校验套餐是否存在
        ExaminationPackage existingPackage = examinationPackageMapper.selectById(packageId);
        if (existingPackage == null) {
            return ResponseDTO.error("未找到ID为" + packageId + "的套餐，无法删除");
        }
        // 执行删除
        int rows = examinationPackageMapper.deleteById(packageId);
        return rows > 0 ? ResponseDTO.success("删除套餐成功") : ResponseDTO.error("删除套餐失败");
    }


    //分页查询实现
    @Override
    public ResponseDTO selectPage(
            String packageName,
            String packageStatus,
            Double minPrice,
            Double maxPrice,
            int pageNum,
            int pageSize) {
        // 计算分页起始位置（pageNum前端从0开始）
        int start = pageNum * pageSize;
        // 查询分页数据
        List<ExaminationPackage> list = examinationPackageMapper.selectPage(
                packageName, packageStatus, minPrice, maxPrice, start, pageSize);
        // 查询总条数
        int total = examinationPackageMapper.selectTotal(
                packageName, packageStatus, minPrice, maxPrice);
        // 封装结果
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return ResponseDTO.success(data, "分页查询成功");
    }
}