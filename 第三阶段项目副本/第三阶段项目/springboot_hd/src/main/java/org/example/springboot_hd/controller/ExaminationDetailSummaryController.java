package org.example.springboot_hd.controller;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.ExaminationDetailSummary;
import org.example.springboot_hd.service.ExaminationDetailSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-11-08 09:51:16
 * @Description: 小结录入
 * @Version: 1.0
 */

@RestController
@RequestMapping("/examination-detail-summary")
public class ExaminationDetailSummaryController {

    @Autowired
    private ExaminationDetailSummaryService examinationDetailSummaryService;

    /**
     * 保存细项小结 - 统一使用ResponseDTO
     */
    @PostMapping("/save")
    public ResponseDTO saveSummary(@RequestBody ExaminationDetailSummary summary) {
        try {
            int count = examinationDetailSummaryService.saveSummary(summary);
            if (count > 0) {
                return ResponseDTO.success(summary,"细项小结保存成功");
            } else {
                return ResponseDTO.error("细项小结保存失败");
            }
        } catch (Exception e) {
            return ResponseDTO.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 保存细项小结（包含完整信息） - 统一使用ResponseDTO
     */
    @PostMapping("/save-with-doctor")
    public ResponseDTO saveSummaryWithDoctor(@RequestBody ExaminationDetailSummary summary) {
        System.out.println("订单编号" + summary.getOrderOn());
        try {
            int count = examinationDetailSummaryService.saveSummaryWithDoctorInfo(summary);
            if (count > 0) {
                return ResponseDTO.success(summary,"细项小结保存成功");
            } else {
                return ResponseDTO.error("细项小结保存失败");
            }
        } catch (Exception e) {
            return ResponseDTO.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 根据细项ID查询小结
     */
    @GetMapping("/get-by-detail/{detailId}")
    public ResponseDTO getByDetailId(@PathVariable Long detailId) {
        try {
            System.out.println("查询细项小结，细项ID: " + detailId);

            List<ExaminationDetailSummary> details = examinationDetailSummaryService.getByDetailId(detailId);

            if (details != null && !details.isEmpty()) {
                System.out.println("查询成功，找到 " + details.size() + " 条记录");
                return ResponseDTO.success(details);
            } else {
                System.out.println("查询成功，但未找到相关记录");
                return ResponseDTO.success("该细项暂无小结数据", null);
            }
        } catch (Exception e) {
            System.err.println("查询细项小结失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseDTO.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据管理员ID查询小结
     */
    @GetMapping("/get-by-admin/{adminId}")
    public ResponseDTO getByAdminId(@PathVariable Long adminId) {
        try {
            System.out.println("查询管理员小结，管理员ID: " + adminId);

            List<ExaminationDetailSummary> details = examinationDetailSummaryService.getByAdminId(adminId);

            if (details != null && !details.isEmpty()) {
                return ResponseDTO.success(details);
            } else {
                return ResponseDTO.success("该管理员暂无小结数据", null);
            }
        } catch (Exception e) {
            System.err.println("查询管理员小结失败: " + e.getMessage());
            return ResponseDTO.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询单条小结
     */
    @GetMapping("/get-by-id/{id}")
    public ResponseDTO getById(@PathVariable Long id) {
        try {
            ExaminationDetailSummary detail = examinationDetailSummaryService.getById(id);

            if (detail != null) {
                return ResponseDTO.success(detail);
            } else {
                return ResponseDTO.success("未找到对应的小结数据", null);
            }
        } catch (Exception e) {
            System.err.println("查询小结失败: " + e.getMessage());
            return ResponseDTO.error("查询失败: " + e.getMessage());
        }
    }
// ExaminationDetailSummaryController.java

    /**
     * 根据订单号查询体检细项小结
     */
    @GetMapping("/get-by-order/{orderNo}")
    public ResponseDTO getByOrderNo(@PathVariable String orderNo) {
        try {
            System.out.println("查询订单小结，订单号: " + orderNo);

            List<ExaminationDetailSummary> details = examinationDetailSummaryService.getByOrderNo(orderNo);

            if (details != null && !details.isEmpty()) {
                System.out.println("查询成功，找到 " + details.size() + " 条记录");
                return ResponseDTO.success(details);
            } else {
                System.out.println("查询成功，但未找到相关记录");
                return ResponseDTO.success("该订单暂无小结数据", null);
            }
        } catch (Exception e) {
            System.err.println("查询订单小结失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseDTO.error("查询失败: " + e.getMessage());
        }
    }
}
