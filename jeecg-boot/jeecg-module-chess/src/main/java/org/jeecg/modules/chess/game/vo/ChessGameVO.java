package org.jeecg.modules.chess.game.vo;

import lombok.Data;
import org.jeecg.modules.chess.game.entity.ChessPieces;

import java.io.Serializable;
import java.util.List;

@Data
public class ChessGameVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String gameId;

    private List<ChessPieces> chessPiecesList;
}
