package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springboot_hd.entity.SystemInfo;

@Mapper
public interface SystemInfoMapper {
    //1.获取系统信息
    SystemInfo getSystemInfo();
}
