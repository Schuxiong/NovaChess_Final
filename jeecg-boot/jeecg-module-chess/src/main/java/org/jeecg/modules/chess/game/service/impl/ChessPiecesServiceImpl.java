package org.jeecg.modules.chess.game.service.impl;

import org.jeecg.modules.chess.game.entity.ChessPieces;
import org.jeecg.modules.chess.game.mapper.ChessPiecesMapper;
import org.jeecg.modules.chess.game.service.ChessPiecesPositionService;
import org.jeecg.modules.chess.game.service.IChessPiecesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 棋子位置
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
@Service
public class ChessPiecesServiceImpl extends ServiceImpl<ChessPiecesMapper, ChessPieces> implements IChessPiecesService {
    @Autowired
    private ChessPiecesPositionService chessPiecesPositionService;
    @Override
    public List<ChessPieces> initPosition(String chessGameId) {
        List<ChessPieces> lstChessPieces =  chessPiecesPositionService.obtainChessPiecesList(chessGameId);
        this.saveBatch(lstChessPieces);
        return lstChessPieces;
    }
}
