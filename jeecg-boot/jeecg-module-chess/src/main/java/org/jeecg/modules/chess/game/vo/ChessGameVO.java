package org.jeecg.modules.chess.game.vo;

import lombok.Data;
import org.jeecg.modules.chess.game.entity.ChessPieces;

import java.io.Serializable;
import java.util.List;

@Data
public class ChessGameVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String gameId;

    /** 当前用户id **/
    private String currentUserId;

    /** 当前用户 执子方：1:黑 深色，2: 白 浅色 **/
    private int currentHoldChess;

    /** 当前轮到哪方下棋，1:黑方，2:白方 **/
    private int currentTurn;

    /** 错误信息 **/
    private String errorMessage;

    private List<ChessPieces> chessPiecesList;
}
