package org.example.springboot_hd.service.impl;
import org.example.springboot_hd.dto.ResponseDTO;

import org.example.springboot_hd.mapper.ExaminationItemMapper;
import org.example.springboot_hd.service.ExaminationItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.springboot_hd.entity.ExaminationItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-10-30 10:45:19
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class ExaminationItemServiceImpl implements ExaminationItemService {

    @Autowired
    private ExaminationItemMapper examinationItemMapper;
    // 1. 新增项目
    @Override
    public ResponseDTO addItem(ExaminationItem examinationItem) {
        try {
            // 参数校验
            if (examinationItem == null || examinationItem.getItemName() == null || examinationItem.getItemName().trim().isEmpty()) {
                return ResponseDTO.error(400, "项目名称不能为空");
            }
            if (examinationItem.getDepartmentId() == null) {
                return ResponseDTO.error(400, "科室ID不能为空");
            }
            if (examinationItem.getItemPrice() == null || examinationItem.getItemPrice() < 0) {
                return ResponseDTO.error(400, "项目价格必须大于等于0");
            }

            // 类型匹配：itemIsDelete 改为 Integer 类型（与数据库 TINYINT 对齐）
            examinationItem.setItemIsDelete(0);
            // 状态默认值：active（启用）
            if (examinationItem.getItemStatus() == null || examinationItem.getItemStatus().trim().isEmpty()) {
                examinationItem.setItemStatus("active");
            }

            int rows = examinationItemMapper.insert(examinationItem);
            if (rows > 0) {
                return ResponseDTO.success(examinationItem.getItemId(), "项目新增成功");
            } else {
                return ResponseDTO.error("项目新增失败");
            }
        } catch (Exception e) {
            return ResponseDTO.error(500, "新增失败：" + e.getMessage());
        }
    }

    // 2. 更新项目（补充状态合法性、类型一致性校验）
    @Override
    public ResponseDTO updateItem(ExaminationItem item) {
        try {
            if (item == null || item.getItemId() == null) {
                return ResponseDTO.error(400, "项目ID不能为空");
            }
            // 校验状态合法性
            if (item.getItemStatus() != null && !item.getItemStatus().trim().isEmpty()) {
                if (!"active".equals(item.getItemStatus()) && !"inactive".equals(item.getItemStatus())) {
                    return ResponseDTO.error(400, "项目状态仅支持 active（启用）或 inactive（停用）");
                }
            }

            ExaminationItem examinationItem = examinationItemMapper.selectById(item.getItemId());
            if (examinationItem == null) {
                return ResponseDTO.error(404, "项目不存在或已被删除");
            }

            int rows = examinationItemMapper.update(item);
            if (rows > 0) {
                return ResponseDTO.success("项目更新成功");
            } else {
                return ResponseDTO.error("项目更新失败（无有效修改）");
            }
        } catch (Exception e) {
            return ResponseDTO.error(500, "更新失败：" + e.getMessage());
        }
    }

    // 3. 软删除项目（类型匹配：itemIsDelete 改为 Integer 校验）
    @Override
    public ResponseDTO softDeleteItem(Long itemId) {
        try {
            if (itemId == null) {
                return ResponseDTO.error(400, "项目ID不能为空");
            }

            ExaminationItem existItem = examinationItemMapper.selectById(itemId);
            if (existItem == null) {
                return ResponseDTO.error(404, "项目不存在或已被删除");
            }
            // 类型匹配：itemIsDelete 是 Integer 类型，直接比较数值
            if (existItem.getItemIsDelete() == 1) {
                return ResponseDTO.error(400, "项目已删除，无需重复操作");
            }

            int rows = examinationItemMapper.softDelete(itemId);
            if (rows > 0) {
                return ResponseDTO.success("项目删除成功");
            } else {
                return ResponseDTO.error("项目删除失败");
            }
        } catch (Exception e) {
            return ResponseDTO.error(500, "删除失败：" + e.getMessage());
        }
    }

    // 4. 根据ID查询（保持不变）
    @Override
    public ResponseDTO getItemById(Long itemId) {
        try {
            if (itemId == null) {
                return ResponseDTO.error(400, "项目ID不能为空");
            }

            ExaminationItem item = examinationItemMapper.selectById(itemId);
            if (item != null) {
                return ResponseDTO.success(item, "查询成功");
            } else {
                return ResponseDTO.error(404, "项目不存在或已被删除");
            }
        } catch (Exception e) {
            return ResponseDTO.error(500, "查询失败：" + e.getMessage());
        }
    }

    // 5. 分页多条件查询（保持不变，已支持总条数统计）
    @Override
    public ResponseDTO getItemList(ExaminationItem query) {
        try {
            // 处理分页参数（默认第一页，每页10条）
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