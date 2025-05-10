package org.jeecg.modules.chess.game.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlayerPairVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;

    private String userAccount;

    /** 执子方：1:黑 深色，2: 白 浅色 */
    private int holdChess;

    /** 邀请ID，用于关联邀请记录 **/
    private String sourceInviteId;
}
