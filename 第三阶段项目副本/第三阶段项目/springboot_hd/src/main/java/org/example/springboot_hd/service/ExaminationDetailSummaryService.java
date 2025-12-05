package org.example.springboot_hd.service;

import org.example.springboot_hd.entity.ExaminationDetailSummary;

import java.util.List;

/**小结录入*/
public interface ExaminationDetailSummaryService {
    /**
     * 保存细项小结
     */
    int saveSummary(ExaminationDetailSummary summary);

    /**
     * 保存细项小结（包含完整信息）
     */
    int saveSummaryWithDoctorInfo(ExaminationDetailSummary summary);

    boolean saveDetail(ExaminationDetailSummary detail);

    /**
     * 根据细项ID查询小结
     */
    List<ExaminationDetailSummary> getByDetailId(Long detailId);

    /**
     * 根据管理员ID查询小结
     */
    List<ExaminationDetailSummary> getByAdminId(Long adminId);

    /**
     * 根据ID查询小结
     */
    ExaminationDetailSummary getById(Long id);

    // ExaminationDetailSummaryService.java
    List<ExaminationDetailSummary> getByOrderNo(String orderNo);
}
