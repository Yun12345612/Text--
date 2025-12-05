package org.example.springboot_hd.controller;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.ExaminationPackage;
import org.example.springboot_hd.service.ExaminationPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/examination-packages")
public class ExaminationPackageController {

    @Autowired
    private ExaminationPackageService examinationPackageService;

    /**
     * 根据ID查询套餐
     */
    @GetMapping("/{id}")
    public ResponseDTO getById(@PathVariable("id") Long id) {
        return examinationPackageService.getById(id);
    }

    /**
     * 查询所有套餐
     */
    @GetMapping
    public ResponseDTO getAll() {
        return examinationPackageService.getAll();
    }

    /**
     * 新增套餐
     */
    @PostMapping
    public ResponseDTO add(@RequestBody ExaminationPackage examinationPackage) {
        return examinationPackageService.add(examinationPackage);
    }

    /**
     * 更新套餐
     */
    @PutMapping
    public ResponseDTO update(@RequestBody ExaminationPackage examinationPackage) {
        return examinationPackageService.update(examinationPackage);
    }

    /**
     * 删除套餐
     */
    @DeleteMapping("/{id}")
    public ResponseDTO delete(@PathVariable("id") Long id) {
        return examinationPackageService.delete(id);
    }

    @GetMapping("/page")
    public ResponseDTO selectPage(
            @RequestParam(required = false) String packageName,
            @RequestParam(required = false) String packageStatus,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return examinationPackageService.selectPage(
                packageName, packageStatus, minPrice, maxPrice, pageNum, pageSize);
    }
}