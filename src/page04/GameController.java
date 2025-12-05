package page04;
import page01.GameStatus;
import page01.GameZtai;
import page01.Menu;
import page02.Common;
import page02.User;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class GameController {
    // 数据库连接信息
    public static final String DB_URL = "jdbc:mysql://localhost:3306/game2048";
    public static final String USER = "root";
    public static final String PASS = "123456";
    //定义所需要的变量
    public static int score = 0;
    public static int maxScore = 0;
    static Random r = new Random();
    static int mapSize = 4;
    public static int[][] map = new int[mapSize][mapSize];
    private static Stack<GameStatus> undoStack = new Stack<>();

    // 保存当前状态到撤销栈（保持原名）
    private static void saveStateToUndoStack() {
        // 直接保存状态，不做变化检查（简化逻辑）
        GameStatus currentState = new GameStatus();

        // 深拷贝地图
        int[][] copyMap = new int[mapSize][mapSize];
        for (int i = 0; i < mapSize; i++) {
            System.arraycopy(map[i], 0, copyMap[i], 0, mapSize);
        }
        currentState.setMap(copyMap);

        // 保存分数
        currentState.setScore(score);
        currentState.setMaxScore(maxScore);

        // 入栈
        undoStack.push(currentState);

        // 可选：限制最大撤销步数（如10步）
        while (undoStack.size() > 10) {
            undoStack.remove(0); // 移除最旧的状态
        }
    }

    // 撤销方法
    private static boolean undoLastMove() {
        // 需要至少2个状态才能撤销（初始状态+当前状态）
        if (undoStack.size() < 2) {
            System.out.println("❌ 没有可撤销的操作！（当前保存步数：" + undoStack.size() + ")");
            return false;
        }

        // 弹出当前状态
        undoStack.pop();

        // 获取并恢复上一个状态
        GameStatus prevState = undoStack.peek();
        restoreGameState(prevState);

        System.out.println("✅ 已撤销上一步操作");
        return true;
    }

    // 恢复游戏状态
    private static void restoreGameState(GameStatus state) {
        // 恢复地图
        int[][] prevMap = state.getMap();
        for (int i = 0; i < mapSize; i++) {
            System.arraycopy(prevMap[i], 0, map[i], 0, mapSize);
        }

        // 恢复分数
        score = state.getScore();
        maxScore = state.getMaxScore();
    }

    // 排行榜更新方法 - 确保每次分数变化都更新
    public static void updateLeaderboard() {
        if (User.currentUser == null) {
            System.out.println("未登录用户分数不记录到排行榜");
            return;
        }

        // 确保maxScore是最新值
        maxScore = Math.max(score, maxScore);

        // 使用ON DUPLICATE KEY UPDATE语法
        String sql = "INSERT INTO leaderboard (account, score, high_score, achieved) " +
                "VALUES (?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE " +
                "score = VALUES(score), " +
                "high_score = GREATEST(high_score, VALUES(high_score)), " +
                "achieved = NOW()";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, User.currentUser.getAccount());
            pstmt.setInt(2, score);
            pstmt.setInt(3, maxScore);

            pstmt.executeUpdate();
            System.out.println("排行榜已更新");

        } catch (SQLException e) {
            System.err.println("更新排行榜失败: " + e.getMessage());
            // 如果表不存在则创建
            createLeaderboardTable();
            // 重试一次
            updateLeaderboard();
        }
    }

    public static void createLeaderboardTable() {
        // 修改创建表的SQL
        String createSql = "CREATE TABLE IF NOT EXISTS leaderboard (" +
                "account VARCHAR(255) NOT NULL PRIMARY KEY, " +  // 账号作为主键
                "score INT NOT NULL, " +                         // 当前分数
                "high_score INT NOT NULL, " +                    // 历史最高分
                "achieved TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " + // 最后更新时间
                "INDEX (high_score), " +                         // 为高分建索引
                "INDEX (score))";                                // 为当前分建索引

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createSql);
            System.out.println("排行榜表已创建");
        } catch (SQLException e) {
            System.err.println("创建表失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        if (!GameZtai.GameLoad()) {
            InitialMap();
        }
        PrintMap();
        GameLoop();
    }

    //1.初始化地图
    public static void InitialMap() {
        // 清空地图
        for (int i = 0; i < mapSize; i++) {
            Arrays.fill(map[i], 0);
        }

        //设置四个角落的初始位置
        int[][] corners = {{0, 0}, {0, 3}, {3, 0}, {3, 3}};
        //随机挑一个角落放入数字
        int[] cornersPos = corners[r.nextInt(4)];
        //挑选一个角落放入数字,2的概率是90%,4的概率为10%
        map[cornersPos[0]][cornersPos[1]] = r.nextDouble() < 0.9 ? 2 : 4;

        while (true) {
            //在地图范围内
            int i = r.nextInt(mapSize);
            int j = r.nextInt(mapSize);
            //放入不是角落位置的随机数
            if (!isCorner(i, j, mapSize) && map[i][j] == 0) {
                map[i][j] = r.nextDouble() < 0.9 ? 4 : 2;
                break;
            }
        }
    }

    private static boolean isCorner(int i, int j, int mapSize) {
        return (i == 0 && j == 0) ||
                (i == 0 && j == mapSize - 1) ||
                (i == mapSize - 1 && j == 0) ||
                (i == mapSize - 1 && j == mapSize - 1);
    }

    // 2.打印地图
    public static void PrintMap() {
        System.out.println(" ♒当前地图状态♒");
        System.out.println("-----------------");
        for (int i = 0; i < mapSize; i++) {
            System.out.print("| ");
            for (int j = 0; j < mapSize; j++) {
                System.out.print(map[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-----------------");
        }
    }

    //3. 游戏主循环
    public static void GameLoop() throws Exception {
        while (true) {
            // 计时模式下检查是否超时（2分钟 = 120000毫秒）
            if (Menu.isTimedMode) {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = (currentTime - Menu.startTime) / 1000; // 转换为秒

                // 显示剩余时间
                long remainingTime = 120 - elapsedTime;
                System.out.println("⏰ 剩余时间: " + remainingTime + "秒");

                // 如果超过2分钟，游戏结束
                if (elapsedTime >= 120) {
                    System.out.println("\n⌛ 时间到！游戏结束！");
                    updateLeaderboard();
                    return;
                }
            }

            System.out.println("\uD83D\uDEA9当前分数:" + score + " 历史最佳:" + maxScore);
            System.out.println("\n\uD83D\uDE80操作指令: ☆W-上 S-下 A-左 D-右" +
                    "\n           ★E-保存 R-读取 Q-退出 N-新游戏 F-撤销");

            if (isGameOver()) {
                System.out.println("游戏结束！GG!");
                PrintMap();
                updateLeaderboard();
                return;
            }

            String input = Common.sc.next().toLowerCase();
            boolean moved = false;

            switch (input) {
                case "w":
                    moved = MoveUp();
                    break;
                case "s":
                    moved = MoveDown();
                    break;
                case "a":
                    moved = MoveLeft();
                    break;
                case "d":
                    moved = MoveRight();
                    break;
                case "e":
                    try {
                        moved = GameZtai.GameSave();
                        if (moved) {
                            System.out.println("✅ 游戏已保存!");
                            System.out.println("🕒 保存时间: " + new Date());
                        } else {
                            System.out.println("❌ 保存失败!");
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ 保存时发生错误: " + e.getMessage());
                    }
                    break;
                case "r":
                    try {
                        moved = GameZtai.GameLoad();
                        if (moved) {
                            System.out.println("✅ 游戏加载成功!");
                            System.out.println("🕒 加载时间: " + new Date());
                            // 加载游戏后重置撤销栈
                            undoStack.clear();
                            saveStateToUndoStack();
                        } else {
                            System.out.println("❌ 加载失败!");
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ 加载时发生错误: " + e.getMessage());
                    }
                    break;
                case "n":
                    if (GameZtai.StartNewGame()) {
                        score = 0;
                        InitialMap();
                        // 新游戏重置撤销栈
                        undoStack.clear();
                        saveStateToUndoStack();
                        PrintMap();
                        System.out.println("🆕 新游戏已开始!");
                    }
                    break;
                case "f":
                    moved = undoLastMove();
                    break;
                case "q":
                    System.out.println("已退出,期待下次见面\uD83D\uDC4B");
                    return;
                default:
                    System.out.println("无效指令！");
                    continue;
            }

            if (moved && !input.equals("f")) { // 移动操作才保存状态，撤销操作不保存
                saveStateToUndoStack();
                PrintMap();
                if (score > maxScore) {
                    maxScore = score;
                    System.out.println("\uD83C\uDFC6" + "新纪录: " + maxScore);
                }
            } else if (input.equals("f")) {
                // 撤销操作后重新打印地图
                PrintMap();
            }
        }
    }

    //4. 移动过后会填补空缺格子而随机生成新数字的方法
    private static void newNum() {
        List<int[]> newNum = new ArrayList<>();
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if (map[i][j] == 0) {
                    newNum.add(new int[]{i, j});
                }
            }
        }
        if (!newNum.isEmpty()) {
            int[] pos = newNum.get(r.nextInt(newNum.size()));
            map[pos[0]][pos[1]] = r.nextDouble() < 0.9 ? 2 : 4;
        }
    }

    //5. 检查游戏是否结束
    public static boolean isGameOver() {
        // 检查是否有空格
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if (map[i][j] == 0) {
                    return false;
                }
            }
        }
        // 检查是否可以合并
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if (j < mapSize - 1 && map[i][j] == map[i][j + 1]) {
                    return false;
                }
                if (i < mapSize - 1 && map[i][j] == map[i + 1][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    //6.向右移动的方法
    public static boolean MoveRight() {
        boolean moved = false;
        boolean[][] merged = new boolean[mapSize][mapSize]; // 如果已合并相同的数,则存到数组中,不在合并,遇到相同的数还会继续合并
        //把当前格子和新的格子全部移到右边
        for (int i = 0; i < mapSize; i++) {
            //方向是向右,所以循环从mapSize-1开始向左遍历(也就是倒数第二列)
            for (int j = mapSize - 1; j > 0; j--) {
                //如果当前格子为0
                if (map[i][j] == 0) {
                    //新生成的格子从j-1开始遍历,如果k为0会越界,每执行一次,列减一
                    for (int k = j - 1; k >= 0; k--) {
                        //判断新的格子不为0
                        if (map[i][k] != 0) {
                            //当期格子和新的格子相等
                            map[i][j] = map[i][k];
                            //新的格子和最右边格子靠拢,位置清零
                            map[i][k] = 0;
                            moved = true;//标记已合并
                            break;//结束当前循环
                        }
                    }
                }
            }

            //从右向左开始遍历,但优先向右合并,起始边界为mapSize-1,索引要大于0,向左遍历所以执行j--
            for (int j = mapSize - 1; j > 0; j--) {
                //判断当前格子不为0,且与相邻格子相等
                if (map[i][j] != 0 && !merged[i][j] && map[i][j] == map[i][j - 1]) {
                    int mergedValue = map[i][j] * 2; // 计算合并后的值
                    score += mergedValue;// 增加分数
                    map[i][j] = mergedValue;// 更新格子值
                    //位置清零
                    map[i][j - 1] = 0;
                    merged[i][j] = true; // 标记已合并
                    merged[i][j - 1] = true;  // 标记相邻格子
                    moved = true;
                }
            }
            //再次移动填补向右合并后,产生新的空格
            for (int j = mapSize - 1; j > 0; j--) {
                if (map[i][j] == 0) {
                    for (int k = j - 1; k >= 0; k--) {
                        if (map[i][k] != 0) {
                            map[i][j] = map[i][k];
                            map[i][k] = 0;
                            moved = true;
                            break;
                        }
                    }
                }
            }
        }
        //如果发深了移动,则随机在生成2/4的数
        if (moved) {
            //调用移动后只产生一个数的方法
            newNum();
        }

        return moved;
    }

    //7.向左移动的方法
    public static boolean MoveLeft() {
        boolean moved = false;
        boolean[][] merged = new boolean[mapSize][mapSize]; // 标记是否已合并

        //把所有格子先移到最左侧
        for (int i = 0; i < mapSize; i++) {
            //虽然是向左的方向,但遍历从0开始,边界不超过mapSize-1,遍历从第二列开始往右找,所以执行j++
            for (int j = 0; j < mapSize - 1; j++) {
                //判断当前位置为0则生成新的空格k
                if (map[i][j] == 0) {
                    //因为是向右遍历所以执行j+1,边界不超过mapSize,执行k++
                    for (int k = j + 1; k < mapSize; k++) {
                        //如果新的格子不为0
                        if (map[i][k] != 0) {
                            //当前位置就等于新的格子
                            map[i][j] = map[i][k];
                            //移动后,位置清零
                            map[i][k] = 0;
                            moved = true;
                            break;
                        }
                    }
                }
            }

            //向左合并的方法
            for (int j = 0; j < mapSize - 1; j++) {
                //当前格子不为0,且等于相邻格子
                if (map[i][j] != 0 && !merged[i][j] && map[i][j] == map[i][j + 1]) {

                    int mergedValue = map[i][j] * 2; // 计算合并后的值
                    score += mergedValue;            // 增加分数
                    map[i][j] = mergedValue;         // 更新格子值

                    //位置清零
                    map[i][j + 1] = 0;
                    merged[i][j] = true; // 标记已合并
                    merged[i][j + 1] = true;  // 标记相邻格子
                    moved = true;
                }
            }
            //再次移动填补向左合并移动后,产生新的空格
            for (int j = 0; j < mapSize - 1; j++) {
                if (map[i][j] == 0) {
                    for (int k = j + 1; k < mapSize; k++) {
                        if (map[i][k] != 0) {
                            map[i][j] = map[i][k];
                            map[i][k] = 0;
                            moved = true;
                            break;
                        }
                    }
                }
            }
        }
        //如果发深了移动,则随机在生成2/4的数
        if (moved) {
            newNum();
        }
        return moved;
    }

    //8.向上的方法
    public static boolean MoveUp() {
        boolean moved = false;
        boolean[][] merged = new boolean[mapSize][mapSize]; // 标记是否已合并

        //虽然是向上的方向,但起始位置从0(也就是第二行开始向下循环),边界不超过mapSize,向下遍历所以遍历一次列就加一格,所以执行i++
        //列没有减少,所以索引从0开始,小于mapSize,刚好遍历四列(0,1,2,3)
        for (int j = 0; j < mapSize; j++) {
            //行数从0开始,因为向下遍历所以边界不超过mapSize-1,向下一次行数加一,所以执行i++
            for (int i = 0; i < mapSize - 1; i++) {
                //如果当前格不为0,则产生一个新的格子K
                if (map[i][j] == 0) {
                    //新的格子,向下遍历,所以执行i+1
                    for (int k = i + 1; k < mapSize; k++) {
                        //如果新格子不为0
                        if (map[k][j] != 0) {
                            //则当前格子等于新的格子
                            map[i][j] = map[k][j];
                            //格子清零
                            map[k][j] = 0;
                            moved = true;
                            break;
                        }
                    }
                }
            }
            //向上合并的方
            //行的条件不变
            for (int i = 0; i < mapSize - 1; i++) {
                //当前格子和相邻格子相等且不为0
                if (map[i][j] != 0 && !merged[i][j] && map[i][j] == map[i + 1][j]) {

                    int mergedValue = map[i][j] * 2; // 计算合并后的值
                    score += mergedValue;            // 增加分数
                    map[i][j] = mergedValue;         // 更新格子值

                    //格子清零
                    map[i + 1][j] = 0;
                    merged[i][j] = true; // 标记已合并
                    merged[i + 1][j] = true;  // 标记相邻格子
                    moved = true;
                }
            }
            //再次移动,填补向上合并后,列的空格
            for (int i = 0; i < mapSize - 1; i++) {
                if (map[i][j] == 0) {
                    for (int k = i + 1; k < mapSize; k++) {
                        if (map[k][j] != 0) {
                            map[i][j] = map[k][j];
                            map[k][j] = 0;
                            moved = true;
                            break;
                        }
                    }
                }
            }
        }
        //如果发深了移动,则随机在生成2/4的数
        if (moved) {
            newNum();
        }
        return moved;
    }

    //9.向下移动的方法
    public static boolean MoveDown() {
        boolean moved = false;
        boolean[][] merged = new boolean[mapSize][mapSize]; // 标记是否已合并

        //列的没有减少条件不变和向上方向的列条件一样
        for (int j = 0; j < mapSize; j++) {
            //因为是向下的方向,但遍历从mapSize-1开始(也就是倒数第二行往上遍历),往上的所以执行i--
            for (int i = mapSize - 1; i > 0; i--) {
                //如果当前格子为0,则产生新的格子K
                if (map[i][j] == 0) {
                    //新的格子k也从倒数第二行往上遍历,所以i-1...
                    for (int k = i - 1; k >= 0; k--) {
                        //如果k格子不为0
                        if (map[k][j] != 0) {
                            //则当前格子等于新格子k
                            map[i][j] = map[k][j];
                            //格子清零
                            map[k][j] = 0;
                            moved = true;
                            break;
                        }
                    }
                }
            }
            //向下的合并方法
            //行条件不变
            for (int i = mapSize - 1; i > 0; i--) {
                //如果当前格子和相邻格子相等且不为0
                if (map[i][j] != 0 && !merged[i][j] && map[i][j] == map[i - 1][j]) {

                    int mergedValue = map[i][j] * 2; // 计算合并后的值
                    score += mergedValue;            // 增加分数
                    map[i][j] = mergedValue;         // 更新格子值

                    //格子清零
                    map[i - 1][j] = 0;
                    merged[i][j] = true; // 标记已合并
                    merged[i - 1][j] = true;  // 标记相邻格子
                    moved = true;
                }
            }
            //再次移动填补向下合并后,产生的空格
            for (int i = mapSize - 1; i > 0; i--) {
                if (map[i][j] == 0) {
                    for (int k = i - 1; k >= 0; k--) {
                        if (map[k][j] != 0) {
                            map[i][j] = map[k][j];
                            map[k][j] = 0;
                            moved = true;
                            break;
                        }
                    }
                }
            }
        }
        //如果发深了移动,则随机在生成2/4的数
        if (moved) {
            newNum();
        }
        return moved;
    }
}