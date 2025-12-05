package org.example.springboot_hd.service.impl;

import org.example.springboot_hd.entity.Examination;
import org.example.springboot_hd.mapper.ExaminationMapper;
import org.example.springboot_hd.service.ExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-10-29 15:10:03
 * @Description: 体检套餐服务实现类 - 负责处理体检套餐相关的业务逻辑
 * @Version: 1.0
 */

@Service  // Spring服务层注解，标识这是一个业务逻辑组件，会被Spring容器管理
public class ExaminationServiceImpl implements ExaminationService {

    @Autowired  // 依赖注入注解，自动注入ExaminationMapper实例
    private ExaminationMapper examinationMapper;

    /**
     * 查询所有体检套餐列表
     * @return List<Examination> 返回所有体检套餐的列表
     * @description 调用Mapper层的selectAll方法，获取所有套餐数据
     */
    @Override
    public List<Examination> getAllExaminations() {
        return examinationMapper.selectAll();  // 执行查询所有套餐的SQL
    }

    /**
     * 根据ID查询单个体检套餐详情
     * @param id 套餐ID
     * @return Examination 返回对应的体检套餐对象，如果不存在返回null
     * @description 通过主键ID精确查询单个套餐
     */
    @Override
    public Examination getExaminationById(Long id) {
        return examinationMapper.selectById(id);  // 执行根据ID查询的SQL
    }

    /**
     * 新增体检套餐
     * @param examination 套餐对象，包含名称、价格等信息
     * @return boolean 新增成功返回true，失败返回false
     * @description 包含业务验证：名称唯一性检查、价格合法性检查
     */
    @Override
    @Transactional  // 事务注解，保证新增操作的原子性，要么全部成功，要么全部回滚
    public boolean addExamination(Examination examination) {
        // 验证套餐名称是否已存在 - 业务规则验证
        if (examinationMapper.countByName(examination.getName()) > 0) {
            throw new RuntimeException("套餐名称已存在，请使用其他名称");
        }

        // 验证价格是否合法 - 业务规则验证
        if (examination.getPrice() == null || examination.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("套餐价格必须大于0");
        }

        return examinationMapper.insert(examination) > 0;  // 执行插入套餐的SQL
    }

    /**
     * 修改体检套餐信息
     * @param examination 套餐对象，必须包含ID、名称、价格等信息
     * @return boolean 修改成功返回true，失败返回false
     * @description 包含业务验证：套餐存在性检查、名称唯一性检查、价格合法性检查
     */
    @Override
    @Transactional  // 事务注解，保证更新操作的原子性
    public boolean updateExamination(Examination examination) {
        // 验证套餐是否存在 - 业务规则验证
        Examination existing = examinationMapper.selectById(examination.getId());
        if (existing == null) {
            throw new RuntimeException("套餐不存在，无法修改");
        }

        // 如果名称改变，验证新名称是否已存在 - 业务规则验证
        if (!existing.getName().equals(examination.getName())) {
            if (examinationMapper.countByName(examination.getName()) > 0) {
                throw new RuntimeException("套餐名称已存在，请使用其他名称");
            }
        }

        // 验证价格是否合法 - 业务规则验证
        if (examination.getPrice() == null || examination.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("套餐价格必须大于0");
        }

        return examinationMapper.update(examination) > 0;  // 执行更新套餐的SQL
    }

    /**
     * 删除体检套餐
     * @param id 套餐ID
     * @return boolean 删除成功返回true，失败返回false
     * @description 包含业务验证：套餐存在性检查
     */
    @Override
    @Transactional  // 事务注解，保证删除操作的原子性
    public boolean deleteExamination(Long id) {
        // 验证套餐是否存在 - 业务规则验证
        Examination existing = examinationMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("套餐不存在，无法删除");
        }

        return examinationMapper.deleteById(id) > 0;  // 执行删除套餐的SQL
    }

    /**
     * 分页查询体检套餐
     * @param page 页码，从1开始
     * @param pageSize 每页显示数量
     * @return List<Examination> 返回当前页的套餐列表
     * @description 实现分页查询功能，计算偏移量后调用Mapper
     */
    @Override
    public List<Examination> getExaminationsByPage(Integer page, Integer pageSize) {
        // 参数校验和默认值设置
        if (page == null || page < 1) {
            page = 1;  // 默认第一页
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;  // 默认每页10条
        }

        // 计算偏移量：(页码-1) * 每页数量
        int offset = (page - 1) * pageSize;
        return examinationMapper.selectByPage(offset, pageSize);  // 执行分页查询的SQL
    }

    /**
     * 获取体检套餐总数
     * @return Integer 返回套餐总数量
     * @description 用于分页时计算总页数
     */
    @Override
    public Integer getExaminationCount() {
        return examinationMapper.selectCount();  // 执行统计总数的SQL
    }
}