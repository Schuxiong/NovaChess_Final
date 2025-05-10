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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @Description: 游戏
 * @Author: jeecg-boot
 * @Date: 2025-04-27
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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public ChessGameVO init(PlayerPairVO sourcePlayerPariVO, PlayerPairVO targetPlayerPairVO) {
        try {
            log.debug("开始初始化游戏 - 源用户ID: " + sourcePlayerPariVO.getUserId() + ", 目标用户ID: "
                    + targetPlayerPairVO.getUserId());

            // 1.检查是否已经存在关联该邀请ID的游戏
            if (sourcePlayerPariVO.getSourceInviteId() != null && !sourcePlayerPariVO.getSourceInviteId().isEmpty()) {
                QueryWrapper<ChessGame> existingGameQuery = new QueryWrapper<>();
                existingGameQuery.eq("source_invite_id", sourcePlayerPariVO.getSourceInviteId());
                existingGameQuery.eq("game_state", 1); // 进行中
                existingGameQuery.eq("del_flag", 0);
                List<ChessGame> existingGames = this.list(existingGameQuery);

                if (existingGames != null && !existingGames.isEmpty()) {
                    log.warn("已存在关联邀请ID:" + sourcePlayerPariVO.getSourceInviteId() + "的游戏，数量:" + existingGames.size());
                    // 返回第一个找到的游戏
                    ChessGame existingGame = existingGames.get(0);
                    ChessGameVO existingGameVO = new ChessGameVO();
                    existingGameVO.setGameId(existingGame.getId());
                    existingGameVO.setCurrentTurn(existingGame.getCurrentTurn());

                    // 查找当前用户的角色
                    QueryWrapper<ChessPlayer> playerQuery = new QueryWrapper<>();
                    playerQuery.eq("chess_game_id", existingGame.getId());
                    playerQuery.eq("user_id", targetPlayerPairVO.getUserId());
                    playerQuery.eq("del_flag", 0);
                    ChessPlayer player = chessPlayerService.getOne(playerQuery);

                    if (player != null) {
                        existingGameVO.setCurrentUserId(player.getUserId());
                        existingGameVO.setCurrentHoldChess(player.getHoldChess());
                    }

                    return existingGameVO;
                }
            }

            // 2.初始化 参与者信息
            String strChessGameId = UUIDGenerator.generate();
            log.debug("生成新游戏ID: " + strChessGameId);

            // 查询是否已存在棋手记录
            QueryWrapper<ChessPlayer> sourcePlayerQuery = new QueryWrapper<>();
            sourcePlayerQuery.eq("user_id", sourcePlayerPariVO.getUserId());
            sourcePlayerQuery.eq("del_flag", 0);
            List<ChessPlayer> existingSourcePlayers = chessPlayerService.list(sourcePlayerQuery);
            log.debug("查询到源用户已有棋手记录数量: " + existingSourcePlayers.size());

            // 清理旧的棋手记录
            if (existingSourcePlayers != null && !existingSourcePlayers.isEmpty()) {
                for (ChessPlayer oldPlayer : existingSourcePlayers) {
                    // 设置删除标记而不是物理删除
                    oldPlayer.setDelFlag(1);
                    chessPlayerService.updateById(oldPlayer);
                }
                log.debug("已清理源用户旧棋手记录");
            }

            // 同样处理目标用户记录
            QueryWrapper<ChessPlayer> targetPlayerQuery = new QueryWrapper<>();
            targetPlayerQuery.eq("user_id", targetPlayerPairVO.getUserId());
            targetPlayerQuery.eq("del_flag", 0);
            List<ChessPlayer> existingTargetPlayers = chessPlayerService.list(targetPlayerQuery);
            log.debug("查询到目标用户已有棋手记录数量: " + existingTargetPlayers.size());

            if (existingTargetPlayers != null && !existingTargetPlayers.isEmpty()) {
                for (ChessPlayer oldPlayer : existingTargetPlayers) {
                    oldPlayer.setDelFlag(1);
                    chessPlayerService.updateById(oldPlayer);
                }
                log.debug("已清理目标用户旧棋手记录");
            }

            // 创建新的棋手记录
            ChessPlayer objSource = switchChessPlayer(sourcePlayerPariVO);
            String strSourceChessPlayerId = UUIDGenerator.generate();
            objSource.setId(strSourceChessPlayerId);
            objSource.setChessGameId(strChessGameId);
            objSource.setDelFlag(0);

            ChessPlayer objTarget = switchChessPlayer(targetPlayerPairVO);
            String strTargetChessPlayerId = UUIDGenerator.generate();
            objTarget.setId(strTargetChessPlayerId);
            objTarget.setChessGameId(strChessGameId);
            objTarget.setDelFlag(0);

            log.debug("保存棋手信息 - 源ID: " + strSourceChessPlayerId + ", 目标ID: " + strTargetChessPlayerId);
            chessPlayerService.save(objSource);
            chessPlayerService.save(objTarget);

            // 3.初始化游戏
            ChessGame objChessGame = new ChessGame();
            objChessGame.setId(strChessGameId);

            // Determine black and white based on who holds which color
            // objSource represents the inviting player (parameter 1 from controller, who initiated the game setup)
            // objTarget represents the accepting player (parameter 2 from controller, who is the current user processing the invitation)
            if (objSource.getHoldChess() == 1) { // Inviting player (objSource) chooses black
                objChessGame.setBlackPlayId(objSource.getUserId()); // Use inviter's actual user ID
                objChessGame.setBlackPlayAccount(objSource.getUserAccount()); // Inviter's account
                objChessGame.setWhitePlayId(objTarget.getUserId()); // Use acceptor's actual user ID
                objChessGame.setWhitePlayAccount(objTarget.getUserAccount()); // Acceptor's account
                objChessGame.setCurrentTurn(2); // White's turn first
            } else { // Inviting player (objSource) chooses white
                objChessGame.setWhitePlayId(objSource.getUserId()); // Use inviter's actual user ID
                objChessGame.setWhitePlayAccount(objSource.getUserAccount()); // Inviter's account
                objChessGame.setBlackPlayId(objTarget.getUserId()); // Use acceptor's actual user ID
                objChessGame.setBlackPlayAccount(objTarget.getUserAccount()); // Acceptor's account
                objChessGame.setCurrentTurn(2); // White's turn first (Standard chess rule)
            }

            objChessGame.setGameState(1); // 进行中
            objChessGame.setDelFlag(0);

            // 保存邀请ID到游戏记录 - sourcePlayerPariVO (arg 1) holds the ID set in controller
            if (sourcePlayerPariVO.getSourceInviteId() != null && !sourcePlayerPariVO.getSourceInviteId().isEmpty()) {
                objChessGame.setSourceInviteId(sourcePlayerPariVO.getSourceInviteId());
                // log.debug("关联邀请ID: " + sourcePlayerPariVO.getSourceInviteId());
            } else {
                // Log a warning if the invite ID is missing, as it's expected
                // log.warn("游戏初始化时未找到sourceInviteId, gameId: {}", strChessGameId);
                log.warn("游戏初始化时未找到sourceInviteId, gameId: " + strChessGameId); // Use string concatenation for now
            }

            log.debug("保存游戏信息");
            this.save(objChessGame);

            // 4.初始化选手分数
            List<ChessPlayerScore> lstChessPlayerScore = this.siwtchChessPlayerScore(sourcePlayerPariVO,
                    targetPlayerPairVO);
            if (!lstChessPlayerScore.isEmpty()) {
                log.debug("保存选手分数: " + lstChessPlayerScore.size() + " 条记录");
                chessPlayerScoreService.saveOrUpdateBatch(lstChessPlayerScore);
            }

            // 5.初始化棋盘信息
            log.debug("初始化棋子位置");
            List<ChessPieces> lstChessPieces = chessPiecesService.initPosition(strChessGameId);// 初始化棋子位置

            // 创建返回对象
            ChessGameVO objChessGameVO = new ChessGameVO();
            objChessGameVO.setGameId(strChessGameId);

            // 设置完整的返回信息
            objChessGameVO.setCurrentUserId(targetPlayerPairVO.getUserId());
            objChessGameVO.setCurrentHoldChess(targetPlayerPairVO.getHoldChess());
            objChessGameVO.setCurrentTurn(objChessGame.getCurrentTurn());
            objChessGameVO.setChessPiecesList(lstChessPieces);

            log.debug("游戏初始化完成");
            return objChessGameVO;
        } catch (Exception e) {
            log.error("游戏初始化失败", e);
            // 返回包含错误信息的对象
            ChessGameVO errorResult = new ChessGameVO();
            errorResult.setErrorMessage("初始化游戏失败: " + e.getMessage());
            return errorResult;
        }
    }

    private List<ChessPlayerScore> siwtchChessPlayerScore(PlayerPairVO sourcePlayerPariVO,
            PlayerPairVO targetPlayerPairVO) {
        List<ChessPlayerScore> lstResult = new LinkedList<>();
        try {
            // 获取sourcePlayerPariVO的玩家分数
            QueryWrapper<ChessPlayerScore> sourceQueryWrapper = new QueryWrapper<>();
            sourceQueryWrapper.eq("user_id", sourcePlayerPariVO.getUserId());
            sourceQueryWrapper.eq("del_flag", 0);
            ChessPlayerScore sourceResult = chessPlayerScoreService.getOne(sourceQueryWrapper, false);
            if (sourceResult == null) {
                ChessPlayerScore objSource = new ChessPlayerScore();
                objSource.setId(UUIDGenerator.generate());
                objSource.setUserId(sourcePlayerPariVO.getUserId());
                objSource.setUserAccount(sourcePlayerPariVO.getUserAccount());
                objSource.setScore(Constant.SCORE_INIT);
                objSource.setDelFlag(0);
                lstResult.add(objSource);
            }

            // 获取targetPlayerPairVO的玩家分数
            QueryWrapper<ChessPlayerScore> targetQueryWrapper = new QueryWrapper<>();
            targetQueryWrapper.eq("user_id", targetPlayerPairVO.getUserId());
            targetQueryWrapper.eq("del_flag", 0);
            ChessPlayerScore targetResult = chessPlayerScoreService.getOne(targetQueryWrapper, false);
            if (targetResult == null) {
                ChessPlayerScore objTarget = new ChessPlayerScore();
                objTarget.setId(UUIDGenerator.generate());
                objTarget.setUserId(targetPlayerPairVO.getUserId());
                objTarget.setUserAccount(targetPlayerPairVO.getUserAccount());
                objTarget.setScore(Constant.SCORE_INIT);
                objTarget.setDelFlag(0);
                lstResult.add(objTarget);
            }
        } catch (Exception e) {
            log.error("获取玩家分数失败", e);
        }
        return lstResult;
    }

    private ChessPlayer switchChessPlayer(PlayerPairVO sourcePlayerPariVO) {
        ChessPlayer objSource = new ChessPlayer();
        objSource.setUserId(sourcePlayerPariVO.getUserId());
        objSource.setUserAccount(sourcePlayerPariVO.getUserAccount());
        objSource.setHoldChess(sourcePlayerPariVO.getHoldChess());
        objSource.setPlayLevel(1);// 默认一级
        objSource.setPlayType(1);// 默认人
        return objSource;
    }

    @Override
    public List<ChessGameBatchVO> enter(String userId) {
        QueryWrapper<ChessPlayer> playerQuery = new QueryWrapper<>();
        playerQuery.eq("user_id", userId).eq("del_flag", 0);
        List<ChessPlayer> lstChessPlayer = chessPlayerService.list(playerQuery);

        List<ChessGame> activeGames = new ArrayList<>();

        if (lstChessPlayer != null && !lstChessPlayer.isEmpty()) {
            List<String> gameIds = lstChessPlayer.stream()
                    .map(ChessPlayer::getChessGameId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            if (!gameIds.isEmpty()) {
                QueryWrapper<ChessGame> gameQuery = new QueryWrapper<>();
                gameQuery.in("id", gameIds).eq("game_state", 1).eq("del_flag", 0);
                activeGames = this.list(gameQuery);
            }
        } else {
            QueryWrapper<ChessGame> directGameQuery = new QueryWrapper<>();
            directGameQuery.eq("game_state", 1).eq("del_flag", 0);
            directGameQuery
                    .and(wrapper -> wrapper.eq("black_play_account", userId).or().eq("white_play_account", userId));
            activeGames = this.list(directGameQuery);
        }

        if (activeGames.isEmpty()) {
            return Collections.emptyList();
        }

        List<ChessGameBatchVO> resultList = new LinkedList<>();
        for (ChessGame game : activeGames) {
            ChessGameBatchVO vo = new ChessGameBatchVO();
            vo.setGameId(game.getId());
            resultList.add(vo);

            notifyOpponentOfJoin(game.getId(), userId);
        }

        return resultList;
    }

    private void notifyOpponentOfJoin(String gameId, String joiningUserId) {
        try {
            ChessGame game = this.getById(gameId);
            if (game == null)
                return;

            String opponentUserId = null;

            QueryWrapper<ChessPlayer> opponentQuery = new QueryWrapper<>();
            opponentQuery.eq("chess_game_id", gameId)
                    .ne("user_id", joiningUserId)
                    .eq("del_flag", 0);
            List<ChessPlayer> players = chessPlayerService.list(opponentQuery);

            if (players != null && !players.isEmpty()) {
                opponentUserId = players.get(0).getUserId();
            }

            if (opponentUserId != null) {
                String destination = "/topic/game/" + gameId;

                Map<String, String> payload = new HashMap<>();
                payload.put("type", "OPPONENT_JOINED");
                payload.put("gameId", gameId);
                payload.put("joiningUserId", joiningUserId);

                messagingTemplate.convertAndSend(destination, payload);
            }
        } catch (Exception e) {
            // log.error("Failed to send OPPONENT_JOINED notification for game {}", gameId,
            // e);
            log.error("Failed to send OPPONENT_JOINED notification for game " + gameId, e);
        }
    }

    /**
     * 获取当前棋子位置信息
     * 
     * @param chessGameId
     * @param userId
     * @return
     */
    @Override
    public ChessGameVO getChessGameChessPieces(String chessGameId, String userId) {
        QueryWrapper<ChessPieces> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("chess_game_id", chessGameId);
        queryWrapper.eq("del_flag", 0);
        List<ChessPieces> lstChessPieces = chessPiecesService.list(queryWrapper);

        QueryWrapper<ChessPlayer> queryPlayerWrapper = new QueryWrapper<>();
        queryPlayerWrapper.eq("chess_game_id", chessGameId);
        queryPlayerWrapper.eq("user_id", userId);
        queryPlayerWrapper.eq("del_flag", 0);
        ChessPlayer objChessPlayer = chessPlayerService.getOne(queryPlayerWrapper);

        // 获取游戏信息，包括当前轮次
        ChessGame objChessGame = this.getById(chessGameId);

        ChessGameVO objChessGameVO = new ChessGameVO();
        objChessGameVO.setGameId(chessGameId);
        objChessGameVO.setCurrentUserId(userId);
        objChessGameVO.setCurrentHoldChess(objChessPlayer.getHoldChess());
        objChessGameVO.setCurrentTurn(objChessGame.getCurrentTurn());
        objChessGameVO.setChessPiecesList(lstChessPieces);
        return objChessGameVO;
    }
}
