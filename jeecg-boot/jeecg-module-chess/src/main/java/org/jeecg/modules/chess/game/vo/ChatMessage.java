package org.jeecg.modules.chess.game.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private String content;
    private String sender;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
