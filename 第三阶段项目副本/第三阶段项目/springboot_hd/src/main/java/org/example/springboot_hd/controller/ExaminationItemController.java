package org.example.springboot_hd.controller;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.ExaminationItem;
import org.example.springboot_hd.service.ExaminationItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-10-30 10:53:46
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/examination/item") // 路径添加item前缀，避免与其他接口冲突
public class ExaminationItemController {

    @Autowired
    private ExaminationItemService examinationItemService;

    // 1. 新增项目
    @PostMapping("/add")
    public ResponseDTO addItem(@RequestBody ExaminationItem examinationItem) {
        return examinationItemService.addItem(examinationItem);
    }

    // 2. 更新项目
    @PostMapping("/update")
    public ResponseDTO updateItem(@RequestBody ExaminationItem examinationItem) {
        return examinationItemService.updateItem(examinationItem);
    }

    // 3. 软删除项目
    @DeleteMapping("/delete/{itemId}")
    public ResponseDTO softDeleteItem(@PathVariable Long itemId) {
        return examinationItemService.softDeleteItem(itemId);
    }

    // 4. 根据ID查询项目
    @GetMapping("/{itemId}")
    public ResponseDTO getItemById(@PathVariable Long itemId) {
        return examinationItemService.getItemById(itemId);
    }

    // 5. 分页多条件查询（支持价格区间、项目名称模糊搜索等）
    @GetMapping("/list")
    public ResponseDTO getItemList(
            // 项目名称模糊搜索
            @RequestParam(required = false) String itemName,
            // 科室ID筛选
            @RequestParam(required = false) Long departmentId,
            // 价格区间筛选
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            // 项目状态筛选（新增）
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
        query.setItemStatus(itemStatus); // 新增：传递状态参数
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        // 调用Service层查询
        return examinationItemService.getItemList(query);
    }
}