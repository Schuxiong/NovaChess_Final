package org.jeecg.modules.chess.game.constant;

/**
 * 游戏状态常量
 * @Description: 定义游戏的各种状态
 * @Author: jeecg-boot
 * @Date: 2025-01-20
 * @Version: V1.0
 */
public class GameStateConstant {
    
    /** 游戏进行中 */
    public static final Integer GAME_PLAYING = 1;
    
    /** 游戏正常结束 */
    public static final Integer GAME_FINISHED = 2;
    
    /** 黑方胜利 */
    public static final Integer BLACK_WIN = 3;
    
    /** 白方胜利 */
    public static final Integer WHITE_WIN = 4;
    
    /** 和棋 */
    public static final Integer DRAW = 5;
    
    /** 流局(超时) */
    public static final Integer TIMEOUT = 6;
    
    /** 黑方退出 */
    public static final Integer BLACK_QUIT = 7;
    
    /** 白方退出 */
    public static final Integer WHITE_QUIT = 8;
    
    /**
     * 获取游戏状态描述
     * @param gameState 游戏状态
     * @return 状态描述
     */
    public static String getGameStateDescription(Integer gameState) {
        if (gameState == null) {
            return "未知状态";
        }
        
        switch (gameState) {
            case 1:
                return "进行中";
            case 2:
                return "正常结束";
            case 3:
                return "黑方胜利";
            case 4:
                return "白方胜利";
            case 5:
                return "和棋";
            case 6:
                return "流局(超时)";
            case 7:
                return "黑方退出";
            case 8:
                return "白方退出";
            default:
                return "未知状态";
        }
    }
    
    /**
     * 判断游戏是否已结束
     * @param gameState 游戏状态
     * @return true-已结束，false-进行中
     */
    public static boolean isGameFinished(Integer gameState) {
        return gameState != null && gameState != GAME_PLAYING;
    }
    
    /**
     * 判断是否为异常结束（退出或超时）
     * @param gameState 游戏状态
     * @return true-异常结束，false-正常结束或进行中
     */
    public static boolean isAbnormalEnd(Integer gameState) {
        return gameState != null && (gameState == TIMEOUT || gameState == BLACK_QUIT || gameState == WHITE_QUIT);
    }
}