package com.cykj.service.Impl;

import com.cykj.dto.ResponseDTO;
import com.cykj.mapper.UserMapper;
import com.cykj.pojo.CyAdmin;
import com.cykj.pojo.User;
import com.cykj.service.UserService;
import com.cykj.utils.JwtUtils;
import com.cykj.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.service.Impl
 * @Author: 夏日花店
 * @CreateTime: 2025-11-03 13:32:10
 * @Description: 用户服务实现类
 * @Version: 1.0
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    //1.用户登录 - 支持账号/手机号/身份证号登录
    public ResponseDTO login(String loginInput, String pwd) {
        // 1. 根据账号/手机号/身份证号查询数据库中的用户
        User dbUser = userMapper.getUserByAccountOrPhoneOrCard(loginInput);
        if (dbUser == null) {
            return ResponseDTO.error(-1, "账号/手机号/身份证号不存在");
        }

        try {
            // 2. 获取数据库中的盐值和加密密码
            String dbSalt = dbUser.getSalt();
            String dbEncryptedPwd = dbUser.getUserPassword();
            boolean isLoginSuccess = false;

            // 3. 检查数据库中的密码是否为空,如果密码为空说明密码是是以明文或者无盐的MD5方式存储的
            if (dbSalt == null || dbSalt.trim().isEmpty()) {
                // 直接比较用户输入的密码与数据库存储的密码
                if (pwd.equals(dbEncryptedPwd)) {
                    isLoginSuccess = true;
                } else {
                    MessageDigest md = MessageDigest.getInstance("MD5");//创建MD5加密实例，对用户输入的密码进行MD5加密
                    byte[] bytes = md.digest(pwd.getBytes());//将密码字符串转换为字节数组并进行MD5加密
                    //将MD5加密后的字节数组转换为十六进制字符串
                    StringBuilder sb = new StringBuilder();
                    for (byte b : bytes) {
                        String hex = Integer.toHexString(0xff & b);
                        if (hex.length() == 1) sb.append('0');
                        sb.append(hex);//生成标准的32位MD5哈希字符串
                    }
                    String inputEncryptedPwd = sb.toString();
                    isLoginSuccess = inputEncryptedPwd.equals(dbEncryptedPwd);
                }
            } else {
                // 有盐值：盐值加密比对
                String inputEncryptedPwd = MD5Utils.encrypt(pwd, dbSalt);
                isLoginSuccess = inputEncryptedPwd.equals(dbEncryptedPwd);
            }

            // 4. 登录成功处理
            if (isLoginSuccess) {
                // 清除敏感信息
                dbUser.setUserPassword(null);
                dbUser.setSalt(null);

                if (dbUser.getUserId() == null) {
                    return ResponseDTO.error(-3, "用户信息不完整");
                }

                // 生成token
                HashMap<String, Object> map = new HashMap<>();
                map.put("userId", dbUser.getUserId());
                String token = JwtUtils.generateJwt(map);

                HashMap<String, Object> result = new HashMap<>();
                result.put("userInfo", dbUser);
                result.put("token", token);
                return ResponseDTO.success(result);
            } else {
                return ResponseDTO.error(-1, "密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error(-2, "系统异常：" + e.getMessage());
        }
    }

    //2.用户注册
    @Override
    public ResponseDTO register(User user) {
        try {
            // 1. 验证账号是否已存在
            User accountUser = userMapper.getUserByAccount(user.getUserAccount());
            if (accountUser != null) {
                return ResponseDTO.error(-1, "账号已存在");
            }

            // 2. 验证手机号是否已存在
            User phoneUser = userMapper.getUserByPhone(user.getUserPhone());
            if (phoneUser != null) {
                return ResponseDTO.error(-1, "手机号已存在");
            }

            // 3. 验证身份证号是否已存在
            User cardUser = userMapper.getUserByCard(user.getUserCard());
            if (cardUser != null) {
                return ResponseDTO.error(-1, "身份证号已存在");
            }

            // 创建新的User实例（使用新的变量名）
            User newUser = new User();

            // 4. 给新用户赋值（设置所有前端传来的字段）
            newUser.setUserAccount(user.getUserAccount()); // 账号
            newUser.setUserName(user.getUserName());       // 用户名
            newUser.setUserPhone(user.getUserPhone());     // 手机号
            newUser.setUserCard(user.getUserCard());       // 身份证号
            newUser.setUserGender(user.getUserGender());   // 性别
            newUser.setUserAge(user.getUserAge());         // 年龄
            newUser.setUserEmail(user.getUserEmail());     // 邮箱

            // 5. 生成盐值并加密密码
            String salt = MD5Utils.generateSalt();
            String encryptedPwd = MD5Utils.encrypt(user.getUserPassword(), salt);
            newUser.setUserPassword(encryptedPwd); // 加密后的密码
            newUser.setSalt(salt);                 // 盐值

            // 6. 保存到数据库
            int insertCount = userMapper.insertUser(newUser);

            if (insertCount > 0) {
                newUser.setUserPassword(null); // 清除敏感信息
                newUser.setSalt(null);
                return ResponseDTO.success("注册成功", newUser);
            } else {
                return ResponseDTO.error(-2, "注册失败，请重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error(-99, "系统异常：" + e.getMessage());
        }
    }
}