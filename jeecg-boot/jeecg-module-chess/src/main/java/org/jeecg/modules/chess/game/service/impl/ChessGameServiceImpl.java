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
import org.jeecg.modules.chess.game.vo.ChessGameBatchVO;
import org.jeecg.modules.chess.game.vo.ChessGameVO;
import org.jeecg.modules.chess.game.vo.PlayerPairVO;
import org.jeecg.modules.chess.score.entity.ChessPlayerScore;
import org.jeecg.modules.chess.score.service.IChessPlayerScoreService;
import org.jeecg.modules.chess.util.Constant;
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

    @Autowired
    private IChessPlayerScoreService chessPlayerScoreService;


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

            //3.初始化选手分数
            List<ChessPlayerScore>  lstChessPlayerScore = this.siwtchChessPlayerScore(sourcePlayerPariVO,targetPlayerPairVO);
            if(!lstChessPlayerScore.isEmpty()){
                chessPlayerScoreService.saveOrUpdateBatch(lstChessPlayerScore);
            }

            // 4. 初始化棋盘信息
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

    private List<ChessPlayerScore> siwtchChessPlayerScore(PlayerPairVO sourcePlayerPariVO, PlayerPairVO targetPlayerPairVO) {
        List<ChessPlayerScore> lstResult = new LinkedList<>();
        ChessPlayerScore objQueryChessPlayerScore = new ChessPlayerScore();
        Map<String,String[]> objParamMap = new HashMap<>();
        String[] strUserIdArray = new String[2];
        strUserIdArray[0] = sourcePlayerPariVO.getUserId();
        objParamMap.put("user_id",strUserIdArray);
        objParamMap.put("del_flag",new String[]{"0"});
        QueryWrapper<ChessPlayerScore> queryWrapper = QueryGenerator.initQueryWrapper(objQueryChessPlayerScore, objParamMap);
        ChessPlayerScore objResult = chessPlayerScoreService.getOne(queryWrapper);
        if(objResult == null){
            ChessPlayerScore objSource = new ChessPlayerScore();
            objSource.setId(UUIDGenerator.generate());
            objSource.setUserId(sourcePlayerPariVO.getUserId());
            objSource.setUserAccount(sourcePlayerPariVO.getUserAccount());
            objSource.setScore(Constant.SCORE_INIT);
            lstResult.add(objSource);
        }
        strUserIdArray[0] = targetPlayerPairVO.getUserId();
        queryWrapper = QueryGenerator.initQueryWrapper(objQueryChessPlayerScore, objParamMap);
        objResult = chessPlayerScoreService.getOne(queryWrapper);
        if(objResult == null){
            ChessPlayerScore objTarget = new ChessPlayerScore();
            objTarget.setId(UUIDGenerator.generate());
            objTarget.setUserId(targetPlayerPairVO.getUserId());
            objTarget.setUserAccount(targetPlayerPairVO.getUserAccount());
            objTarget.setScore(Constant.SCORE_INIT);
            lstResult.add(objTarget);
        }


        return lstResult;

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


    @Override
    public List<ChessGameBatchVO> enter(String userId) {
        QueryWrapper<ChessPlayer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("del_flag",0);
        List<ChessPlayer> lstChessPlayer = chessPlayerService.list(queryWrapper);

        if(lstChessPlayer == null || lstChessPlayer.isEmpty()){
            return null;
        }
        List<String> lstChessGameId = new LinkedList<>();
        for(ChessPlayer temp : lstChessPlayer){
            lstChessGameId.add(temp.getChessGameId());
        }
        QueryWrapper<ChessGame> queryChessGameWrapper = new QueryWrapper<>();
        queryChessGameWrapper.in("id",lstChessGameId);
        queryChessGameWrapper.eq("gameState",1);
        queryChessGameWrapper.eq("delFlag",0);
        List<ChessGame> lstChessGame = this.list(queryChessGameWrapper);
        List<ChessGameBatchVO> lstChessGameBatchVO = new LinkedList<>();
        if(lstChessGame != null && !lstChessGame.isEmpty()){
            for(ChessGame temp  :lstChessGame){
                ChessGameBatchVO obj = new ChessGameBatchVO();
                obj.setGameId(temp.getId());
                lstChessGameBatchVO.add(obj);
            }
        };

        return lstChessGameBatchVO;
    }

}
