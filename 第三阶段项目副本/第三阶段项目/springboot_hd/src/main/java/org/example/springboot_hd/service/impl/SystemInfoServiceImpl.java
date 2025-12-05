package org.example.springboot_hd.service.impl;

import org.example.springboot_hd.entity.SystemInfo;
import org.example.springboot_hd.mapper.SystemInfoMapper;
import org.example.springboot_hd.service.SystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-10-29 09:09:35
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class SystemInfoServiceImpl implements SystemInfoService {
    @Autowired
    private SystemInfoMapper systemInfoMapper;
    /**
     * 构造方法注入
     * @param systemInfoMapper 系统信息Mapper接口
     */
    @Autowired // Spring自动注入Mapper实例
    public SystemInfoServiceImpl(SystemInfoMapper systemInfoMapper) {
        this.systemInfoMapper = systemInfoMapper;
    }

    //1.获取系统信息
    @Override
    public SystemInfo getSystemInfo() {
        return systemInfoMapper.getSystemInfo();
    }


}
