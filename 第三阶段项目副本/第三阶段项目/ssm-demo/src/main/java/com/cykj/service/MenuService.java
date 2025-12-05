package com.cykj.service;

import com.cykj.dto.ResponseDTO;

public interface MenuService {
    // 3. 新增：根据管理员ID获取权限菜单树
    ResponseDTO getPermissionMenu(String adminId);
}
