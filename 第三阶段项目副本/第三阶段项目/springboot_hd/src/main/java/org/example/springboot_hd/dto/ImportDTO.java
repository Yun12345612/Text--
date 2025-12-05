package org.example.springboot_hd.dto;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.dto
 * @Author: 夏日花店
 * @CreateTime: 2025-11-02 11:33:20
 * @Description: 头部注释
 * @Version: 1.0
 */

import lombok.Data;
import java.util.List;

public class ImportDTO {
    /**
     * 导入结果DTO
     */
    @Data
    public static class Result {
        private Integer success = 0;
        private Integer fail = 0;
        private List<Error> errorList;
    }

    /**
     * 导入错误信息DTO
     */
    @Data
    public static class Error {
        private Integer row;        // 行号
        private String userName;    // 姓名
        private String userPhone;   // 手机号
        private String reason;      // 错误原因

        public Error() {}

        public Error(Integer row, String userName, String userPhone, String reason) {
            this.row = row;
            this.userName = userName;
            this.userPhone = userPhone;
            this.reason = reason;
        }
    }
}

