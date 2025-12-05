package com.cykj.service;

import com.cykj.dto.ResponseDTO;
import com.cykj.pojo.ExaminationItem;

public interface ExaminationItemService {
    // 1.分页多条件查询（返回列表+总条数，封装到ResponseDTO）
    ResponseDTO getItemList(ExaminationItem query);
}
