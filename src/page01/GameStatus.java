package page01;
import java.io.Serializable;
/**
 * @BelongsProject: Priject_0729
 * @BelongsPackage: page05
 * @Author: 阿枫
 * @CreateTime: 2025-07-29 14:40
 * @Description: 游戏数据状态类
 * @Version: 1.0
 */
public class GameStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private int[][] map;
    private int score;
    private int maxScore;

    public GameStatus() {
    }

    // Getters and Setters
    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }
}