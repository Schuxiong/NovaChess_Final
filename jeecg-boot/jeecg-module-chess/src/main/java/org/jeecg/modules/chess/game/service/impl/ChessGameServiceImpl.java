package org.jeecg.modules.chess.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.chess.game.entity.ChessGame;
import org.jeecg.modules.chess.game.entity.ChessPieces;
import org.jeecg.modules.chess.game.entity.ChessPlayer;
import org.jeecg.modules.chess.game.mapper.ChessGameMapper;
import org.jeecg.modules.chess.game.service.IChessGameService;
import org.jeecg.modules.chess.game.service.IChessPiecesService;
import org.jeecg.modules.chess.game.service.IChessPlayerService;
import org.jeecg.modules.chess.game.vo.ChessGameVO;
import org.jeecg.modules.chess.game.vo.PlayerPairVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.*;

/**
 * @Description: 游戏
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
@Service
public class ChessGameServiceImpl extends ServiceImpl<ChessGameMapper, ChessGame> implements IChessGameService {

    @Autowired
    private IChessPlayerService chessPlayerService;



    @Autowired
    private IChessPiecesService chessPiecesService;


    @Override
    public ChessGameVO init(PlayerPairVO sourcePlayerPariVO, PlayerPairVO targetPlayerPairVO) {
        try {
            // 1.初始化 参与者信息
            String strChessGameId = UUIDGenerator.generate();
            ChessPlayer objSource = switchChessPlayer(sourcePlayerPariVO);
            String strSourceChessPlayerId = UUIDGenerator.generate();
            objSource.setId(strSourceChessPlayerId);
            objSource.setChessGameId(strChessGameId);
            ChessPlayer objTarget = switchChessPlayer(targetPlayerPairVO);
            String strTargetChessPlayerId = UUIDGenerator.generate();
            objTarget.setId(strTargetChessPlayerId);
            objTarget.setChessGameId(strChessGameId);
            chessPlayerService.saveOrUpdate(objSource);
            chessPlayerService.saveOrUpdate(objTarget);

            // 2.初始化游戏
            ChessGame objChessGame = new ChessGame();
            objChessGame.setId(strChessGameId);
            objChessGame.setBlackPlayId(objSource.getId());
            objChessGame.setBlackPlayAccount(objSource.getUserAccount());
            objChessGame.setWhitePlayId(objTarget.getId());
            objChessGame.setWhitePlayAccount(objTarget.getUserAccount());
            objChessGame.setGameState(1);//进行中
            this.saveOrUpdate(objChessGame);
            // 3. 初始化棋盘信息
            List<ChessPieces> lstChessPieces = chessPiecesService.initPosition(strChessGameId);//初始化棋子位置
            ChessGameVO objChessGameVO = new ChessGameVO();
            objChessGameVO.setGameId(strChessGameId);
            objChessGameVO.setChessPiecesList(lstChessPieces);
            return objChessGameVO;
        }catch (Exception e){
            log.error("错误信息：",e);
        }
        return  new ChessGameVO();
    }




    private ChessPlayer switchChessPlayer(PlayerPairVO sourcePlayerPariVO) {
        ChessPlayer objSource = new ChessPlayer();
        objSource.setUserId(sourcePlayerPariVO.getUserId());
        objSource.setUserAccount(sourcePlayerPariVO.getUserAccount());
        objSource.setHoldChess(sourcePlayerPariVO.getHoldChess());
        objSource.setPlayLevel(1);//默认一级
        objSource.setPlayType(1);//默认人
        return  objSource;
    }
}
