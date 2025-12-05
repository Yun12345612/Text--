package com.cykj.service.Impl;

import com.cykj.dto.ResponseDTO;
import com.cykj.mapper.AdminMapper;
import com.cykj.mapper.MenuMapper;
import com.cykj.pojo.CyAdmin;
import com.cykj.pojo.CyMenu;
import com.cykj.pojo.CyRole;
import com.cykj.service.AdminService;
import com.cykj.utils.JwtUtils;
import com.cykj.utils.MD5Utils;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Objects;

import java.security.MessageDigest;
import java.util.List;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-14 11:16:30
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;


    //1.登录
    public ResponseDTO login(String acc, String pwd) {
        // 1. 根据账号查询数据库中的用户（确保是真实用户）
        CyAdmin dbAdmin = adminMapper.getAdminByAccount(acc);
        if (dbAdmin == null) {
            return ResponseDTO.error(-1, "账号不存在"); // 非数据库用户直接拦截
        }

        // 2. 获取数据库中的盐值和加密密码
        String dbSalt = dbAdmin.getSalt();
        String dbEncryptedPwd = dbAdmin.getAdminPwd();
        boolean isLoginSuccess = false; // 标记登录是否成功

        try {
            // 3. 密码验证（明文/加密比对）
            if (dbSalt == null || dbSalt.trim().isEmpty()) {
                // 无盐值：支持明文或无盐MD5比对
                if (pwd.equals(dbEncryptedPwd)) {
                    isLoginSuccess = true; // 明文比对成功（数据库用户）
                } else {
                    // 无盐MD5加密比对
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] bytes = md.digest(pwd.getBytes());
                    StringBuilder sb = new StringBuilder();
                    for (byte b : bytes) {
                        String hex = Integer.toHexString(0xff & b);
                        if (hex.length() == 1) sb.append('0');
                        sb.append(hex);
                    }
                    String inputEncryptedPwd = sb.toString();
                    isLoginSuccess = inputEncryptedPwd.equals(dbEncryptedPwd);
                }
            } else {
                // 有盐值：盐值加密比对
                String inputEncryptedPwd = MD5Utils.encrypt(pwd, dbSalt);
                isLoginSuccess = inputEncryptedPwd.equals(dbEncryptedPwd);
            }

            // 4. 登录成功（必须是数据库用户），生成token
            if (isLoginSuccess) {
                // 清除敏感信息（密码、盐值）
                dbAdmin.setAdminPwd(null);
                dbAdmin.setSalt(null);

                // 验证数据库用户的关键信息是否存在
                if (dbAdmin.getAdminId() == null) {
                    return ResponseDTO.error(-3, "用户信息不完整");
                }

                // 生成token（使用数据库用户的真实ID）
                HashMap<String, Object> map = new HashMap<>();
                map.put("adminId", dbAdmin.getAdminId()); // 确保是数据库中的真实ID
                String token = JwtUtils.generateJwt(map);
                System.err.println("生成的token：" + token);

                //同时包含用户信息和token（证明是数据库用户）
                HashMap<String, Object> result = new HashMap<>();
                result.put("adminInfo", dbAdmin); // 数据库用户信息
                result.put("token", token); // 关联该用户的token
                return ResponseDTO.success(result);
            } else {
                return ResponseDTO.error(-1, "密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error(-2, "系统异常：" + e.getMessage());
        }
    }
    //2.管理员状态
    public ResponseDTO updateAdminStatus(int[] ids) {
        int res = adminMapper.updateAdminStatus(ids);
        return ResponseDTO.success(res);
    }


    //3.注册新管理员
    @Override
    public ResponseDTO register(CyAdmin admin) {
        try {
            // 1. 验证账号是否已存在
            CyAdmin existingAdmin = adminMapper.getAdminByAccount(admin.getAdminAccount());
            if (existingAdmin != null) {
                return ResponseDTO.error(-1, "账号已存在");
            }

            // 2. 生成盐值
            String salt = MD5Utils.generateSalt();

            // 3. 对密码进行MD5加密(带盐值)
            String encryptedPwd = MD5Utils.encrypt(admin.getAdminPwd(), salt);

            // 4. 设置默认角色为普通管理员
            admin.setAdminRoleId(2);  // 假设2是普通管理员角色ID
            admin.setAdminIsDelete(0);  // 0表示未删除

            // 5. 设置加密后的密码和盐值
            admin.setAdminPwd(encryptedPwd);
            admin.setSalt(salt);
            admin.setAdminName(admin.getAdminAccount());

            // 6. 保存到数据库
            int insertCount = adminMapper.insertAdmin(admin);

            if (insertCount > 0) {
                // 注册成功，清除敏感信息
                admin.setAdminPwd(null);
                admin.setSalt(null);
                return ResponseDTO.success("注册成功");
            } else {
                return ResponseDTO.error(-2, "注册失败，请重试");
            }
        } catch (Exception e) {
            return ResponseDTO.error(-3, "注册异常：" + e.getMessage());
        }
    }
}



