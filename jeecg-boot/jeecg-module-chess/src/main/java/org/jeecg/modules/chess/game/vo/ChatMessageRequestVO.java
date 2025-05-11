package org.jeecg.modules.chess.game.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMessageRequestVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private ChatMessageRequestVO.MessageType type;

    /** 用户Id */
    private String userId;

    /** 用户名 */
    private String username;

    /** 棋局Id */
    private String gameId;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
