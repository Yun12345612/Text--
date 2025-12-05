package page04;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
/**
 * @BelongsProject: Priject_0729
 * @BelongsPackage: page05
 * @Author: 阿枫
 * @CreateTime: 2025-07-29 14:46
 * @Description: 数据库工具类
 * @Version: 1.0
 */
public class DatabaseConnector {
    public static String driver;
    public static String url;
    public static String userName;
    public static String password;
    public static int high_score;
    public static int max_score;


    //静态代码块，和类一起加载
    static {
        Properties prop = new Properties();
        try {
            //读取配置文件
            prop.load(new FileInputStream("config/db.properties"));//配置文件的相对路径
            //把配置文件中读取到的数据赋给当前类变量
            driver = prop.getProperty("driver");// 读取配置文件中的driver
            url = prop.getProperty("url");//读取配置文件中的url
            userName = prop.getProperty("username");//读取配置文件中的userName
            password = prop.getProperty("password");// 读取配置文件中的password
            // 加载驱动
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("数据库配置加载失败", e);
        }
    }

    public static Connection db() throws Exception {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //建立连接
        Connection connection = DriverManager.getConnection(
                url,
                userName,
                password

        );
        return connection;
    }
}