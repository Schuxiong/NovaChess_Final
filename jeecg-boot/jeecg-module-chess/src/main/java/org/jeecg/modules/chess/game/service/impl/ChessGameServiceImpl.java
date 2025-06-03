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
import org.jeecg.modules.chess.game.vo.ChessGameWithScoreVO;
import org.jeecg.modules.chess.game.vo.PlayerPairVO;
import org.springframework.beans.BeanUtils;
import org.jeecg.modules.chess.score.entity.ChessPlayerScore;
import org.jeecg.modules.chess.score.service.IChessPlayerScoreService;
import org.jeecg.modules.chess.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 游戏
 * @Author: jeecg-boot
 * @Date: 2025-04-27
 * @Version: V1.0
 */
@Service
public class ChessGameServiceImpl extends ServiceImpl<ChessGameMapper, ChessGame> implements IChessGameService {
    private static final Logger log = LoggerFactory.getLogger(ChessGameServiceImpl.class);

    @Autowired
    private IChessPlayerService chessPlayerService;

    @Autowired
    private IChessPiecesService chessPiecesService;

    @Autowired
    private IChessPlayerScoreService chessPlayerScoreService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 添加一个简单的棋盘缓存
    private final Map<String, List<ChessPieces>> gameBoardCache = new ConcurrentHashMap<>();

    /**
     * 更新棋盘缓存
     * 
     * @param chessGameId 游戏ID
     * @param pieces      棋子列表
     */
    public void updateBoardCache(String chessGameId, List<ChessPieces> pieces) {
        if (chessGameId == null || pieces == null) {
            return;
        }

        log.info("更新游戏{}的棋盘缓存，包含{}个棋子", chessGameId, pieces.size());
        gameBoardCache.put(chessGameId, new ArrayList<>(pieces));
    }

    // 从缓存获取棋盘
    private List<ChessPieces> getBoardFromCache(String gameId) {
        List<ChessPieces> cachedPieces = gameBoardCache.get(gameId);
        if (cachedPieces != null && !cachedPieces.isEmpty()) {
            log.info("从缓存获取到游戏{}的{}个棋子", gameId, cachedPieces.size());
            return new ArrayList<>(cachedPieces);
        }
        return null;
    }

    // 清除特定游戏的缓存
    private void clearBoardCache(String gameId) {
        gameBoardCache.remove(gameId);
        log.info("已清除游戏{}的棋盘缓存", gameId);
    }

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
            // objSource represents the inviting player (parameter 1 from controller, who
            // initiated the game setup)
            // objTarget represents the accepting player (parameter 2 from controller, who
            // is the current user processing the invitation)
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

            // 更新棋盘缓存
            updateBoardCache(strChessGameId, lstChessPieces);

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
     * @param chessGameId 游戏ID
     * @param userId      用户ID
     * @return 游戏棋盘状态
     */
    @Override
    public ChessGameVO getChessGameChessPieces(String chessGameId, String userId) {
        return getChessGameChessPieces(chessGameId, userId, null);
    }

    /**
     * 获取当前棋子位置信息（带参数）
     * 
     * @param chessGameId 游戏ID
     * @param userId      用户ID
     * @param params      附加参数，如afterMove标志
     * @return 游戏棋盘状态
     */
    @Transactional
    @Override
    public ChessGameVO getChessGameChessPieces(String chessGameId, String userId, Map<String, Object> params) {
        // 使用同步块防止并发初始化
        synchronized (this) {
            try {
                boolean isAfterMove = params != null && Boolean.TRUE.equals(params.get("afterMove"));
                boolean skipInitialization = params != null && Boolean.TRUE.equals(params.get("skipInitialization"));

                log.info("开始获取游戏{}的棋盘信息, afterMove={}, skipInitialization={}",
                        chessGameId, isAfterMove, skipInitialization);

                // 优先尝试获取缓存
                List<ChessPieces> cachedPieces = getBoardFromCache(chessGameId);

                // 如果是移动后的请求，强制从数据库重新加载棋子
                if (isAfterMove) {
                    // 强制刷新缓存，从数据库获取最新数据
                    log.info("移动后的请求，强制从数据库加载最新棋子数据");
                    // 清除旧缓存
                    clearBoardCache(chessGameId);
                    // 设置缓存为空，强制查询数据库
                    cachedPieces = null;
                } else if (cachedPieces != null && !cachedPieces.isEmpty()) {
                    // 非移动后请求，可以使用有效缓存
                    log.info("使用缓存中的棋盘数据，跳过数据库查询，棋子数量: {}", cachedPieces.size());

                    // 获取玩家和游戏信息
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
                    objChessGameVO.setCurrentHoldChess(objChessPlayer != null ? objChessPlayer.getHoldChess() : null);
                    objChessGameVO.setCurrentTurn(objChessGame != null ? objChessGame.getCurrentTurn() : 2); // 默认白方先行
                    objChessGameVO.setChessPiecesList(cachedPieces);
                    return objChessGameVO;
                }

                // 查询数据库中的棋子
                QueryWrapper<ChessPieces> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("chess_game_id", chessGameId);
                // 放宽条件，先不过滤del_flag
                List<ChessPieces> allPieces = chessPiecesService.list(queryWrapper);

                log.info("查询到游戏{}的总棋子数量: {}", chessGameId, allPieces != null ? allPieces.size() : 0);

                // 如果找到棋子，过滤掉已删除的
                if (allPieces != null && !allPieces.isEmpty()) {
                    allPieces = allPieces.stream()
                            .filter(p -> p.getDelFlag() == null || p.getDelFlag() == 0)
                            .collect(java.util.stream.Collectors.toList());
                    log.info("过滤del_flag后的棋子数量: {}", allPieces.size());
                }

                // 如果是移动后的请求，即使没有查到棋子也不要初始化
                if ((allPieces == null || allPieces.isEmpty()) && !skipInitialization && !isAfterMove) {
                    log.info("棋盘上没有任何棋子，为游戏{}初始化新棋子", chessGameId);
                    allPieces = chessPiecesService.initPosition(chessGameId);
                    // 更新缓存
                    updateBoardCache(chessGameId, allPieces);
                } else if (allPieces != null && !allPieces.isEmpty()) {
                    // 过滤活跃棋子
                    List<ChessPieces> activePieces = allPieces.stream()
                            .filter(p -> p.getPiecesState() == 1)
                            .collect(java.util.stream.Collectors.toList());

                    log.info("游戏{}的活跃棋子数量: {}", chessGameId, activePieces.size());

                    // 如果没有活跃棋子但有非活跃棋子，激活它们
                    if (activePieces.isEmpty() && !skipInitialization) {
                        log.info("游戏{}没有活跃棋子，激活现有棋子", chessGameId);
                        for (ChessPieces piece : allPieces) {
                            piece.setPiecesState(1);
                            chessPiecesService.updateById(piece);
                        }
                        // 重新加载所有棋子作为活跃棋子
                        activePieces = new ArrayList<>(allPieces);
                    }

                    // 检查是否存在重复的活跃棋子
                    Map<String, List<ChessPieces>> piecesByPosition = new HashMap<>();
                    for (ChessPieces piece : activePieces) {
                        String position = piece.getPositionX() + piece.getPositionY();
                        if (!piecesByPosition.containsKey(position)) {
                            piecesByPosition.put(position, new ArrayList<>());
                        }
                        piecesByPosition.get(position).add(piece);
                    }

                    // 处理重复棋子
                    boolean hasDuplicates = false;
                    for (Map.Entry<String, List<ChessPieces>> entry : piecesByPosition.entrySet()) {
                        if (entry.getValue().size() > 1) {
                            hasDuplicates = true;
                            log.warn("位置{}发现{}个重复棋子", entry.getKey(), entry.getValue().size());

                            // 保留第一个，其余设为非活跃
                            for (int i = 1; i < entry.getValue().size(); i++) {
                                ChessPieces duplicatePiece = entry.getValue().get(i);
                                duplicatePiece.setPiecesState(0);
                                chessPiecesService.updateById(duplicatePiece);
                                log.info("设置重复棋子{}为非活跃", duplicatePiece.getId());
                            }
                        }
                    }

                    // 如果存在重复棋子，重新获取活跃棋子列表
                    if (hasDuplicates) {
                        QueryWrapper<ChessPieces> activeQuery = new QueryWrapper<>();
                        activeQuery.eq("chess_game_id", chessGameId);
                        activeQuery.eq("pieces_state", 1); // 活跃的棋子
                        activeQuery.eq("del_flag", 0);
                        activePieces = chessPiecesService.list(activeQuery);
                    }

                    // 设置返回的棋子列表为当前活跃的棋子
                    allPieces = activePieces;

                    // 更新缓存
                    updateBoardCache(chessGameId, allPieces);
                }

                // 如果所有查询后仍然没有棋子，但缓存有棋子，使用缓存数据
                if ((allPieces == null || allPieces.isEmpty()) && cachedPieces != null && !cachedPieces.isEmpty()) {
                    log.info("数据库查询无结果，使用缓存的{}个棋子", cachedPieces.size());
                    allPieces = cachedPieces;
                }

                // 打印部分棋子位置，用于调试
                log.info("返回活跃棋子数量: {}", allPieces != null ? allPieces.size() : 0);
                if (allPieces != null && !allPieces.isEmpty()) {
                    for (int i = 0; i < Math.min(5, allPieces.size()); i++) {
                        ChessPieces piece = allPieces.get(i);
                        log.info("棋子 {}: 类型={}, 位置={}{}",
                                piece.getId(), piece.getChessPiecesName(),
                                piece.getPositionX(), piece.getPositionY());
                    }
                }

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
                objChessGameVO.setCurrentHoldChess(objChessPlayer != null ? objChessPlayer.getHoldChess() : null);
                objChessGameVO.setCurrentTurn(objChessGame != null ? objChessGame.getCurrentTurn() : 2); // 默认白方先行
                objChessGameVO.setChessPiecesList(allPieces);
                return objChessGameVO;
            } catch (Exception e) {
                log.error("获取棋盘信息失败", e);
                ChessGameVO errorResult = new ChessGameVO();
                errorResult.setGameId(chessGameId);
                errorResult.setCurrentUserId(userId);
                errorResult.setErrorMessage("获取棋盘信息失败: " + e.getMessage());
                return errorResult;
            }
        }
    }

    @Override
    public IPage<ChessGameWithScoreVO> pageWithScore(Page<ChessGame> page, QueryWrapper<ChessGame> queryWrapper) {
        // 先查询游戏列表
        IPage<ChessGame> gamePageList = this.page(page, queryWrapper);
        
        // 创建返回结果
        Page<ChessGameWithScoreVO> resultPage = new Page<>(page.getCurrent(), page.getSize(), gamePageList.getTotal());
        
        List<ChessGameWithScoreVO> voList = new ArrayList<>();
        
        for (ChessGame game : gamePageList.getRecords()) {
            ChessGameWithScoreVO vo = new ChessGameWithScoreVO();
            // 复制基本属性
            BeanUtils.copyProperties(game, vo);
            
            // 查询黑方积分
            if (game.getBlackPlayId() != null) {
                QueryWrapper<ChessPlayerScore> blackScoreQuery = new QueryWrapper<>();
                blackScoreQuery.eq("user_id", game.getBlackPlayId());
                ChessPlayerScore blackScore = chessPlayerScoreService.getOne(blackScoreQuery);
                vo.setBlackPlayerScore(blackScore != null ? blackScore.getScore() : 600);
            }
            
            // 查询白方积分
            if (game.getWhitePlayId() != null) {
                QueryWrapper<ChessPlayerScore> whiteScoreQuery = new QueryWrapper<>();
                whiteScoreQuery.eq("user_id", game.getWhitePlayId());
                ChessPlayerScore whiteScore = chessPlayerScoreService.getOne(whiteScoreQuery);
                vo.setWhitePlayerScore(whiteScore != null ? whiteScore.getScore() : 600);
            }
            
            voList.add(vo);
        }
        
        resultPage.setRecords(voList);
        return resultPage;
    }
}
