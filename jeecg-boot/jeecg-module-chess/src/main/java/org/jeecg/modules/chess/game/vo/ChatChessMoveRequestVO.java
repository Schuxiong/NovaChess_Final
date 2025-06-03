package org.jeecg.modules.chess.game.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatChessMoveRequestVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chessGameId;

    /** 游戏ID，与chessGameId相同，提供别名方便使用 **/
    private String gameId;

    /** 用户ID **/
    private String userId;

    private String chessPiecesId;

    private Integer piecesType;

    /** 单独的坐标表示 **/
    private String fromPositionX;
    private String fromPositionY;
    private String toPositionX;
    private String toPositionY;

    /** 完整的坐标表示 (例如 "F2") **/
    private String fromPosition;
    private String toPosition;

    /** 升变选择，如果兵升变为其他棋子 **/
    private String promotionPieceType;

    /** 走棋顺序 **/
    private Integer moveSequence;

    /** 走棋耗时（秒） **/
    private Integer moveDurationSeconds;

    /** 被吃棋子ID **/
    private String tookPiecesId;

    /** 是否升变 **/
    private Boolean isPromotion;

    private ChatChessMoveRequestVO.MessageType type;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        MOVE
    }
}
