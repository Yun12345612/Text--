    package org.example.springboot_hd.service.impl;

    import org.example.springboot_hd.entity.ExaminationDetailSummary;
    import org.example.springboot_hd.mapper.ExaminationDetailSummaryMapper;
    import org.example.springboot_hd.service.ExaminationDetailSummaryService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    /**
     * @BelongsProject: springboot_hd
     * @BelongsPackage: org.example.springboot_hd.service.impl
     * @Author: 夏日花店
     * @CreateTime: 2025-11-08 09:50:28
     * @Description: 小结录入
     * @Version: 1.0
     */
    @Service
    public class ExaminationDetailSummaryServiceImpl implements ExaminationDetailSummaryService {

        @Autowired
        private ExaminationDetailSummaryMapper examinationDetailSummaryMapper;

        @Override
        @Transactional
        public int saveSummary(ExaminationDetailSummary summary) {
            return examinationDetailSummaryMapper.insert(summary);
        }

        @Override
        @Transactional
        public int saveSummaryWithDoctorInfo(ExaminationDetailSummary summary) {
            return examinationDetailSummaryMapper.insertWithDoctorInfo(summary);
        }

        @Override
        public boolean saveDetail(ExaminationDetailSummary detail) {
            try {
                return examinationDetailSummaryMapper.insertWithDoctorInfo(detail) > 0;
            } catch (Exception e) {
                throw new RuntimeException("保存细项小结失败: " + e.getMessage(), e);
            }
        }

        @Override
        public List<ExaminationDetailSummary> getByDetailId(Long detailId) {
            try {
                return examinationDetailSummaryMapper.selectByDetailId(detailId);
            } catch (Exception e) {
                throw new RuntimeException("查询细项小结失败: " + e.getMessage(), e);
            }
        }

        @Override
        public List<ExaminationDetailSummary> getByAdminId(Long adminId) {
            try {
                return examinationDetailSummaryMapper.selectByAdminId(adminId);
            } catch (Exception e) {
                throw new RuntimeException("查询管理员小结失败: " + e.getMessage(), e);
            }
        }

        @Override
        public ExaminationDetailSummary getById(Long id) {
            try {
                return examinationDetailSummaryMapper.selectById(id);
            } catch (Exception e) {
                throw new RuntimeException("查询小结失败: " + e.getMessage(), e);
            }
        }

        // ExaminationDetailSummaryServiceImpl.java
        @Override
        public List<ExaminationDetailSummary> getByOrderNo(String orderNo) {
            try {
                return examinationDetailSummaryMapper.selectByOrderNo(orderNo);
            } catch (Exception e) {
                throw new RuntimeException("根据订单号查询小结失败: " + e.getMessage(), e);
            }
        }
    }
