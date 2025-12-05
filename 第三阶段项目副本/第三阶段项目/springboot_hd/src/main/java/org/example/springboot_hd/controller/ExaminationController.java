package org.example.springboot_hd.controller;

import org.example.springboot_hd.entity.Examination;
import org.example.springboot_hd.service.ExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-10-29 15:16:29
 * @Description: 体检套餐控制器 - 处理体检套餐相关的HTTP请求和响应
 * @Version: 1.0
 */
@RestController  // Spring控制层注解，标识这是一个RESTful风格的控制器，会自动将返回对象转换为JSON
@RequestMapping("/examination")  // 基础路径，实际对外为 /api/examination（来自全局 context-path）
public class ExaminationController {

    @Autowired  // 依赖注入注解，自动注入ExaminationService业务层实例
    private ExaminationService examinationService;

    /**
     * 获取所有体检套餐列表 - GET查询请求
     * @return Map<String, Object> 返回统一格式的响应结果
     * @description 查询所有套餐数据，无分页，适合数据量小的场景
     * @api GET http://localhost:8080/api/examination/list
     */
    @GetMapping("/list")  // HTTP GET请求映射，处理/list路径的GET请求
    public Map<String, Object> getAllExaminations() {
        Map<String, Object> result = new HashMap<>();  // 创建响应结果Map
        try {
            // 调用业务层获取所有套餐数据
            List<Examination> examinations = examinationService.getAllExaminations();
            result.put("code", 200);  // 成功状态码
            result.put("message", "查询成功");  // 成功消息
            result.put("data", examinations);  // 返回的数据
        } catch (Exception e) {
            // 异常处理：服务器内部错误
            result.put("code", 500);  // 服务器错误状态码
            result.put("message", "查询失败: " + e.getMessage());  // 错误消息
        }
        return result;  // 返回JSON格式的响应
    }

    /**
     * 分页查询体检套餐 - GET查询请求
     * @param page 页码，从1开始，默认值1
     * @param pageSize 每页显示数量，默认值10
     * @return Map<String, Object> 返回分页数据和分页信息
     * @description 分页查询套餐数据，适合数据量大的场景
     * @api GET http://localhost:8080/api/examination/page?page=1&pageSize=10
     */
    @GetMapping("/page")  // HTTP GET请求映射，处理/page路径的GET请求
    public Map<String, Object> getExaminationsByPage(
            @RequestParam(defaultValue = "1") Integer page,  // 请求参数注解，设置默认值
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用业务层分页查询
            List<Examination> examinations = examinationService.getExaminationsByPage(page, pageSize);
            Integer total = examinationService.getExaminationCount();  // 获取总记录数

            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", examinations);  // 当前页数据
            result.put("total", total);  // 总记录数
            result.put("page", page);  // 当前页码
            result.put("pageSize", pageSize);  // 每页大小
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 根据ID查询体检套餐详情 - GET查询请求
     * @param id 套餐ID，路径参数
     * @return Map<String, Object> 返回单个套餐详情或错误信息
     * @description 根据主键ID精确查询单个套餐详情
     * @api GET http://localhost:8080/api/examination/1
     */
    @GetMapping("/{id}")  // HTTP GET请求映射，处理/{id}路径的GET请求，{id}为路径变量
    public Map<String, Object> getExaminationById(@PathVariable Long id) {  // 路径变量注解，从URL路径中获取id
        Map<String, Object> result = new HashMap<>();
        try {
            Examination examination = examinationService.getExaminationById(id);
            if (examination != null) {
                result.put("code", 200);
                result.put("message", "查询成功");
                result.put("data", examination);
            } else {
                result.put("code", 404);  // 资源未找到状态码
                result.put("message", "套餐不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 新增体检套餐 - POST创建请求
     * @param examination 套餐对象，从请求体中自动绑定
     * @return Map<String, Object> 返回新增结果
     * @description 创建新的体检套餐，包含基础数据验证
     * @api POST http://localhost:8080/api/examination/add
     * @body {"name": "基础体检", "price": 299.00}
     */
    @PostMapping("/add")  // HTTP POST请求映射，处理/add路径的POST请求
    public Map<String, Object> addExamination(@RequestBody Examination examination) {  // 请求体注解，将JSON自动转换为对象
        Map<String, Object> result = new HashMap<>();
        try {
            // 基础数据验证 - 控制器层验证
            if (examination.getName() == null || examination.getName().trim().isEmpty()) {
                result.put("code", 400);  // 客户端错误状态码
                result.put("message", "套餐名称不能为空");
                return result;
            }

            // 调用业务层新增套餐
            boolean success = examinationService.addExamination(examination);
            if (success) {
                result.put("code", 200);
                result.put("message", "新增套餐成功");
                result.put("data", examination);  // 返回新增的套餐数据（包含生成的ID）
            } else {
                result.put("code", 500);
                result.put("message", "新增套餐失败");
            }
        } catch (RuntimeException e) {
            // 业务规则异常处理（如名称重复）
            result.put("code", 400);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            // 系统异常处理
            result.put("code", 500);
            result.put("message", "新增套餐失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 修改体检套餐 - PUT更新请求
     * @param examination 套餐对象，必须包含ID
     * @return Map<String, Object> 返回修改结果
     * @description 更新已存在的体检套餐信息
     * @api PUT http://localhost:8080/api/examination/update
     * @body {"id": 1, "name": "升级基础体检", "price": 399.00}
     */
    @PutMapping("/update")  // HTTP PUT请求映射，处理/update路径的PUT请求
    public Map<String, Object> updateExamination(@RequestBody Examination examination) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 基础数据验证
            if (examination.getId() == null) {
                result.put("code", 400);
                result.put("message", "套餐ID不能为空");
                return result;
            }
            if (examination.getName() == null || examination.getName().trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "套餐名称不能为空");
                return result;
            }

            boolean success = examinationService.updateExamination(examination);
            if (success) {
                result.put("code", 200);
                result.put("message", "修改套餐成功");
                result.put("data", examination);
            } else {
                result.put("code", 500);
                result.put("message", "修改套餐失败");
            }
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "修改套餐失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除体检套餐 - DELETE删除请求
     * @param id 套餐ID，路径参数
     * @return Map<String, Object> 返回删除结果
     * @description 根据ID删除指定的体检套餐
     * @api DELETE http://localhost:8080/api/examination/delete/1
     */
    @DeleteMapping("/delete/{id}")  // HTTP DELETE请求映射，处理/delete/{id}路径的DELETE请求
    public Map<String, Object> deleteExamination(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = examinationService.deleteExamination(id);
            if (success) {
                result.put("code", 200);
                result.put("message", "删除套餐成功");
            } else {
                result.put("code", 500);
                result.put("message", "删除套餐失败");
            }
        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除套餐失败: " + e.getMessage());
        }
        return result;
    }
}