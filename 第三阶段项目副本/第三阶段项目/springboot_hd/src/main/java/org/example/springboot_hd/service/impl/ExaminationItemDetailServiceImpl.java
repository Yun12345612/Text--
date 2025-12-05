package org.example.springboot_hd.service.impl;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.ExaminationItemDetail;
import org.example.springboot_hd.mapper.DepartmentMapper;
import org.example.springboot_hd.mapper.ExaminationItemDetailMapper;
import org.example.springboot_hd.service.ExaminationItemDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-10-31 14:00:57
 * @Description: 检查项目明细服务实现类 - 实现检查项目明细的增删改查业务逻辑
 * @Version: 1.0
 */
@Service
public class ExaminationItemDetailServiceImpl implements ExaminationItemDetailService {

    @Autowired
    private ExaminationItemDetailMapper examinationItemDetailMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    /**
     * 新增检查项目明细
     * @param detail 检查项目明细实体
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO addItem(ExaminationItemDetail detail) {
        try {
            // 设置默认值
            if (detail.getDetailIsDelete() == null) {
                detail.setDetailIsDelete(0);
            }
            if (detail.getCreateTime() == null) {
                detail.setCreateTime(new Timestamp(System.currentTimeMillis()));
            }
            if (detail.getUpdateTime() == null) {
                detail.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            }

            int result = examinationItemDetailMapper.insert(detail);
            if (result > 0) {
                return ResponseDTO.success(detail, "新增成功");
            } else {
                return ResponseDTO.error("新增失败");
            }
        } catch (Exception e) {
            return ResponseDTO.error("新增异常: " + e.getMessage());
        }
    }

    /**
     * 软删除检查项目明细
     * @param detailId 检查项目明细ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO deleteItem(Long detailId) {
        try {
            int result = examinationItemDetailMapper.softDeleteById(detailId);
            if (result > 0) {
                return ResponseDTO.success(null, "删除成功");
            } else {
                return ResponseDTO.error("删除失败，记录可能不存在");
            }
        } catch (Exception e) {
            return ResponseDTO.error("删除异常: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询检查项目明细
     * @param detailId 检查项目明细ID
     * @return 检查项目明细信息
     */
    @Override
    public ResponseDTO getItemById(Long detailId) {
        try {
            ExaminationItemDetail detail = examinationItemDetailMapper.selectById(detailId);
            if (detail != null) {
                return ResponseDTO.success(detail, "查询成功");
            } else {
                return ResponseDTO.error(404, "记录不存在");
            }
        } catch (Exception e) {
            return ResponseDTO.error("查询异常: " + e.getMessage());
        }
    }

    /**
     * 查询所有检查项目明细（未删除的）
     * @return 检查项目明细列表
     */
    @Override
    public ResponseDTO getAllItems() {
        try {
            List<ExaminationItemDetail> list = examinationItemDetailMapper.selectAll();
            return ResponseDTO.success(list, "查询成功");
        } catch (Exception e) {
            return ResponseDTO.error("查询异常: " + e.getMessage());
        }
    }

    /**
     * 分页查询检查项目明细
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Override
    public ResponseDTO getItemsByPage(Integer pageNum, Integer pageSize) {
        try {
            // 参数校验和默认值设置
            if (pageNum == null || pageNum < 1) {
                pageNum = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 10;
            }
            int offset = (pageNum - 1) * pageSize;

            Map<String, Object> params = new HashMap<>();
            params.put("offset", offset);
            params.put("pageSize", pageSize);

            List<ExaminationItemDetail> list = examinationItemDetailMapper.selectPage(params);

            // 封装分页信息
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("list", list);
            pageData.put("pageNum", pageNum);
            pageData.put("pageSize", pageSize);
            pageData.put("total", examinationItemDetailMapper.count());

            return ResponseDTO.success(pageData, "分页查询成功");
        } catch (Exception e) {
            return ResponseDTO.error("分页查询异常: " + e.getMessage());
        }
    }

    /**
     * 更新检查项目明细
     * @param detail 检查项目明细实体
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO updateItem(ExaminationItemDetail detail) {
        try {
            // 设置更新时间
            detail.setUpdateTime(new Timestamp(System.currentTimeMillis()));

            int result = examinationItemDetailMapper.update(detail);
            if (result > 0) {
                return ResponseDTO.success(detail, "更新成功");
            } else {
                return ResponseDTO.error("更新失败，记录可能不存在");
            }
        } catch (Exception e) {
            return ResponseDTO.error("更新异常: " + e.getMessage());
        }
    }

    /**
     * 根据条件查询检查项目明细
     * @param condition 查询条件
     * @return 符合条件的检查项目明细列表
     */
    @Override
    public ResponseDTO getItemsByCondition(ExaminationItemDetail condition) {
        try {
            List<ExaminationItemDetail> list = examinationItemDetailMapper.selectByCondition(condition);
            return ResponseDTO.success(list, "条件查询成功");
        } catch (Exception e) {
            return ResponseDTO.error("条件查询异常: " + e.getMessage());
        }
    }

    /**
     * 统计检查项目明细总数（未删除的）
     * @return 总数
     */
    @Override
    public ResponseDTO getCount() {
        try {
            Long count = examinationItemDetailMapper.count();
            return ResponseDTO.success(count, "统计成功");
        } catch (Exception e) {
            return ResponseDTO.error("统计异常: " + e.getMessage());
        }
    }
}