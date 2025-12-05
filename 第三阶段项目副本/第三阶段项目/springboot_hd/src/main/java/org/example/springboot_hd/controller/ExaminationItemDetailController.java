package org.example.springboot_hd.controller;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.ExaminationItemDetail;
import org.example.springboot_hd.service.ExaminationItemDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-10-31 14:03:27
 * @Description: 检查项目明细控制器
 * @Version: 1.0
 */
@RestController
@RequestMapping("/examination-item-detail")
public class ExaminationItemDetailController {

    @Autowired
    private ExaminationItemDetailService examinationItemDetailService;

    /**
     * 新增检查项目明细
     */
    @PostMapping
    public ResponseDTO addItem(@RequestBody ExaminationItemDetail detail) {
        return examinationItemDetailService.addItem(detail);
    }

    /**
     * 软删除检查项目明细
     */
    @DeleteMapping("/{detailId}")
    public ResponseDTO deleteItem(@PathVariable Long detailId) {
        return examinationItemDetailService.deleteItem(detailId);
    }

    /**
     * 根据ID查询检查项目明细
     */
    @GetMapping("/{detailId}")
    public ResponseDTO getItemById(@PathVariable Long detailId) {
        return examinationItemDetailService.getItemById(detailId);
    }

    /**
     * 查询所有检查项目明细
     */
    @GetMapping("/all")
    public ResponseDTO getAllItems() {
        return examinationItemDetailService.getAllItems();
    }

    /**
     * 分页查询检查项目明细
     */
    @GetMapping("/page")
    public ResponseDTO getItemsByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return examinationItemDetailService.getItemsByPage(pageNum, pageSize);
    }

    /**
     * 更新检查项目明细
     */
    @PutMapping
    public ResponseDTO updateItem(@RequestBody ExaminationItemDetail detail) {
        return examinationItemDetailService.updateItem(detail);
    }

    /**
     * 根据条件查询检查项目明细
     */
    @PostMapping("/condition")
    public ResponseDTO getItemsByCondition(@RequestBody ExaminationItemDetail condition) {
        return examinationItemDetailService.getItemsByCondition(condition);
    }

    /**
     * 统计检查项目明细总数
     */
    @GetMapping("/count")
    public ResponseDTO getCount() {
        return examinationItemDetailService.getCount();
    }
}