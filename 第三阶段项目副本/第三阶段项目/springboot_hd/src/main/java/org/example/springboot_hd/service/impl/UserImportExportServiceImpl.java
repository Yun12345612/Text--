package org.example.springboot_hd.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.example.springboot_hd.dto.ExportDTO;
import org.example.springboot_hd.dto.ImportDTO;
import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.User;
import org.example.springboot_hd.mapper.UserImportExportMapper;
import org.example.springboot_hd.mapper.UserMapper;
import org.example.springboot_hd.service.UserImportExportService;
import org.example.springboot_hd.utils.CsvUtils;
import org.example.springboot_hd.utils.ExcelUtils;
import org.example.springboot_hd.utils.MD5Utils;
import org.example.springboot_hd.utils.UserImportExportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户导入导出服务实现类 - 支持Excel和CSV格式的用户数据导入导出功能
 *
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-11-02 12:12:04
 * @Description: 用户导入导出服务实现类，支持Excel和CSV格式，提供完整的导入导出功能
 * @Version: 2.0 - Excel格式支持
 */
@Service
public class UserImportExportServiceImpl implements UserImportExportService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserImportExportMapper userImportExportMapper;

    /**
     * 导入用户数据（支持Excel和CSV双格式）
     *
     * @param file 上传的文件，支持.xlsx、.xls、.csv格式
     * @return ResponseDTO 导入结果，包含成功/失败数量和详细错误信息
     * @throws Exception 文件读取失败或格式不正确时抛出异常
     * @apiNote 支持批量导入用户数据，自动识别文件格式，统一验证和保存逻辑
     * @example 导入流程：
     * 用户上传文件 → 格式识别 → 解析数据 → 验证保存 → 返回结果
     * 支持格式：
     * - Excel: .xlsx, .xls
     * - CSV: .csv
     */
    @Override
    public ResponseDTO importUsers(MultipartFile file) {
        // 初始化导入结果对象
        ImportDTO.Result result = new ImportDTO.Result();
        result.setErrorList(new ArrayList<>());

        try {
            // 1. 文件空值检查
            if (file.isEmpty()) {
                return ResponseDTO.error("请选择文件");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                return ResponseDTO.error("文件名不能为空");
            }

            String lowerFileName = fileName.toLowerCase();
            List<User> userList;

            // 2. 根据文件格式选择解析方式
            if (lowerFileName.endsWith(".xlsx") || lowerFileName.endsWith(".xls")) {
                // Excel文件处理
                userList = ExcelUtils.readUsersFromExcel(file);
            } else if (lowerFileName.endsWith(".csv")) {
                // CSV文件处理（保持原有逻辑）
                userList = readUsersFromCsv(file);
            } else {
                return ResponseDTO.error("不支持的文件格式，请上传Excel(.xlsx/.xls)或CSV(.csv)文件");
            }

            // 3. 统一验证和保存逻辑
            for (int i = 0; i < userList.size(); i++) {
                User user = userList.get(i);
                try {
                    validateAndSaveUser(user);
                    result.setSuccess(result.getSuccess() + 1);
                } catch (Exception e) {
                    // 记录导入失败信息
                    result.getErrorList().add(new ImportDTO.Error(
                            i + 2, // 行号：Excel从第2行开始（表头+1），CSV从第2行开始
                            user.getUserName(),
                            user.getUserPhone(),
                            e.getMessage()
                    ));
                    result.setFail(result.getFail() + 1);
                }
            }

            // 4. 返回导入结果
            return ResponseDTO.success(result,
                    String.format("导入完成：成功 %d 条，失败 %d 条", result.getSuccess(), result.getFail()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error("文件读取失败: " + e.getMessage());
        }
    }

    /**
     * 导出用户数据到Excel文件
     *
     * @param user     查询条件对象，支持按条件筛选导出数据
     * @param response HTTP响应对象，用于输出Excel文件流
     * @return ResponseDTO 导出结果，包含文件名、数据量等统计信息
     * @throws Exception 数据查询或文件写入失败时抛出异常
     * @apiNote 根据查询条件导出用户数据到Excel格式，支持条件筛选
     * @example 导出流程：
     * 查询数据 → 创建Excel工作簿 → 设置响应头 → 输出文件流 → 返回统计信息
     * 导出字段：姓名,手机号,身份证号,性别,年龄,邮箱,账号,余额,状态,创建时间
     */
    @Override
    public void exportUsers(User user, HttpServletResponse response) {
        try {
            // 1. 查询用户数据
            List<User> userList = userMapper.selectUserList(user);

            // 2. 设置Excel文件下载响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "用户数据_" + System.currentTimeMillis() + ".xlsx";
            String encodedFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);

            // 3. 生成Excel文件并输出
            Workbook workbook = ExcelUtils.createUserExportWorkbook(userList);
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 下载用户数据导入模板（Excel格式）
     *
     * @param response HTTP响应对象，用于设置文件下载头信息
     * @return ResponseDTO 下载结果，包含模板文件信息
     * @throws Exception 响应流写入失败时抛出异常
     * @apiNote 提供标准化的Excel用户数据导入模板，包含表头说明和示例数据
     * @example 模板包含字段：
     * 姓名*,手机号*,身份证号*,性别(1男2女)*,年龄,邮箱,账号*,密码*,余额,状态(1启用0禁用)
     * 标*字段为必填项
     */
    @Override
    public void downloadTemplate(HttpServletResponse response) {
        try {
            // 设置Excel响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "用户导入模板.xlsx";
            String encodedFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);

            // 生成模板文件并输出
            Workbook workbook = ExcelUtils.createTemplateWorkbook();
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("下载模板失败: " + e.getMessage());
        }
    }

    /**
     * 从CSV文件读取用户数据（保持向后兼容）
     *
     * @param file 上传的CSV文件
     * @return 解析后的用户列表
     * @throws Exception 文件读取或解析异常
     * @apiNote 原有的CSV文件读取逻辑，保持对CSV格式的兼容支持
     * @step 1. 读取文件内容 2. 按行分割 3. 跳过表头 4. 解析每行数据
     */
    private List<User> readUsersFromCsv(MultipartFile file) throws Exception {
        List<User> userList = new ArrayList<>();
        String content = new String(file.getBytes(), "UTF-8");
        String[] lines = content.split("\r\n|\n");

        // 处理每一行数据（跳过表头）
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue; // 跳过空行

            try {
                String[] fields = CsvUtils.parseCSVLine(line);
                if (fields.length >= 5) {
                    User user = UserImportExportUtils.createUserFromCSV(fields);
                    userList.add(user);
                }
            } catch (Exception e) {
                // 跳过格式错误的行，在统一验证阶段会记录错误
                continue;
            }
        }
        return userList;
    }

    /**
     * 验证并保存用户数据
     *
     * @param user 待验证和保存的用户对象
     * @throws RuntimeException 数据验证失败或保存失败时抛出业务异常
     * @apiNote 执行完整的数据验证流程，确保数据完整性和业务规则
     * @step 1. 基础数据格式验证 → 2. 唯一性检查 → 3. 密码加密 → 4. 数据保存
     */
    private void validateAndSaveUser(User user) {
        // 1. 基础数据验证（使用工具类）
        UserImportExportUtils.validateImportUser(user);

        // 2. 唯一性检查 - 手机号
        if (userImportExportMapper.existsByPhone(user.getUserPhone())) {
            throw new RuntimeException("手机号已存在");
        }

        // 2. 唯一性检查 - 身份证号
        if (userImportExportMapper.existsByIdCard(user.getUserCard())) {
            throw new RuntimeException("身份证号已存在");
        }

        // 2. 唯一性检查 - 账号
        if (userImportExportMapper.existsByAccount(user.getUserAccount())) {
            throw new RuntimeException("账号已存在");
        }

        // 3. 密码安全处理 - MD5加密
        String encryptedPassword = MD5Utils.encrypt(user.getUserPassword(), MD5Utils.generateSalt());
        user.setUserPassword(encryptedPassword);

        // 4. 保存用户数据到数据库
        int result = userMapper.insertUser(user);
        if (result <= 0) {
            throw new RuntimeException("保存到数据库失败");
        }
    }
}