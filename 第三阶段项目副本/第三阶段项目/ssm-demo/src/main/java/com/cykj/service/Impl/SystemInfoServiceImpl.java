package com.cykj.service.Impl;

import com.cykj.mapper.SystemInfoMapper;
import com.cykj.pojo.SystemInfo;
import com.cykj.service.SystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-18 11:13:50
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class SystemInfoServiceImpl implements SystemInfoService {
    // 注入Mapper接口
    private final SystemInfoMapper systemInfoMapper;

    /**
     * 构造方法注入
     * @param systemInfoMapper 系统信息Mapper接口
     */
    @Autowired // Spring自动注入Mapper实例
    public SystemInfoServiceImpl(SystemInfoMapper systemInfoMapper) {
        this.systemInfoMapper = systemInfoMapper;
    }

    /**
     * 实现获取系统信息的业务逻辑
     * @return 系统信息实体类
     */
    @Override
    public SystemInfo getSystemInfo() {
        return systemInfoMapper.getSystemInfo();
    }
}
