package org.example.springboot_hd.dto;

import lombok.Data;
import org.example.springboot_hd.entity.Admin;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.dto
 * @Author: 夏日花店
 * @CreateTime: 2025-10-27 16:12:53
 * @Description: 头部注释
 * @Version: 1.0
 * 统一接口返回DTO（非泛型版本）
 * 用Object接收所有类型数据
 */
@Data
public class ResponseDTO {
    private int code;       // 状态码（200成功，500失败等）
    private String msg;     // 提示信息
    private Object data;    // 响应数据（Object类型，支持任意数据）
    private boolean success;

    // 构造方法
    public ResponseDTO(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = (code == 200);
    }

    // ========== 成功响应工具方法 ==========
    /**
     * 带数据和提示的成功响应
     */
    public static ResponseDTO success(Object data, String msg) {
        return new ResponseDTO(200, msg, data);
    }

    /**
     * 仅带数据的成功响应（默认提示“操作成功”）
     */
    public static ResponseDTO success(Object data) {
        return success(data, "操作成功");
    }

    /**
     * 无数据的成功响应（默认提示“操作成功”）
     */
    public static ResponseDTO success() {
        return success(null, "操作成功");
    }

    // ========== 失败响应工具方法 ==========
    /**
     * 带错误码和提示的失败响应
     */
    public static ResponseDTO error(int code, String msg) {
        return new ResponseDTO(code, msg, null);
    }

    /**
     * 仅带提示的失败响应（默认错误码500）
     */
    public static ResponseDTO error(String msg) {
        return error(500, msg);
    }

    /**
     * 无提示的失败响应（默认错误码500，提示“操作失败”）
     */
    public static ResponseDTO error() {
        return error(500, "操作失败");
    }
}