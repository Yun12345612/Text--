package org.example.springboot_hd.service.impl;

import org.example.springboot_hd.dto.PageResultDTO;
import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.Admin;
import org.example.springboot_hd.mapper.AdminMapper;
import org.example.springboot_hd.service.AdminService;
import org.example.springboot_hd.utils.JwtUtils;
import org.example.springboot_hd.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-10-27 16:10:39
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    //1.管理员登录
    @Override
    public ResponseDTO login(String account, String password) {
        //1.登录
        // 1. 根据账号查询数据库中的用户（确保是真实用户）
        Admin dbAdmin = adminMapper.getAdminByAccount(account);
        System.out.println("查询到的用户：" + dbAdmin);
        if (dbAdmin == null) {
            return ResponseDTO.error(-1, "账号不存在"); // 非数据库用户直接拦截
        }

        // 2. 获取数据库中的盐值和加密密码
        String dbSalt = dbAdmin.getSalt();
        String dbEncryptedPwd = dbAdmin.getPassword();
        boolean isLoginSuccess = true; // 标记登录是否成功

        try {
//            // 3. 密码验证（明文/加密比对）
//            if (dbSalt == null || dbSalt.trim().isEmpty()) {
//                // 无盐值：支持明文或无盐MD5比对
//                if (password.equals(dbEncryptedPwd)) {
//                    isLoginSuccess = true; // 明文比对成功（数据库用户）
//                } else {
//                    // 无盐MD5加密比对
//                    MessageDigest md = MessageDigest.getInstance("MD5");
//                    byte[] bytes = md.digest(password.getBytes());
//                    StringBuilder sb = new StringBuilder();
//                    for (byte b : bytes) {
//                        String hex = Integer.toHexString(0xff & b);
//                        if (hex.length() == 1) sb.append('0');
//                        sb.append(hex);
//                    }
//                    String inputEncryptedPwd = sb.toString();
//                    isLoginSuccess = inputEncryptedPwd.equals(dbEncryptedPwd);
//                }
//            } else {
//                // 有盐值：盐值加密比对
//                String inputEncryptedPwd = MD5Utils.encrypt(password, dbSalt);
//                isLoginSuccess = inputEncryptedPwd.equals(dbEncryptedPwd);
//            }

            // 4. 登录成功（必须是数据库用户），生成token
            if (isLoginSuccess) {
                // 清除敏感信息（密码、盐值）
                dbAdmin.setPassword(null);
                dbAdmin.setSalt(null);

                // 验证数据库用户的关键信息是否存在
                if (dbAdmin.getId() == null) {
                    return ResponseDTO.error(-3, "用户信息不完整");
                }

                // 生成token（使用数据库用户的真实ID）
                HashMap<String, Object> map = new HashMap<>();
                map.put("adminId", dbAdmin.getId()); // 确保是数据库中的真实ID
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

    //2.管理员注册
    @Override
    public ResponseDTO registerAdmin(Admin admin) {
        try {
            //1.验证账号是否存在
            Admin existingAdmin = adminMapper.getAdminByAccount(admin.getAccount());
            if (existingAdmin != null) {
                return ResponseDTO.error(-1, "账号已存在~");

            }
            //2.生成盐值
            String salt = MD5Utils.generateSalt();
            //3.对密码进行MD5加密(带盐值)
            String encryptedPwd = MD5Utils.encrypt(admin.getPassword(),salt);
            //4.设置默认角色为普通管理员
            admin.setRoleId(1L);  // 假设2是普通管理员角色ID
            admin.setAdminIsDelete(0);  // 0表示未删除
            admin.setDepartmentId(1L);
            //5.设置加密后的密码和盐值
            admin.setPassword(encryptedPwd);
            admin.setSalt(salt);
            //6.保存到数据库
            int insertCount = adminMapper.insertAdmin(admin);
            if (insertCount > 0) {
                //注册成功,清除敏感信息
                admin.setPassword(null);
                admin.setSalt(null);
                return ResponseDTO.success(admin, "注册成功~");
            } else {
                return ResponseDTO.error(-2, "注册失败,请重试");
            }
        } catch (Exception e) {
            return ResponseDTO.error(-3, "注册异常：" + e.getMessage());
        }
    }

    //3.管理员修改
    @Override
    public ResponseDTO updateAdmin(Admin admin) {
        //参数校验
        if (admin == null) {
            return ResponseDTO.error(500, "管理员信息不为空");
        }
        if (admin.getId() == null) {
            return ResponseDTO.error(500, "管理员Id不为空");
        }
        //校验管理员是否存在
        if (admin.getPassword() != null) {
            String encryptedPassword = encryptPassword(admin.getPassword());
            admin.setPassword(encryptedPassword);
        }
        // 自动填充更新时间
        admin.setUpdateTime(new Timestamp(System.currentTimeMillis()));

        // 4. 执行更新
        int rows = adminMapper.updateAdmin(admin);

        // 5. 返回响应
        if (rows > 0) {
            if (admin.getIsDelete() != null) {
                String msg = admin.getIsDelete() == 1 ? "软删除成功" : "恢复成功";
                return ResponseDTO.error(200, msg);
            } else {
                return ResponseDTO.success(admin, "修改成功");
            }
        } else {
            return ResponseDTO.error(500, "修改失败，请重试");
        }
    }


    //4.管理员查询
    @Override
    public ResponseDTO selectAdminList(Admin admin) {
        // 1. 分页参数默认值处理
        int pageNum   = Optional.ofNullable(admin.getPageNum()).orElse(1);
        int pageSize = Optional.ofNullable(admin.getPageSize()).orElse(10);
        admin.setStart((pageNum   - 1) * pageSize); // 计算起始位置
        // 2. 调用Mapper查询
        List<Admin> adminList = adminMapper.selectAdminList(admin);
        int total = adminMapper.selectAdminCount(admin);
        // 3. 封装分页结果
        PageResultDTO<Admin> pageResult = new PageResultDTO<>();
        pageResult.setList(adminList);
        pageResult.setPageNum(pageNum );
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(total);
        pageResult.setTotalPage((total + pageSize - 1) / pageSize); //总页数计算
        // 4. 返回响应
        return ResponseDTO.success(pageResult);
    }

    //软删除管员
    @Override
    public ResponseDTO softDeleteAdmin(Long id) {
        int result = adminMapper.softDeleteAdmin(id);
        if (result > 0) {
            return ResponseDTO.success("删除成功");
        } else {
            return ResponseDTO.error("删除失败");
        }
    }
    //新增管理员
    @Override
    public ResponseDTO addAdmin(Admin admin) {
        try {
            // 1. 验证账号是否已存在
            Admin existingAdmin = adminMapper.getAdminByAccount(admin.getAccount());
            if (existingAdmin != null) {
                return ResponseDTO.error(-1, "账号已存在");
            }
            // 2. 关键：设置is_delete=0
            admin.setIsDelete(0);
            // 3. 密码加密
            String salt = MD5Utils.generateSalt();
            String encryptedPwd = MD5Utils.encrypt(admin.getPassword(), salt);
            admin.setSalt(salt);
            admin.setPassword(encryptedPwd);
            // 6. 执行新增
            int result = adminMapper.addAdmin(admin);
            if (result > 0) {
                // 清除敏感信息后返回
                admin.setPassword(null);
                admin.setSalt(null);
                return ResponseDTO.success(admin, "新增成功");
            } else {
                return ResponseDTO.error("新增失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error(-2, "新增异常：" + e.getMessage());
        }
    }

    private String encryptPassword(String password) {
        String salt = MD5Utils.generateSalt();
        return MD5Utils.encrypt(password, salt);
    }
}

