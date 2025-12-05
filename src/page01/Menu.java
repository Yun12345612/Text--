package page01;

import page02.User;
import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import static page02.Common.sc;
import static page03.GameController.*;
/**
 * @BelongsProject: Priject_0729
 * @BelongsPackage: page05
 * @Author: 阿枫
 * @CreateTime: 2025-07-29 14:40
 * @Description: 菜单栏类
 * @Version: 1.0
 */
/**
 * 游戏菜单类 - 负责游戏模式选择和界面展示
 * 相当于游戏的"总控制台"，管理所有游戏模式和功能
 */
public class Menu {
    // 全局变量定义区域

    // 输入扫描器，用于读取玩家输入
    public static final Scanner SC = new Scanner(System.in);
    // 是否计时模式标志（true表示正在玩计时模式）
    public static boolean isTimedMode = false;
    // 是否困难模式标志
    public static boolean isHardMode = false;
    // 游戏开始时间（用于计时模式计算剩余时间）
    public static long startTime = 0;
    // 玩家移动次数统计（比如按了几次方向键）
    public static int moveCount = 0;
    // 标记障碍物是否已生成（防止重复生成）
    private static boolean obstacleGenerated = false;
    // 标记随机事件是否已触发
    private static boolean eventTriggered = false;
    // 游戏锁对象（用于多线程同步，防止多个线程同时修改游戏数据）
    private static final Object gameLock = new Object();
    // 障碍物生成线程（负责定时生成障碍物）
    private static Thread obstacleThread;
    // 随机事件线程（负责定时触发随机事件）
    private static Thread eventThread;
    // 游戏是否结束标志（true表示游戏已结束）
    private static boolean gameOver = false;


    /**
     * 主菜单方法 - 游戏启动后看到的第一个界面
     * @throws Exception 可能抛出的异常
     */
    public static void menu() throws Exception {
        boolean running = true;  // 控制主循环是否继续运行

        // 主菜单循环（一直显示菜单直到玩家选择退出）
        while (running) {
            // 打印菜单选项（像饭店的点菜单一样）
            System.out.println("\n========== 主菜单 ==========");
            System.out.println("1. 进入游戏");    // 就像"开始游戏"按钮
            System.out.println("2. 查看排行");    // 查看谁玩得最好
            System.out.println("3. 游戏介绍");    // 游戏玩法说明
            System.out.println("4. 退出游戏");    // 关闭游戏
            System.out.print("请选择:");         // 等待玩家输入

            // 读取玩家输入的数字（1-4）
            int choice = sc.nextInt();
            // 清除输入缓冲区（防止上次输入影响下次读取）
            SC.nextLine();

            // 根据玩家选择执行对应功能（像电梯按钮一样）
            switch (choice) {
                case 1:
                    gameModeMenu();  // 进入二级菜单选择游戏模式
                    break;
                case 2:
                    showRanking();    // 显示排行榜（像成绩单）
                    break;
                case 3:
                    showGameIntro();  // 显示游戏说明书
                    break;
                case 4:
                    exitGame();       // 退出游戏
                    running = false;  // 结束主循环
                    break;
                default:  // 如果输入的不是1-4
                    System.out.println("输入无效请重新输入!!!");  // 提示错误
            }
        }
    }

    /**
     * 游戏模式选择菜单 - 选择要玩哪种模式
     * @throws Exception 可能抛出的异常
     */
    private static void gameModeMenu() throws Exception {
        // 打印游戏模式选项（像选择游戏难度）
        System.out.println("\n========== 游戏模式 ==========");
        System.out.println("1. 单人模式");      // 自己一个人玩
//        System.out.println("2. 玩家对战");      // 双人对战（暂未实现）
        System.out.println("3. 五×五模式");     // 更大的棋盘（暂未实现）
        System.out.println("4. 计时模式");      // 限时挑战
        System.out.println("5. 困难模式");      // 有障碍物的困难模式
        System.out.print("请选择:");

        // 读取玩家选择的模式编号
        int modeChoice = SC.nextInt();
        SC.nextLine();  // 清除输入缓冲区

        // 重置游戏模式状态（相当于把游戏机恢复到初始设置）
        isTimedMode = false;   // 关闭计时模式
        isHardMode = false;    // 关闭困难模式
        moveCount = 0;         // 移动次数归零
        gameOver = false;      // 游戏未结束

        // 根据选择启动对应游戏模式（像选择不同的游戏模式）
        switch (modeChoice) {
            case 1:
                soloGame();          // 调用普通模式单人模式
                break;
            case 2:
                pkGame();            // 调用玩家对战暂未实现
                break;
            case 3:
                startLargeBoardGame(); // 调用大棋盘模式//暂未实现
                break;
            case 4:
                startTimedMode();    // 调用计时模式
                break;
            case 5:
                startHardMode();     // 调用困难模式
                break;
            default:
                System.out.println("无效的游戏模式选择!");  // 卡带插错了
        }
    }

    /**
     * 单人模式启动方法 - 最基础的玩法
     * @throws Exception 可能抛出的异常
     */
    private static void soloGame() throws Exception {
        System.out.println("\n=== 单人模式已启动! ===");  // 游戏机启动音效
        initializeAndStartGame();  // 开始游戏流程
    }

    /**
     * 玩家对战模式启动方法
     * @throws Exception 可能抛出的异常
     */
    private static void pkGame() throws Exception {
        System.out.println("\n=== 玩家对战模式已启动! ===");
        // 这里可以添加对战模式的具体实现（目前和单人模式一样）
        initializeAndStartGame();
    }

    /**
     * 五×五模式启动方法 - 更大的游戏棋盘
     * @throws Exception 可能抛出的异常
     */
    private static void startLargeBoardGame() throws Exception {
        System.out.println("\n=== 五×五模式已启动! ===");
        int mapSize = 5;  // 设置地图大小为5×5（原版是4×4）
        map = new int[mapSize][mapSize];  // 创建更大的游戏棋盘
        initializeAndStartGame();  // 开始游戏
    }

    /**
     * 计时模式启动方法 - 限时挑战
     * @throws Exception 可能抛出的异常
     */
    private static void startTimedMode() throws Exception {
        isTimedMode = true;  // 开启计时模式标志
        startTime = System.currentTimeMillis();  // 记录开始时间（按下秒表）
        final int timeLimit = 10;  // 设置10秒倒计时

        System.out.println("\n=== 计时模式已启动! ===");
        System.out.println("您有10秒的时间完成游戏!");  // 显示挑战规则

        // 创建并启动倒计时线程（相当于一个独立运行的秒表）
        Thread timerThread = new Thread(() -> {
            try {
                int remainingTime = timeLimit;  // 剩余时间从10开始

                // 倒计时循环（每秒更新一次显示）
                while (remainingTime > 0 && !Thread.currentThread().isInterrupted()) {
                    // \r让光标回到行首，实现原地更新效果
                    System.out.printf("\r⏰ 剩余时间: %d秒 ", remainingTime);
                    Thread.sleep(1000);  // 暂停1秒
                    remainingTime--;     // 时间减少1秒
                }

                // 时间到处理
                if (remainingTime <= 0) {
                    System.out.println("\n⌛ 时间到！游戏结束！");
                    gameOver = true;  // 设置游戏结束标志
                    System.exit(0);   // 直接退出游戏
                }
            } catch (InterruptedException e) {
                // 如果线程被中断（比如游戏提前结束）
                Thread.currentThread().interrupt();
            }
        });

        timerThread.setDaemon(true);  // 设置为守护线程（主线程结束它自动结束）
        timerThread.start();          // 启动倒计时线程（按下秒表开始按钮）

        initializeAndStartGame();     // 开始游戏
        timerThread.interrupt();      // 游戏正常结束时停止计时线程
    }

    /**
     * 困难模式启动方法 - 有定时障碍物和随机事件
     * @throws Exception 可能抛出的异常
     */
    private static void startHardMode() throws Exception {
        isHardMode = true;  // 开启困难模式标志
        System.out.println("\n=== 困难模式已启动! ===");
        // 警告提示（像过山车前的警示牌）
        System.out.println("⚠️ 警告：本模式每8秒会出现障碍物(X)，每10秒会出现随机事件！");

        // 停止之前的线程（如果存在）（相当于重启游戏机）
        stopHardModeThreads();
        gameOver = false;  // 重置游戏状态

        // 创建障碍物生成线程（每8秒工作一次）
        obstacleThread = new Thread(() -> {
            try {
                while (!gameOver) {  // 只要游戏没结束就一直循环
                    Thread.sleep(8000); // 暂停8秒
                    if (!gameOver) {   // 再次检查游戏是否结束
                        generateRandomObstacle();  // 生成障碍物
                    }
                }
            } catch (InterruptedException e) {
                // 如果线程被中断（比如游戏结束）
                Thread.currentThread().interrupt();
            }
        });

        // 创建随机事件线程（每10秒工作一次）
        eventThread = new Thread(() -> {
            try {
                while (!gameOver) {
                    Thread.sleep(10000);  // 暂停10秒
                    if (!gameOver) {
                        triggerRandomEvent();  // 触发随机事件
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 设置线程为守护线程（主线程结束它们自动结束）
        obstacleThread.setDaemon(true);
        eventThread.setDaemon(true);

        // 启动两个线程（相当于打开两个定时器）
        obstacleThread.start();
        eventThread.start();

        // 开始游戏主流程
        initializeAndStartGame();
        // 游戏结束后停止两个线程
        stopHardModeThreads();
    }

    /**
     * 生成随机障碍物 - 在空格子上放一个"X"
     */
    private static void generateRandomObstacle() {
        // 加锁防止多线程同时修改游戏地图（像厕所门锁，一次只进一个人）
        synchronized (gameLock) {
            // 安全检查：如果游戏已结束或地图不存在，直接返回
            if (gameOver || map == null) return;

            // 收集所有空格子（值为0的位置）
            List<int[]> emptyCells = new ArrayList<>();
            // 遍历整个地图（从左到右，从上到下）
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] == 0) {  // 如果是空格子
                        emptyCells.add(new int[]{i, j});  // 记录坐标
                    }
                }
            }

            // 如果有空格子
            if (!emptyCells.isEmpty()) {
                // 随机选择一个空格子（像抽奖一样）
                int[] pos = emptyCells.get((int)(Math.random() * emptyCells.size()));
                // 再次检查坐标是否合法（防止意外错误）
                if (pos[0] >= 0 && pos[0] < map.length &&
                        pos[1] >= 0 && pos[1] < map[0].length) {
                    map[pos[0]][pos[1]] = -1; // -1表示障碍物（X）
                    // 打印提示信息（像游戏中的系统消息）
                    System.out.println("\n💥 障碍物X出现在 (" + (pos[0]+1) + "," + (pos[1]+1) + ")");
                    PrintMap();  // 重新打印地图
                }
            }
        }
    }

    /**
     * 触发随机事件 - 在空格子上生成特殊数字
     */
    private static void triggerRandomEvent() {
        synchronized (gameLock) {
            if (gameOver || map == null) return;

            List<int[]> emptyCells = new ArrayList<>();
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] == 0) {
                        emptyCells.add(new int[]{i, j});
                    }
                }
            }

            if (!emptyCells.isEmpty()) {
                int[] pos = emptyCells.get((int)(Math.random() * emptyCells.size()));
                if (pos[0] >= 0 && pos[0] < map.length &&
                        pos[1] >= 0 && pos[1] < map[0].length) {
                    // 70%概率生成8，30%概率生成16（像抽奖概率）
                    int number = Math.random() < 0.7 ? 8 : 16;
                    map[pos[0]][pos[1]] = number;  // 放置数字
                    System.out.println("\n✨ 随机事件: 数字" + number + "出现在 (" +
                            (pos[0]+1) + "," + (pos[1]+1) + ")");
                    PrintMap();  // 更新地图显示
                }
            }
        }
    }

    /**
     * 停止困难模式线程 - 相当于关闭两个定时器
     */
    private static void stopHardModeThreads() {
        // 如果障碍物线程存在且还在运行
        if (obstacleThread != null && obstacleThread.isAlive()) {
            obstacleThread.interrupt();  // 发送中断信号
        }
        // 如果事件线程存在且还在运行
        if (eventThread != null && eventThread.isAlive()) {
            eventThread.interrupt();
        }
    }

    /**
     * 初始化并开始游戏 - 游戏主流程
     * @throws Exception 可能抛出的异常
     */
    private static void initializeAndStartGame() throws Exception {
        InitialMap();       // 初始化地图（清空棋盘）
        PrintMap();         // 打印初始地图
        GameLoop();         // 进入游戏主循环（等待玩家操作）
        isGameOver();       // 检查游戏是否结束
        // 重置标记
        obstacleGenerated = false;
        eventTriggered = false;
    }

    /**
     * 显示排行榜 - 展示玩家成绩
     */
    public static void showRanking() {
        // 如果当前有用户登录，更新排行榜数据
        if (User.currentUser != null) {
            updateLeaderboard();
        }

        // 打印排行榜标题（像成绩榜单）
        System.out.println("\n========== 实时排行榜 ==========");
        System.out.println("排名  | 玩家账号    | 当前分数    | 历史最佳 | 最后更新时间 |");

        // SQL查询语句（从数据库获取排行榜数据）
        String query = "SELECT account, score, high_score, achieved " +
                "FROM leaderboard " +
                "ORDER BY high_score DESC, score DESC, achieved DESC " +
                "LIMIT 10";  // 只取前10名

        // 使用try-with-resources自动关闭数据库连接
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int rank = 1;  // 排名从1开始
            // 获取当前登录用户账号（用于标记当前玩家）
            String currentAccount = User.currentUser != null ?
                    User.currentUser.getAccount() : null;

            // 遍历查询结果（每条记录就是一个玩家数据）
            while (rs.next()) {
                String account = rs.getString("account");
                // 检查是否是当前玩家（给自己的名字加星星）
                boolean isCurrent = account.equals(currentAccount);

                // 格式化输出一行排行榜数据
                System.out.printf("| %s%d | %-8s | %8d | %8d | %tF %<tT |\n",
                        isCurrent ? "★" : "",  // 当前玩家前面加★
                        rank++,               // 排名数字递增
                        account,              // 玩家账号
                        rs.getInt("score"),    // 当前分数
                        rs.getInt("high_score"), // 历史最高分
                        rs.getTimestamp("achieved")); // 最后游戏时间
            }

            // 显示当前玩家状态（如果已登录）
            if (User.currentUser != null) {
                System.out.println("\n当前玩家：" + User.currentUser.getAccount() +
                        " | 当前分数：" + score +
                        " | 历史最佳：" + maxScore);
            }
        } catch (SQLException e) {
            System.err.println("查询排行榜失败: " + e.getMessage());
        }
    }

    /**
     * 显示游戏介绍 - 游戏说明书
     */
    static void showGameIntro() {
        System.out.println("\n========== 游戏介绍 ==========");
        System.out.println("2048是一款休闲益智小游戏");
        System.out.println("通过移动方块使相同数字合并，最终获得2048方块!");
        System.out.println("使用方向键或WASD控制方块移动");
        System.out.println("=============================");
    }

    /**
     * 退出游戏方法 - 关闭游戏
     */
    static void exitGame() {
        stopHardModeThreads();  // 停止所有后台线程
        System.out.println("\n退出成功，再见！\uD83D\uDC4B");  // 挥手表情
    }
}