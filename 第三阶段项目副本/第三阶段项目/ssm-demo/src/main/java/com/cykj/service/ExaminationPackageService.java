package com.cykj.service;

import com.cykj.dto.ResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface ExaminationPackageService {
    /**
     * 查询所有套餐
     *
     * @return 统一响应DTO（包含所有套餐列表）
     */
    ResponseDTO getAll();

}
