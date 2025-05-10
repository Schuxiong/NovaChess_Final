package org.jeecg.modules.chess.game.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChessMoveResponseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private ChessPiecesResponseVO blackPiecesVO;

    private ChessPiecesResponseVO whitePiecesVO;

    /** 操作是否成功 **/
    private Boolean success = true;

    /** 最新的游戏状态 **/
    private ChessGameVO latestGameState;
}
