package org.example.springboot_hd.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.utils
 * @Author: 夏日花店
 * @CreateTime: 2025-11-02 12:46:01
 * @Description: CSV文件处理工具类 - 提供CSV格式数据的解析、转义和格式化功能
 * CSV（Comma-Separated Values）纯文本格式的数据文件工具类:
 * - 简单易用：不需要复杂的库，字符串处理即可
 * - 兼容性好：Excel、WPS、Numbers都能打开
 * - 文件小巧：没有Excel的复杂格式和样式
 * - 开发简单：几行代码就能实现读写
 * @Version: 1.0
 */
public class CsvUtils {
    /**
     * 解析CSV格式的单行数据
     *
     * @param line CSV格式的单行字符串
     * @return String[] 解析后的字段数组，已去除前后空格
     * @apiNote 支持处理包含引号和逗号的复杂CSV格式，自动处理字段转义
     * @logic 遍历每个字符，处理引号转义和逗号分隔
     * @example
     * 输入: "张三,13800138000,"北京,朝阳",25"
     * 输出: ["张三", "13800138000", "北京,朝阳", "25"]
     * @attention 引号内的逗号不会被作为分隔符处理
     */
    public static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes; // 切换引号状态
            } else if (c == ',' && !inQuotes) {
                // 非引号内的逗号作为字段分隔符
                fields.add(field.toString().trim());
                field.setLength(0); // 清空StringBuilder
            } else {
                field.append(c); // 累积字段字符
            }
        }
        // 添加最后一个字段
        fields.add(field.toString().trim());
        return fields.toArray(new String[0]);
    }

    /**
     * 对CSV字段值进行转义处理
     *
     * @param value 待转义的原始字符串
     * @return String 转义后的CSV安全字符串
     * @apiNote 确保字段值中的特殊字符不会破坏CSV格式结构
     * @rule
     * - 包含逗号、引号、换行符的字段会用双引号包围
     * - 字段内的双引号会被转义为两个双引号
     * - null值转换为空字符串
     * @example
     * 输入: 你好,世界 → 输出: "你好,世界"
     * 输入: 他说"你好" → 输出: "他说""你好"""
     * 输入: 正常字段 → 输出: 正常字段
     */
    public static String escapeCsv(String value) {
        if (value == null) return "";
        // 检查是否需要转义：包含逗号、引号或换行符
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * 格式化时间戳为标准日期时间字符串
     *
     * @param timestamp 数据库时间戳对象
     * @return String 格式化的日期时间字符串，格式：yyyy-MM-dd HH:mm:ss
     * @apiNote 将数据库Timestamp转换为易读的日期时间格式，用于CSV导出显示
     * @format 年-月-日 时:分:秒
     * @example
     * 输入: 2025-11-02 12:30:45.123 → 输出: "2025-11-02 12:30:45"
     * 输入: null → 输出: "" (空字符串)
     * @attention 毫秒部分会被忽略，只显示到秒级精度
     */
    public static String formatDate(Timestamp timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp.getTime()));
    }
}