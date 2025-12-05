package com.cykj.pojo;

import lombok.Data;

import java.util.List;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-14 11:12:56
 * @Description: 头部注释
 * @Version: 1.0
 */
@Data
public class CyAdmin {
    private String adminId;
    private String adminName;
    private String adminAccount;
    private String adminPwd;
    private String salt;
    private Integer adminRoleId;
    private CyRole role;
    private String code;
    private Integer adminIsDelete; // 添加这个字段
    // 管理员可访问的菜单（最终返回给前端）
    private List<CyMenu> accessibleMenus;


    public void setAdminIsDelete(int i) {
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
