package page02;
import page04.DatabaseConnector;
import java.sql.*;
import static page03.GameController.*;
/**
 * @BelongsProject: Priject_0729
 * @BelongsPackage: page05
 * @Author: 阿枫
 * @CreateTime: 2025-07-29 14:40
 * @Description: 用户登录注册方法类
 * @Version: 1.0
 */
public class User {
    public static User currentUser;
    private String account;
    private int currentScore;
    private int highScore;

    public User(String account) {
        this.account = account;
        this.currentScore = 0;
        this.highScore = 0;
    }

    //注册方法
    public static void register(String account, String password) throws Exception {
        //调用数据库
        Connection connection = DatabaseConnector.db();
        //写查询语句数据库是否有用户名
        String checkSql = "SELECT * FROM users WHERE account = ?";
        //创建PreparedStatement对象,用于执行查询语句
        try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setString(1, account);
            //返回结果集,检查数据库是否有匹配的值
            ResultSet rs = checkPs.executeQuery();
            //如果放回true,说明数据库有这个用户名
            //返回false,说明用户名可用
            if (rs.next()) {
                System.out.println("用户名已存在！");
                return;
            }

            String insertSql = "INSERT INTO users (account, password) VALUES (?, ?)";
            try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                insertPs.setString(1, account);
                insertPs.setString(2, password);
                int rows = insertPs.executeUpdate();
                if (rows > 0) {
                    System.out.println("注册成功!");
                    currentUser = new User(account);
                    resetGameScores(); // 注册后重置分数
                }
            }
        }
    }

    //登录方法
    public static boolean login(String account, String password) throws Exception {
        //调用数据库
        Connection connection = DatabaseConnector.db();
        //查询表中是否有这些字段
        String sql = "SELECT * FROM users WHERE account = ? AND password = ?";
        ////创建PreparedStatement对象,用于执行查询语句
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            //读到用户名和密码
            ps.setString(1, account);
            ps.setString(2, password);
            //返回结果集
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                //如果读取到则登录成功
                currentUser = new User(account);
                resetGameScores(); // 登录成功重置分数
                loadUserHighScore(); // 加载用户历史最高分
                return true;
            }
            return false;
        }
    }

    public static void logout() {
        currentUser = null;
        resetGameScores();
    }

    //游客模式方法
    public static void enterAsGuest() {
        currentUser = null;
        resetGameScores();
    }

    //注册分数重置为0方法
    private static void resetGameScores() {
        score = 0;
        maxScore = 0;
    }

    //加载历史最佳分数方法
    private static void loadUserHighScore() {
        // 检查当前用户是否登录，未登录则直接返回
        if (currentUser == null) return;
        // 定义查询用户最高分的SQL语句（使用?作为参数占位符）
        String sql = "SELECT high_score FROM leaderboard WHERE account = ?";
        // 使用try-with-resources自动管理数据库连接和PreparedStatement
        try (
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);  // 获取数据库连接
                PreparedStatement pstmt = conn.prepareStatement(sql)) {//创建预编译的SQL语句对象
// 设置SQL参数（将当前用户账号替换第一个?占位符）
            pstmt.setString(1, currentUser.getAccount());
            // 执行查询并获取结果集
            ResultSet rs = pstmt.executeQuery();
            // 如果查询到结果（用户有历史最高分记录）
            if (rs.next()) {
                // 从结果集中读取high_score字段的值，并更新maxScore
                maxScore = rs.getInt("high_score");
            }
        } catch (SQLException e) {
            // 捕获并处理数据库操作可能出现的异常
            System.err.println("加载用户最高分失败: " + e.getMessage());
        }
    }

    public String getAccount() {
        return account;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public String getUsername() {
        return "";
    }
}