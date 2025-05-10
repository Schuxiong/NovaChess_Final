package org.jeecg.modules.chess.game.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMessageVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;

    /**用户Id*/
    private String userId;

    /**棋局Id*/
    private String gameId;
    private String content;
    private String sender;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }



}
