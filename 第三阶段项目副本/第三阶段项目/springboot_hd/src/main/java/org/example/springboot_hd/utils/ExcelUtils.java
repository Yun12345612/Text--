package org.example.springboot_hd.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.springboot_hd.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Excel处理工具类 - 提供Excel文件的读取、导出和模板生成功能
 *
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.utils
 * @Author: 夏日花店
 * @CreateTime: 2025-11-02 14:30:00
 * @Description: Excel文件操作工具类，支持用户数据的导入导出
 * @Version: 1.0
 */
public class ExcelUtils {

    /**
     * 从Excel文件读取用户数据
     *
     * @param file 上传的Excel文件
     * @return 解析后的用户列表
     * @throws Exception 文件读取或解析异常
     * @apiNote 从Excel文件中读取用户数据，自动跳过表头，支持.xlsx和.xls格式
     * @example 读取流程：文件流 → 工作簿 → 工作表 → 逐行解析 → 用户对象列表
     */
    public static List<User> readUsersFromExcel(MultipartFile file) throws Exception {
        List<User> userList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 跳过表头行（第一行）
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            // 处理数据行
            int rowNumber = 2; // 从第2行开始（表头是第1行）
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                User user = createUserFromExcelRow(row);
                if (user != null) {
                    userList.add(user);
                }
                rowNumber++;
            }
        }
        return userList;
    }

    /**
     * 从Excel行数据创建用户对象
     *
     * @param row Excel行对象
     * @return 用户对象，如果数据不完整则返回null
     * @apiNote 将Excel行数据映射到User实体对象，自动处理数据类型转换
     * @step 1. 检查必要字段 2. 数据类型转换 3. 字段映射
     */
    private static User createUserFromExcelRow(Row row) {
        // 检查数据完整性（至少需要姓名、手机号、身份证号、性别、账号）
        if (row.getPhysicalNumberOfCells() < 5) {
            return null;
        }

        User user = new User();
        try {
            // 姓名（必填）
            user.setUserName(getCellStringValue(row.getCell(0)));

            // 手机号（必填）
            user.setUserPhone(getCellStringValue(row.getCell(1)));

            // 身份证号（必填）
            user.setUserCard(getCellStringValue(row.getCell(2)));

            // 性别（必填）：支持"男"/"1"或"女"/"2"
            String gender = getCellStringValue(row.getCell(3));
            user.setUserGender("男".equals(gender) || "1".equals(gender) ? "1" : "2");

            // 年龄（可选）
            Cell ageCell = row.getCell(4);
            if (ageCell != null) {
                user.setUserAge((long) getCellNumericValue(ageCell));
            }

            // 邮箱（可选）
            user.setUserEmail(getCellStringValue(row.getCell(5)));

            // 账号（必填）
            user.setUserAccount(getCellStringValue(row.getCell(6)));

            // 密码（必填）
            user.setUserPassword(getCellStringValue(row.getCell(7)));

            // 余额（可选）
            Cell balanceCell = row.getCell(8);
            if (balanceCell != null) {
                user.setUserBalance(getCellNumericValue(balanceCell));
            }

            // 状态（可选）：支持"启用"/"1"或"禁用"/"0"
            String status = getCellStringValue(row.getCell(9));
            user.setUserStatus("启用".equals(status) || "1".equals(status) ? 1L : 0L);

        } catch (Exception e) {
            // 数据格式错误，跳过该行
            return null;
        }

        return user;
    }

    /**
     * 创建用户数据导出工作簿
     *
     * @param userList 用户数据列表
     * @return Excel工作簿对象
     * @apiNote 生成包含用户数据的Excel工作簿，自动设置表头和格式
     * @step 1. 创建工作簿 2. 创建表头 3. 填充数据 4. 自动调整列宽
     */
    public static Workbook createUserExportWorkbook(List<User> userList) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("用户数据");

        // 创建样式：表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"姓名", "手机号", "身份证号", "性别", "年龄", "邮箱", "账号", "余额", "状态", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 创建数据样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setWrapText(true);

        // 填充数据行
        int rowNum = 1;
        for (User user : userList) {
            Row row = sheet.createRow(rowNum++);

            // 姓名
            row.createCell(0).setCellValue(user.getUserName() != null ? user.getUserName() : "");

            // 手机号
            row.createCell(1).setCellValue(user.getUserPhone() != null ? user.getUserPhone() : "");

            // 身份证号
            row.createCell(2).setCellValue(user.getUserCard() != null ? user.getUserCard() : "");

            // 性别（1-男，2-女）
            row.createCell(3).setCellValue("1".equals(user.getUserGender()) ? "男" : "女");

            // 年龄
            row.createCell(4).setCellValue(user.getUserAge() != null ? user.getUserAge() : 0);

            // 邮箱
            row.createCell(5).setCellValue(user.getUserEmail() != null ? user.getUserEmail() : "");

            // 账号
            row.createCell(6).setCellValue(user.getUserAccount() != null ? user.getUserAccount() : "");

            // 余额
            row.createCell(7).setCellValue(user.getUserBalance() != null ? user.getUserBalance().doubleValue() : 0.0);

            // 状态（1-启用，0-禁用）
            row.createCell(8).setCellValue(user.getUserStatus() == 1 ? "启用" : "禁用");

            // 创建时间
            String createTime = user.getCreateTime() != null ?
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getCreateTime()) : "";
            row.createCell(9).setCellValue(createTime);
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    /**
     * 创建用户导入模板工作簿
     *
     * @return 模板工作簿对象
     * @apiNote 生成标准化的用户数据导入模板，包含表头说明和示例数据
     * @step 1. 创建工作簿 2. 设置表头说明 3. 添加示例数据 4. 设置列宽
     */
    public static Workbook createTemplateWorkbook() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("用户导入模板");

        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.RED.getIndex());
        headerStyle.setFont(headerFont);

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"姓名*", "手机号*", "身份证号*", "性别(1男2女)*", "年龄", "邮箱", "账号*", "密码*", "余额", "状态(1启用0禁用)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 示例数据
        Row exampleRow = sheet.createRow(1);
        exampleRow.createCell(0).setCellValue("张三");
        exampleRow.createCell(1).setCellValue("13800138000");
        exampleRow.createCell(2).setCellValue("350322200001011234");
        exampleRow.createCell(3).setCellValue("1");
        exampleRow.createCell(4).setCellValue(25);
        exampleRow.createCell(5).setCellValue("zhangsan@example.com");
        exampleRow.createCell(6).setCellValue("zhangsan");
        exampleRow.createCell(7).setCellValue("123456");
        exampleRow.createCell(8).setCellValue(1000.00);
        exampleRow.createCell(9).setCellValue("1");

        // 设置列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 15 * 256); // 15个字符宽度
        }

        return workbook;
    }

    /**
     * 获取单元格字符串值
     *
     * @param cell 单元格对象
     * @return 单元格的字符串值
     * @apiNote 统一处理各种类型的单元格数据，转换为字符串格式
     * @step 1. 判断单元格类型 2. 相应类型转换 3. 返回字符串结果
     */
    private static String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 日期类型
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                } else {
                    // 数字类型，去除小数位
                    double num = cell.getNumericCellValue();
                    if (num == (long) num) {
                        return String.valueOf((long) num);
                    } else {
                        return String.valueOf(num);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // 公式单元格，尝试计算结果
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return cell.getCellFormula();
                    }
                }
            default:
                return "";
        }
    }

    /**
     * 获取单元格数值
     *
     * @param cell 单元格对象
     * @return 单元格的数值
     * @apiNote 统一处理各种类型的单元格数据，转换为数值格式
     * @step 1. 判断单元格类型 2. 相应类型转换 3. 返回数值结果
     */
    private static double getCellNumericValue(Cell cell) {
        if (cell == null) return 0.0;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue() ? 1.0 : 0.0;
            default:
                return 0.0;
        }
    }
}