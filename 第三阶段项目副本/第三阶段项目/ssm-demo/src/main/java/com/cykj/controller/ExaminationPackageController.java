package com.cykj.controller;

import com.cykj.dto.ResponseDTO;
import com.cykj.service.ExaminationPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-11-04 11:41:11
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/examination-packages")
public class ExaminationPackageController {
    @Autowired
    private ExaminationPackageService examinationPackageService;
    //1.查询所有套餐
    @GetMapping
    public ResponseDTO getAll() {
        return examinationPackageService.getAll();
    }

}
