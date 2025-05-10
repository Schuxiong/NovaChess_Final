package org.jeecg.modules.chess.game.service;

import org.jeecg.modules.chess.game.entity.ChessMove;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.chess.game.vo.ChatChessMoveRequestVO;
import org.jeecg.modules.chess.game.vo.ChessMoveResponseVO;
import org.jeecg.modules.chess.game.vo.ChessPiecesVO;

/**
 * @Description: 行棋
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
public interface IChessMoveService extends IService<ChessMove> {


    public ChessPiecesVO movePieces(ChessMove chessMove);


    public ChessMoveResponseVO chessMovePieces(ChatChessMoveRequestVO chatChessMoveRequestVO);
}
