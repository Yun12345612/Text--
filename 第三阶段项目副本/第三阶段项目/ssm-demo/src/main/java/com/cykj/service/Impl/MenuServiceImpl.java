package com.cykj.service.Impl;

import com.alibaba.fastjson.JSON;
import com.cykj.dto.ResponseDTO;
import com.cykj.mapper.AdminMapper;
import com.cykj.mapper.MenuMapper;
import com.cykj.pojo.CyMenu;
import com.cykj.pojo.CyRole;
import com.cykj.service.MenuService;
import com.cykj.utils.BuildMenuTreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public ResponseDTO getPermissionMenu(String adminId) {
        try {
            // 步骤1：校验管理员ID
            if (adminId == null || adminId.trim().isEmpty()) {
                return ResponseDTO.error(-1, "管理员ID不能为空");
            }
            Integer adminIdInt;
            try {
                adminIdInt = Integer.valueOf(adminId);
            } catch (NumberFormatException e) {
                return ResponseDTO.error(-1, "管理员ID必须是数字（如1、2）");
            }

            // 步骤2:调用AdminMapper查角色（而非MenuMapper），变量名用adminIdInt
            CyRole role = adminMapper.selectRoleByAdminId(adminIdInt);
            if (role == null) {
                return ResponseDTO.error(-2, "该管理员未分配角色，无访问权限");
            }

            // 步骤3：查角色对应的菜单（逻辑正确，无需修改）
            List<CyMenu> menuList = menuMapper.selectMenusByRoleId(role.getRoleId());
            if (menuList == null || menuList.isEmpty()) {
                return ResponseDTO.error(-3, "当前角色无可用菜单");
            }

            // 步骤4：组装树形菜单
            List<CyMenu> menuTree = BuildMenuTreeUtil.build(menuList, 0);
            System.out.println("菜单树结构: " + JSON.toJSONString(menuTree));

            return ResponseDTO.success(menuTree);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error(-99, "获取权限菜单失败，请联系管理员");
        }
    }

    // 工具方法
    private List<CyMenu> buildMenuTree(List<CyMenu> allMenus, int parentId) {
        if (allMenus == null) {
            return new ArrayList<>();
        }

        List<CyMenu> children = allMenus.stream()
                .filter(menu -> {
                    Integer menuParentId = menu.getMenuParentId();
                    return Objects.equals(menuParentId, parentId);
                })
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            for (CyMenu child : children) {
                child.setChildren(buildMenuTree(allMenus, child.getMenuId()));
            }
        }

        return children;
    }
}