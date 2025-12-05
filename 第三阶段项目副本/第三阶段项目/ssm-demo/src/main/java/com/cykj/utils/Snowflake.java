package com.cykj.utils;

import java.lang.management.ManagementFactory;
import java.util.concurrent.locks.ReentrantLock;
/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.utils
 * @Author: 夏日花店
 * @CreateTime: 2025-11-06 09:05:04
 * @Description: 雪花算法工具
 * @Version: 1.0
 */


public class Snowflake {

    public static final Snowflake INSTANCE = new Snowflake(getMachineId());

    // 自定义时间戳基准 2024 年 1 月 1 日）
    private static final long EPOCH = 1704067200000L;
    // 默认机器 ID
    private static final long DEFAULT_MACHINE_ID = 1L;

    // 时间戳位数
    private static final long TIMESTAMP_BITS = 41L;
    // 机器 ID 位数
    private static final long MACHINE_ID_BITS = 10L;
    // 递增序列号位数
    private static final long SEQUENCE_BITS = 12L;

    // 机器 ID 左移位数
    private static final long MACHINE_ID_LEFT_SHIFT = SEQUENCE_BITS;
    // 时间戳左移位数
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    // 最大时间戳
    private static final long MAX_TIMESTAMP = (1L << TIMESTAMP_BITS) - 1;
    // 最大机器 ID
    private static final long MAX_MACHINE_ID = (1 << MACHINE_ID_BITS) - 1;
    // 最大序列号
    private static final long MAX_SEQUENCE = (1 << SEQUENCE_BITS) - 1;

    private final long machineId; // 机器 ID
    private long lastTimestamp = -1L;  // 上次生成 ID 的时间戳
    private long sequence = 0L;   // 递增序列号

    private final ReentrantLock lock = new ReentrantLock();

    // 私有化构造方法
    private Snowflake(long machineId) {
        // 校验机器 ID
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException(
                    String.format("MachineId 必须在 0 - %d 之间", MAX_MACHINE_ID));
        }
        this.machineId = machineId;
    }

    /**
     * 生成唯一 ID
     */
    public long nextId() {
        lock.lock();
        try {
            long timestamp = currentTimeMillis();
            // 检查时间是否回拨
            if (timestamp < lastTimestamp) {
                throw new RuntimeException("系统时钟回退，拒绝生成 ID");
            }
            // 检查时间戳是否溢出
            long relativeTimestamp = timestamp - EPOCH;
            if (relativeTimestamp > MAX_TIMESTAMP) {
                throw new RuntimeException("时间戳超出可用范围");
            }
            // 同一毫秒内递增序列号
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                // 序列号溢出，等待下一毫秒
                if (sequence == 0) {
                    timestamp = waitForNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = timestamp;
            // 生成雪花算法 ID
            return  (relativeTimestamp << TIMESTAMP_LEFT_SHIFT)
                    | (machineId << MACHINE_ID_LEFT_SHIFT)
                    | sequence;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 等待下一毫秒
     */
    private long waitForNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 机器 ID
     * 使用进程 ID
     */
    private static long getMachineId() {
        try {
            // 格式：<pid>@<hostname>
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            // 保留后十位
            return Long.parseLong(pid) & MAX_MACHINE_ID;
        } catch (Exception e) {
            throw new RuntimeException("获取机器 ID 失败", e);
        }
    }
}
