package page01;
import page02.User;
import page03.GameController;
import java.io.*;
import java.sql.*;
/**
 * @BelongsProject: Priject_0729
 * @BelongsPackage: page05
 * @Author: 阿枫
 * @CreateTime: 2025-07-29 14:40
 * @Description: 游戏读存类
 * @Version: 1.0
 */
public class GameState {
    // 数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/game2048";
    private static final String USER = "root";
    private static final String PASS = "123456";

    /**
     * 初始化数据库表结构
     */
    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS game_saves (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL, " +
                "save_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "game_data LONGBLOB NOT NULL, " +  // 存储序列化数据
                "UNIQUE KEY (username))";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("初始化数据库失败: " + e.getMessage());
        }
    }

    /**
     * 保存游戏状态到数据库
     */
    public static boolean GameSave() {
        // 使用try-with-resources确保数据库连接自动关闭
        try (Connection conn = getConnection()) {
            // 序列化游戏状态
            GameStatus state = new GameStatus();
            state.setScore(GameController.score);
            state.setMaxScore(GameController.maxScore);
            state.setMap(GameController.map);
            //把字符串转化成二进制数据,在转换成字节流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(state);// 序列化对象到字节流
            }
            //gameData游戏参数
            byte[] gameData = baos.toByteArray(); // 获取序列化后的字节数组

            // 获取当前用户名 - 修改为统一使用登录用户名或"guest"
            String username = User.currentUser != null ?
                    User.currentUser.getUsername() : "guest";

            // 检查是否已有存档
            boolean hasSave = checkSaveExists(conn, username);

            // 插入或更新存档
            String sql = hasSave ?
                    "UPDATE game_saves SET game_data = ?, save_time = CURRENT_TIMESTAMP WHERE username = ?" :
                    "INSERT INTO game_saves (username, game_data) VALUES (?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // 1. 设置游戏数据参数
                pstmt.setBytes(1, gameData);
                // 2. 动态参数设置（根据是否有存档）
                pstmt.setString(hasSave ? 2 : 1, username);
                // 3. 如果是新存档，设置第二个gameData参数
                if (!hasSave) pstmt.setBytes(2, gameData);
                // 4. 执行更新并返回结果
                return pstmt.executeUpdate() > 0;
            }//抛出保存工程中可能出现的异常
        } catch (SQLException | IOException e) {
            System.err.println("保存失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 从数据库加载游戏存档
     */
    public static boolean GameLoad() {
        //使用try-with-resources确保连接自动关闭
        // 调用getConnection()获取数据库连接
        try (Connection conn = getConnection()) {
            // 统一使用"guest"作为游客用户名
            //如果有登录用户，使用其用户名
            // 如果没有登录用户，使用"guest"作为默认用户名
            String username = User.currentUser != null ?
                    User.currentUser.getUsername() : "guest";
            //因为刚刚保存了game_data字段,所以查询这个字段game_data
            String sql = "SELECT game_data FROM game_saves WHERE username = ?";
            //创建PreparedStatement对象,连接数据库
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                //调出来这个字段的信息
                pstmt.setString(1, username);
                //执行查询并获取结果集
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        //如果有结果（找到存档），获取二进制游戏数据
                        // 如果没有结果，输出错误信息并返回false
                        byte[] gameData = rs.getBytes("game_data");

                        // 反序列化,就是把数组转化会字节流,字节流在转化为字符串对象
                        try (ObjectInputStream ois = new ObjectInputStream(
                                new ByteArrayInputStream(gameData))) {
                            GameStatus state = (GameStatus) ois.readObject();

                            // 恢复游戏状态
                            GameController.score = state.getScore();
                            GameController.maxScore = state.getMaxScore();

                            int[][] savedMap = state.getMap();
                            if (savedMap.length == GameController.map.length &&
                                    savedMap[0].length == GameController.map[0].length) {
                                System.arraycopy(savedMap, 0, GameController.map, 0, savedMap.length);
                            }
                            return true;
                        }
                    } else {
                        System.err.println("没有找到用户 " + username + " 的存档");
                        return false;
                    }
                }
            }
            //SQLException：数据库操作错误
            // IOException：数据流读写错误
            // ClassNotFoundException：反序列化类未找到
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.err.println("加载失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 开始新游戏（删除存档）
     */
    public static boolean StartNewGame() {
        try (Connection conn = getConnection()) {
            String username = User.currentUser != null ?
                    User.currentUser.getUsername() : "guest";

            String sql = "DELETE FROM game_saves WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                return pstmt.executeUpdate() >= 0; // 总是返回true，即使没有存档
            }
        } catch (SQLException e) {
            System.err.println("删除存档失败: " + e.getMessage());
            return false;
        }
    }

    // 私有辅助方法
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private static boolean checkSaveExists(Connection conn, String username) throws SQLException {
        String sql = "SELECT id FROM game_saves WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}