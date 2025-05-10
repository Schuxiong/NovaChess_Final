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

    private String fromPositionX;

    private String fromPositionY;

    private String toPositionX;

    private String toPositionY;

    private ChatChessMoveRequestVO.MessageType type;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
