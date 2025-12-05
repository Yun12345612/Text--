package com.cykj.service.Impl;

import com.cykj.dto.ResponseDTO;
import com.cykj.mapper.ExaminationItemMapper;
import com.cykj.pojo.ExaminationItem;
import com.cykj.service.ExaminationItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.service.Impl
 * @Author: 夏日花店
 * @CreateTime: 2025-11-04 15:25:07
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class ExaminationItemServiceImpl implements ExaminationItemService {
    @Autowired
    private ExaminationItemMapper examinationItemMapper;
    // 1.分页多条件查询
    @Override
    public ResponseDTO getItemList(ExaminationItem query) {
        try {
            // 处理分页参数
            if (query.getPageNum() == null || query.getPageNum() < 0) {
                query.setPageNum(0); // LIMIT 偏移量从0开始
            }
            if (query.getPageSize() == null || query.getPageSize() <= 0) {
                query.setPageSize(10);
            }
            // 查询列表和总条数
            List<ExaminationItem> list = examinationItemMapper.selectList(query);
            Long total = examinationItemMapper.count(query);
            // 封装分页数据
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("total", total);
            pageData.put("list", list);
            pageData.put("pageNum", query.getPageNum());
            pageData.put("pageSize", query.getPageSize());
            return ResponseDTO.success(pageData, "查询成功");
        } catch (Exception e) {
            return ResponseDTO.error(500, "查询失败：" + e.getMessage());
        }
    }
}
