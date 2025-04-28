package org.jeecg.modules.chess.game.service;

import org.jeecg.modules.chess.game.entity.ChessPieces;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 棋子位置
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
public interface IChessPiecesService extends IService<ChessPieces> {

    public List<ChessPieces> initPosition(String chessGameId);
}
