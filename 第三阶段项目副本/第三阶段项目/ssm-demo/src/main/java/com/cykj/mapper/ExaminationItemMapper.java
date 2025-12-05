package com.cykj.mapper;

import com.cykj.pojo.ExaminationItem;

import java.util.List;

public interface ExaminationItemMapper {
    //1.多条件查询项目
    List<ExaminationItem> selectList(ExaminationItem query);
    Long count(ExaminationItem query);
}
