package org.jeecg.modules.chess.game.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import org.jeecg.modules.chess.game.entity.ChessMove;

@Data
public class ChessMoveResponseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private ChessPiecesResponseVO blackPiecesVO;

    private ChessPiecesResponseVO whitePiecesVO;

    /** 操作是否成功 **/
    private Boolean success = true;

    /** 错误信息 **/
    private String errorMessage;

    /** 最新的游戏状态 **/
    private ChessGameVO latestGameState;

    /** 走棋记录列表 **/
    private List<ChessMove> moveHistory;
}
