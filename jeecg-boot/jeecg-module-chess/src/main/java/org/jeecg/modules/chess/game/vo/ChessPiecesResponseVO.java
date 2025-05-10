package org.jeecg.modules.chess.game.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class ChessPiecesResponseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chessPiecesId;

    private String chessGameId;

    /**棋子名*/
    private String chessPiecesName;

    /**棋子类别 1:黑 深色，2: 白 浅色*/
    private Integer piecesType;

    private String positionX;

    private String positionY;

    /**棋子状态 1:正常，2:被吃了，不显示*/
    private Integer piecesState;



}
