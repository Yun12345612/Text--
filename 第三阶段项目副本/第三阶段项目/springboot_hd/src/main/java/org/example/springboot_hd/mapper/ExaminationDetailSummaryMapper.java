package org.example.springboot_hd.mapper;

import org.example.springboot_hd.entity.ExaminationDetailSummary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**小结录入*/
public interface ExaminationDetailSummaryMapper {

    /**
     * 插入细项小结
     */
    int insert(ExaminationDetailSummary summary);

    /**
     * 插入细项小结（包含完整信息）
     */
    int insertWithDoctorInfo(ExaminationDetailSummary summary);
    /**
     * 根据细项ID查询小结
     * @param detailId 细项ID
     * @return 细项小结列表
     */
    List<ExaminationDetailSummary> selectByDetailId(@Param("detailId") Long detailId);

    /**
     * 根据管理员ID查询小结
     * @param adminId 管理员ID
     * @return 细项小结列表
     */
    List<ExaminationDetailSummary> selectByAdminId(@Param("adminId") Long adminId);

    /**
     * 根据小结ID查询单条记录
     * @param id 小结ID
     * @return 细项小结
     */
    ExaminationDetailSummary selectById(@Param("id") Long id);

    // ExaminationDetailSummaryMapper
    List<ExaminationDetailSummary> selectByOrderNo(@Param("orderNo") String orderNo);
}
