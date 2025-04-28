package org.jeecg.modules.chess.game.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlayerWalkVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerId;

    private String piecesId;

    private int fromX;

    private int fromY;

    private int toX;

    private int toY;

}
