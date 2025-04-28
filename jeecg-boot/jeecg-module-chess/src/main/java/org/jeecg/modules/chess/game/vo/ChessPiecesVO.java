package org.jeecg.modules.chess.game.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChessPiecesVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chessGameId;


    private String chessPiecesId;

    private String positionX;

    private String positionY;

    private String targetChessPiecesId;


}
