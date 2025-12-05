package com.cykj.controller;

import com.cykj.dto.ResponseDTO;

import com.cykj.pojo.ExaminationItem;
import com.cykj.service.ExaminationItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-11-04 15:30:08
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/examination/item")
public class ExaminationItemController {
    @Autowired
    private ExaminationItemService examinationItemService;

    // 分页多条件查询（支持价格区间、项目名称模糊搜索等）
    @GetMapping("/list")
    public ResponseDTO getItemList(
            // 项目名称模糊搜索
            @RequestParam(required = false) String itemName,
            // 科室ID筛选
            @RequestParam(required = false) Long departmentId,
            // 价格区间筛选
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            // 项目状态筛选
            @RequestParam(required = false) String itemStatus,
            // 分页参数
            @RequestParam(defaultValue = "0") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {


        // 构建查询参数对象
        ExaminationItem query = new ExaminationItem();
        query.setItemName(itemName);
        query.setDepartmentId(departmentId);
        query.setMinPrice(minPrice);
        query.setMaxPrice(maxPrice);
        query.setItemStatus(itemStatus); //传递状态参数
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        // 调用Service层查询
        return examinationItemService.getItemList(query);
    }
}

