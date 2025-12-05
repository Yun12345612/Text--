package com.cykj.service.Impl;

import com.cykj.dto.ResponseDTO;
import com.cykj.mapper.ExaminationPackageMapper;
import com.cykj.pojo.ExaminationPackage;
import com.cykj.service.ExaminationPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.service.Impl
 * @Author: 夏日花店
 * @CreateTime: 2025-11-04 11:36:23
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class ExaminationPackageServiceImpl implements ExaminationPackageService {
    @Autowired
    private ExaminationPackageMapper examinationPackageMapper;

    //1.查询所有套餐
    @Override
    public ResponseDTO getAll() {
        List<ExaminationPackage> packageList = examinationPackageMapper.selectAll();
        return ResponseDTO.success(packageList,"查询套餐成功");
    }
}
