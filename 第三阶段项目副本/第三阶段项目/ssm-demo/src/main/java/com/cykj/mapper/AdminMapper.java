package com.cykj.mapper;

import com.cykj.pojo.CyAdmin;
import com.cykj.pojo.CyRole;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;


public interface AdminMapper {



    //1.登录加密
    CyAdmin getAdminByAccount(String acc);

    //2.批量删除
    int updateAdminStatus(@Param("ids") int[] ids);

    //3.根据管理员ID查询角色
    CyRole selectRoleByAdminId(Integer adminIdInt);

    //4.新增管理员(注册)
    int insertAdmin(CyAdmin admin);
}
