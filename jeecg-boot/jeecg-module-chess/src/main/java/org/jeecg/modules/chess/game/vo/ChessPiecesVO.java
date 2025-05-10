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

    /** 当前轮到哪方下棋，1:黑方，2:白方 **/
    private Integer currentTurn;

    /** 错误信息 **/
    private String errorMessage;

    /** 当前游戏状态 **/
    private ChessGameVO currentGameState;
}
