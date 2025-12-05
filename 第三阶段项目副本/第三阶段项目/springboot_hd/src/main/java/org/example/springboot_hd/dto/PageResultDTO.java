package org.example.springboot_hd.dto;

import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-10-28 15:04:15
 * @Description: 头部注释
 * @Version: 1.0
 */
@Data
public class PageResultDTO<T> {
    private List<T> list;       // 数据列表
    private Integer pageNum;    // 当前页码
    private Integer pageSize;   // 每页条数
    private Integer total;      // 总条数
    private Integer totalPage;  // 总
}
