package com.cykj.service;

import com.cykj.dto.ResponseDTO;
import com.cykj.pojo.CyAdmin;

public interface AdminService {
    //1.查询
    ResponseDTO login(String acc, String pwd);
    //2.批量删除
    ResponseDTO updateAdminStatus(int[] ids);
    //3.注册新管理员
    ResponseDTO register(CyAdmin admin);



}
