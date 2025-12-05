package org.example.springboot_hd.utils;

import io.micrometer.common.util.StringUtils;
import org.example.springboot_hd.entity.User;

import java.sql.Timestamp;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.utils
 * @Author: 夏日花店
 * @CreateTime: 2025-11-02 12:47:21
 * @Description: 用户导入导出工具类
 * @Version: 1.0
 */
public class UserImportExportUtils {
    /**
     * 从CSV字段创建用户对象
     */
    public static User createUserFromCSV(String[] fields) {
        User user = new User();

        // 必填字段
        user.setUserName(fields[0].trim());
        user.setUserPhone(fields[1].trim());
        user.setUserCard(fields[2].trim());
        user.setUserGender(fields[3].trim());

        // 可选字段
        if (fields.length > 4 && !fields[4].trim().isEmpty()) {
            try {
                user.setUserAge(Long.parseLong(fields[4].trim()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("年龄格式不正确");
            }
        }

        if (fields.length > 5) user.setUserEmail(fields[5].trim());
        if (fields.length > 6) user.setUserAccount(fields[6].trim());

        // 密码处理
        if (fields.length > 7 && !fields[7].trim().isEmpty()) {
            user.setUserPassword(fields[7].trim());
        } else {
            user.setUserPassword("123456");
        }

        // 余额
        if (fields.length > 8 && !fields[8].trim().isEmpty()) {
            try {
                user.setUserBalance(Double.parseDouble(fields[8].trim()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("余额格式不正确");
            }
        } else {
            user.setUserBalance(0.0);
        }

        // 状态
        if (fields.length > 9 && !fields[9].trim().isEmpty()) {
            try {
                user.setUserStatus(Long.parseLong(fields[9].trim()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("状态格式不正确");
            }
        } else {
            user.setUserStatus(1L);
        }

        // 设置创建时间和删除状态
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        user.setUserIsDelete("0");

        return user;
    }

    /**
     * 验证导入用户数据
     */
    public static void validateImportUser(User user) {
        // 数据验证
        if (!StringUtils.isNotBlank(user.getUserName())) {
            throw new RuntimeException("姓名不能为空");
        }
        if (!StringUtils.isNotBlank(user.getUserPhone())) {
            throw new RuntimeException("手机号不能为空");
        }
        if (!user.getUserPhone().matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        if (!StringUtils.isNotBlank(user.getUserCard())) {
            throw new RuntimeException("身份证号不能为空");
        }
        if (!user.getUserCard().matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)")) {
            throw new RuntimeException("身份证号格式不正确");
        }
        if (!"1".equals(user.getUserGender()) && !"2".equals(user.getUserGender())) {
            throw new RuntimeException("性别必须为1(男)或2(女)");
        }
        if (!StringUtils.isNotBlank(user.getUserAccount())) {
            throw new RuntimeException("账号不能为空");
        }
    }
}
