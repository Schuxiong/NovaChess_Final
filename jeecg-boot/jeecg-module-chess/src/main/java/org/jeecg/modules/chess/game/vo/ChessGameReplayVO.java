package org.jeecg.modules.chess.game.vo;

import lombok.Data;
import org.jeecg.modules.chess.game.entity.ChessGame;
import org.jeecg.modules.chess.game.entity.ChessMove;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 对局回放完整数据结构
 * @Author: jeecg-boot
 * @Date: 2025-06-02
 * @Version: V1.0
 */
@Data
public class ChessGameReplayVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 对局基本信息
     */
    private ChessGame gameInfo;

    /**
     * 黑方玩家信息
     */
    private PlayerInfo blackPlayer;

    /**
     * 白方玩家信息
     */
    private PlayerInfo whitePlayer;

    /**
     * 对局步骤列表（按时间顺序排列）
     */
    private List<ChessMove> moveHistory;

    /**
     * 对局统计信息
     */
    private GameStatistics statistics;

    /**
     * 玩家信息内部类
     */
    @Data
    public static class PlayerInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**用户ID*/
        private String userId;

        /**用户账号*/
        private String userAccount;

        /**用户头像*/
        private String avatar;

        /**用户积分*/
        private Integer score;

        /**用户姓名*/
        private String realname;

        /**执棋方 1:黑方 2:白方*/
        private Integer piecesType;
    }

    /**
     * 对局统计信息内部类
     */
    @Data
    public static class GameStatistics implements Serializable {
        private static final long serialVersionUID = 1L;

        /**总步数*/
        private Integer totalMoves;

        /**对局总时长（秒）*/
        private Integer totalDurationSeconds;

        /**黑方总用时（秒）*/
        private Integer blackTotalTime;

        /**白方总用时（秒）*/
        private Integer whiteTotalTime;

        /**黑方平均每步用时（秒）*/
        private Double blackAverageTimePerMove;

        /**白方平均每步用时（秒）*/
        private Double whiteAverageTimePerMove;

        /**游戏结果描述*/
        private String gameResultDescription;

        /**获胜方 1:黑方胜利 2:白方胜利 0:和棋 -1:未结束*/
        private Integer winner;
    }
}