package page02;
import page01.Menu;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.net.URL;
import java.util.Scanner;
/**
 * @BelongsProject: Priject_0729
 * @BelongsPackage: page05
 * @Author: 阿枫
 * @CreateTime: 2025-07-29 14:40
 * @Description: 用户操作主类
 * @Version: 1.0
 */
public class UserManager {
    private static Clip bgMusic;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        // 使用绝对路径
        playBackgroundMusic("D:/jx2506/源码/作业源码/Project_0729/sounds/2048消除游戏音效_com.wav");

        boolean running = true;
        while (running) {
            System.out.println("===== ★请选择游戏登录★ =====");
            System.out.println("1. 注册");
            System.out.println("2. 登录");
            System.out.println("3. 游客模式(直接进入游戏)");
            System.out.println("4. 退出");
            System.out.print("请选择：");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    handleRegister();//手动注册输入方法
                    break;
                case 2:
                    handleLogin();//手动登录方法
                    break;
                case 3:
                    handleGuestMode();//游客模式方法
                    break;
                case 4:
                    running = false;
                    System.out.println("再见！");
                    stopMusic();//游戏结束则暂停音乐
                    break;
                default:
                    System.out.println("无效选项，请重新输入！");
            }
        }
    }

    // 背景音乐播放方法
    public static void playBackgroundMusic(String filePath) {
        try {
            AudioInputStream audioIn;
            // 判断是否是资源路径
            if (filePath.startsWith("/")) {
                URL resUrl = UserManager.class.getResource(filePath);
                if (resUrl == null) {
                    throw new Exception("资源文件未找到: " + filePath);
                }
                System.out.println("从资源加载: " + resUrl);
                audioIn = AudioSystem.getAudioInputStream(resUrl);
            } else {
                // 绝对路径
                File file = new File(filePath);
                if (!file.exists()) {
                    throw new Exception("文件不存在: " + filePath);
                }
                System.out.println("从文件系统加载: " + filePath);
                audioIn = AudioSystem.getAudioInputStream(file);
            }
            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioIn);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
            // 音量控制//正数调大,负数小
            //最小值：gainControl.getMinimum() (通常是-80.0f)
            // 最大值：gainControl.getMaximum() (通常是6.0206f)
            FloatControl gainControl = (FloatControl) bgMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(6.0f);
        } catch (Exception e) {
            System.err.println("背景音乐加载失败: ");
            e.printStackTrace(); // 打印完整错误栈
        }
    }

    public static void stopMusic() {
        if (bgMusic != null) {
            bgMusic.stop();
            bgMusic.close();
        }
    }
    //打印手动注册且调用注册方法
    private static void handleRegister() throws Exception {
        System.out.print("请输入账号：");
        String regAccount = sc.nextLine();
        System.out.print("请输入密码：");
        String regPassword = sc.nextLine();
        //如果用户注册信息不为空就注册成功
        User.register(regAccount, regPassword);
        if (User.currentUser != null) {
            Menu.menu();
        }
    }
    //打印手动登录且调用登录方法
    private static void handleLogin() throws Exception {
        System.out.print("请输入账号：");
        String loginAccount = sc.nextLine();
        System.out.print("请输入密码：");
        String loginPassword = sc.nextLine();
        //如果用户登录正确的信息就登录成功
        if (User.login(loginAccount, loginPassword)) {
            System.out.println("登录成功！");
            Menu.menu();
        } else {
            System.out.println("登录失败，将以游客模式进入游戏");
            User.enterAsGuest();
            Menu.menu();
        }
    }
    //打印游客模式并调用游客模式方法
    private static void handleGuestMode() throws Exception {
        System.out.println("===== ★欢迎来到2048（游客模式）★ =====");
        User.enterAsGuest();
        Menu.menu();
    }
}