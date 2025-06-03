package org.jeecg.modules.chess.game.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.chess.game.entity.ChessGame;
import org.jeecg.modules.chess.game.entity.ChessMove;
import org.jeecg.modules.chess.game.entity.ChessPieces;
import org.jeecg.modules.chess.game.entity.ChessPlayer;
import org.jeecg.modules.chess.game.mapper.ChessMoveMapper;
import org.jeecg.modules.chess.game.service.IChessGameService;
import org.jeecg.modules.chess.game.service.IChessMoveService;
import org.jeecg.modules.chess.game.service.IChessPiecesService;
import org.jeecg.modules.chess.game.service.IChessPlayerService;
import org.jeecg.modules.chess.game.vo.ChatChessMoveRequestVO;
import org.jeecg.modules.chess.game.vo.ChessGameVO;
import org.jeecg.modules.chess.game.vo.ChessMoveResponseVO;
import org.jeecg.modules.chess.game.vo.ChessPiecesResponseVO;
import org.jeecg.modules.chess.game.vo.ChessPiecesVO;
import org.jeecg.modules.chess.score.entity.ChessPlayerScore;
import org.jeecg.modules.chess.score.entity.ChessPlayerScoreRecord;
import org.jeecg.modules.chess.score.service.IChessPlayerScoreRecordService;
import org.jeecg.modules.chess.score.service.IChessPlayerScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 行棋
 * @Author: jeecg-boot
 * @Date: 2025-04-27
 * @Version: V1.0
 */
@Slf4j
@Service
public class ChessMoveServiceImpl extends ServiceImpl<ChessMoveMapper, ChessMove> implements IChessMoveService {

    @Autowired
    private IChessPiecesService chessPiecesService;

    @Autowired
    private IChessGameService chessGameService;

    @Autowired
    private IChessPlayerService chessPlayerService;

    @Autowired
    private IChessPlayerScoreService chessPlayerScoreService;

    @Autowired
    private IChessPlayerScoreRecordService chessPlayerScoreRecordService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    @Override
    public ChessPiecesVO movePieces(ChessMove chessMove) {
        ChessPiecesVO objChessPiecesVO = new ChessPiecesVO();
        try {
            // 首先清理棋盘上的重复棋子
            cleanupDuplicatePieces(chessMove.getChessGameId());

            // 获取当前游戏
            ChessGame objChessGame = chessGameService.getById(chessMove.getChessGameId());

            // 获取棋手信息
            QueryWrapper<ChessPlayer> queryPlayerWrapper = new QueryWrapper<>();
            queryPlayerWrapper.eq("chess_game_id", chessMove.getChessGameId());
            queryPlayerWrapper.eq("user_id", chessMove.getUserId());
            ChessPlayer objChessPlayer = chessPlayerService.getOne(queryPlayerWrapper);

            // 检查是否轮到该玩家行棋
            if (objChessPlayer.getHoldChess() != objChessGame.getCurrentTurn()) {
                objChessPiecesVO.setErrorMessage("不是您的回合，请等待对手下棋");
                return objChessPiecesVO;
            }

            // 获取要移动的棋子
            QueryWrapper<ChessPieces> fromQueryWrapper = new QueryWrapper<>();
            fromQueryWrapper.eq("chess_game_id", chessMove.getChessGameId());
            fromQueryWrapper.eq("id", chessMove.getChessPiecesId());
            ChessPieces objChessPieces = chessPiecesService.getOne(fromQueryWrapper);

            if (objChessPieces == null) {
                objChessPiecesVO.setErrorMessage("未找到要移动的棋子");
                return objChessPiecesVO;
            }

            // 前端已实现完整的移动验证，跳过后端验证
            log.info("跳过后端移动验证，允许 {} 从({},{})移动到({},{})",
                    objChessPieces.getChessPiecesName(),
                    objChessPieces.getPositionX(), objChessPieces.getPositionY(),
                    chessMove.getToPositionX(), chessMove.getToPositionY());

            // 检查目标位置是否有棋子
            QueryWrapper<ChessPieces> targetQueryWrapper = new QueryWrapper<>();
            targetQueryWrapper.eq("chess_game_id", chessMove.getChessGameId());
            targetQueryWrapper.eq("position_x", chessMove.getToPositionX());
            targetQueryWrapper.eq("position_y", chessMove.getToPositionY());
            targetQueryWrapper.eq("pieces_state", 1); // 活跃的棋子
            ChessPieces targetPiece = chessPiecesService.getOne(targetQueryWrapper);

            // 如果目标位置有棋子
            if (targetPiece != null) {
                // 不能吃自己的棋子
                if (targetPiece.getPiecesType() == objChessPieces.getPiecesType()) {
                    objChessPiecesVO.setErrorMessage("不能吃掉自己的棋子");
                    return objChessPiecesVO;
                }

                // 可以吃对方的棋子
                if (targetPiece.getChessPiecesName().equalsIgnoreCase("king")) {
                    // 如果吃掉的是王，则赢了
                    winChess(chessMove);
                }

                // 标记目标棋子为已被吃
                targetPiece.setPiecesState(2);
                boolean targetUpdateResult = chessPiecesService.updateById(targetPiece);
                if (!targetUpdateResult) {
                    log.error("更新被吃棋子状态失败");
                }

                // 记录被吃的棋子ID
                chessMove.setTookPiecesId(targetPiece.getId());
                objChessPiecesVO.setTargetChessPiecesId(targetPiece.getId());
            }

            // 备份原始位置，用于记录和验证
            String originalX = objChessPieces.getPositionX();
            String originalY = objChessPieces.getPositionY();

            // 更新棋子位置
            objChessPieces.setPositionX(chessMove.getToPositionX());
            objChessPieces.setPositionY(chessMove.getToPositionY());
            log.info("正在更新棋子位置: ID={}, 从 {} -> {}",
                    objChessPieces.getId(),
                    chessMove.getFromPositionX() + chessMove.getFromPositionY(),
                    chessMove.getToPositionX() + chessMove.getToPositionY());

            boolean updateResult = chessPiecesService.updateById(objChessPieces);
            if (!updateResult) {
                log.error("更新棋子位置失败");
                objChessPiecesVO.setErrorMessage("服务器无法更新棋子位置");
                return objChessPiecesVO;
            }
            log.info("棋子位置更新成功");

            // 移动棋子后，确保同ID棋子只在目标位置活跃，清理同ID在其他位置的活跃棋子
            QueryWrapper<ChessPieces> duplicateQuery = new QueryWrapper<>();
            duplicateQuery.eq("chess_game_id", objChessPieces.getChessGameId());
            duplicateQuery.eq("id", objChessPieces.getId());
            duplicateQuery.ne("position_x", chessMove.getToPositionX());
            duplicateQuery.ne("position_y", chessMove.getToPositionY());
            duplicateQuery.eq("pieces_state", 1);
            List<ChessPieces> duplicates = chessPiecesService.list(duplicateQuery);
            for (ChessPieces dup : duplicates) {
                dup.setPiecesState(0);
                chessPiecesService.updateById(dup);
                log.info("清理同ID棋子: 设置棋子 {} (原位置: {}{}) 为非活跃", dup.getId(), dup.getPositionX(), dup.getPositionY());
            }

            // 验证更新是否成功
            ChessPieces verifiedPiece = chessPiecesService.getById(objChessPieces.getId());
            if (verifiedPiece != null) {
                if (!verifiedPiece.getPositionX().equals(chessMove.getToPositionX()) ||
                        !verifiedPiece.getPositionY().equals(chessMove.getToPositionY())) {

                    log.error("验证失败：棋子ID={}位置未更新，当前={}{}, 期望={}{}",
                            verifiedPiece.getId(),
                            verifiedPiece.getPositionX(), verifiedPiece.getPositionY(),
                            chessMove.getToPositionX(), chessMove.getToPositionY());

                    // 尝试再次更新位置
                    verifiedPiece.setPositionX(chessMove.getToPositionX());
                    verifiedPiece.setPositionY(chessMove.getToPositionY());
                    chessPiecesService.updateById(verifiedPiece);
                } else {
                    log.info("验证成功：棋子ID={}位置已正确更新到{}{}",
                            verifiedPiece.getId(),
                            verifiedPiece.getPositionX(), verifiedPiece.getPositionY());
                }
            }

            // 保存移动记录
            this.save(chessMove);

            // 更新当前轮次
            if (objChessGame.getCurrentTurn() == 1) {
                objChessGame.setCurrentTurn(2); // 切换到白方
            } else {
                objChessGame.setCurrentTurn(1); // 切换到黑方
            }
            chessGameService.updateById(objChessGame);

            // 获取并更新棋盘缓存
            QueryWrapper<ChessPieces> allPiecesQuery = new QueryWrapper<>();
            allPiecesQuery.eq("chess_game_id", chessMove.getChessGameId());
            allPiecesQuery.eq("pieces_state", 1); // 只获取活跃的棋子
            allPiecesQuery.eq("del_flag", 0);
            List<ChessPieces> updatedPieces = chessPiecesService.list(allPiecesQuery);

            if (updatedPieces != null && !updatedPieces.isEmpty()) {
                log.info("更新棋盘缓存，包含{}个棋子", updatedPieces.size());
                // 调用GameService的缓存更新方法
                chessGameService.updateBoardCache(chessMove.getChessGameId(), updatedPieces);
            } else {
                log.warn("无法更新棋盘缓存：未找到活跃棋子");
            }

            // 设置返回值
            objChessPiecesVO.setChessGameId(chessMove.getChessGameId());
            objChessPiecesVO.setPositionX(chessMove.getToPositionX());
            objChessPiecesVO.setPositionY(chessMove.getToPositionY());
            objChessPiecesVO.setCurrentTurn(objChessGame.getCurrentTurn());

        } catch (Exception e) {
            log.error("棋子移动错误：", e);
            objChessPiecesVO.setErrorMessage("服务器处理棋子移动时发生错误");
        }
        return objChessPiecesVO;
    }

    /**
     * 验证移动是否符合棋子的规则
     * 
     * @param piece 要移动的棋子
     * @param toX   目标X坐标
     * @param toY   目标Y坐标
     * @return 移动是否有效
     */
    private boolean isValidMove(ChessPieces piece, String toX, String toY) {
        // 前端已实现完整的移动验证，后端不再重复验证
        log.info("跳过后端移动验证，信任前端验证结果");
        return true;
    }

    /**
     * 赢棋
     * 
     * @param chessMove
     */
    @Transactional
    private void winChess(ChessMove chessMove) {
        try {
            ChessGame objChessGame = chessGameService.getById(chessMove.getChessGameId());
            if (objChessGame == null) {
                log.error("游戏结束处理失败：未找到游戏 {}", chessMove.getChessGameId());
                return;
            }

            objChessGame.setGameState(2); // 设置游戏结束状态
            chessGameService.updateById(objChessGame);

            // 正确获取棋手信息 - 使用查询而不是直接getById
            QueryWrapper<ChessPlayer> blackPlayerQuery = new QueryWrapper<>();
            blackPlayerQuery.eq("chess_game_id", chessMove.getChessGameId());
            blackPlayerQuery.eq("user_id", objChessGame.getBlackPlayId());
            ChessPlayer objBlackChessPlayer = chessPlayerService.getOne(blackPlayerQuery);

            QueryWrapper<ChessPlayer> whitePlayerQuery = new QueryWrapper<>();
            whitePlayerQuery.eq("chess_game_id", chessMove.getChessGameId());
            whitePlayerQuery.eq("user_id", objChessGame.getWhitePlayId());
            ChessPlayer objWhiteChessPlayer = chessPlayerService.getOne(whitePlayerQuery);

            // 添加安全检查
            if (objBlackChessPlayer == null || objWhiteChessPlayer == null) {
                log.error("游戏结束处理失败：未找到玩家信息，黑方ID: {}, 白方ID: {}",
                        objChessGame.getBlackPlayId(), objChessGame.getWhitePlayId());
                return;
            }

            // 获取玩家分数
            QueryWrapper<ChessPlayerScore> blackScoreQuery = new QueryWrapper<>();
            blackScoreQuery.eq("user_id", objBlackChessPlayer.getUserId());
            // 添加false参数，表示找到多个结果时不抛出异常，返回第一个结果
            ChessPlayerScore objBlackScore = chessPlayerScoreService.getOne(blackScoreQuery, false);

            QueryWrapper<ChessPlayerScore> whiteScoreQuery = new QueryWrapper<>();
            whiteScoreQuery.eq("user_id", objWhiteChessPlayer.getUserId());
            // 添加false参数，表示找到多个结果时不抛出异常，返回第一个结果
            ChessPlayerScore objWhiteScore = chessPlayerScoreService.getOne(whiteScoreQuery, false);

            // 安全检查分数记录
            if (objBlackScore == null || objWhiteScore == null) {
                log.warn("未找到玩家分数记录，将创建新记录");
                if (objBlackScore == null) {
                    objBlackScore = new ChessPlayerScore();
                    objBlackScore.setId(UUIDGenerator.generate());
                    objBlackScore.setUserId(objBlackChessPlayer.getUserId());
                    objBlackScore.setUserAccount(objBlackChessPlayer.getUserAccount());
                    objBlackScore.setScore(0);
                    objBlackScore.setDelFlag(0);
                }

                if (objWhiteScore == null) {
                    objWhiteScore = new ChessPlayerScore();
                    objWhiteScore.setId(UUIDGenerator.generate());
                    objWhiteScore.setUserId(objWhiteChessPlayer.getUserId());
                    objWhiteScore.setUserAccount(objWhiteChessPlayer.getUserAccount());
                    objWhiteScore.setScore(0);
                    objWhiteScore.setDelFlag(0);
                }
            }

            // 创建分数变更记录
            ChessPlayerScoreRecord objBlackChessPlayerScoreRecord = new ChessPlayerScoreRecord();
            objBlackChessPlayerScoreRecord.setId(UUIDGenerator.generate());
            objBlackChessPlayerScoreRecord.setChessGameId(chessMove.getChessGameId());
            objBlackChessPlayerScoreRecord.setChessPlayerId(objBlackChessPlayer.getId());

            ChessPlayerScoreRecord objWhiteChessPlayerScoreRecord = new ChessPlayerScoreRecord();
            objWhiteChessPlayerScoreRecord.setId(UUIDGenerator.generate());
            objWhiteChessPlayerScoreRecord.setChessGameId(chessMove.getChessGameId());
            objWhiteChessPlayerScoreRecord.setChessPlayerId(objWhiteChessPlayer.getId());

            // 更新分数（黑方胜利或白方胜利）
            String winnerSide;
            if (chessMove.getPiecesType() == 1) {
                // 黑方胜利
                objBlackScore.setScore(objBlackScore.getScore() + 30);
                objBlackChessPlayerScoreRecord.setScore(30);
                objWhiteScore.setScore((objWhiteScore.getScore() - 30) > 0 ? (objWhiteScore.getScore() - 30) : 0);
                objWhiteChessPlayerScoreRecord.setScore(-30);
                winnerSide = "黑方";
            } else {
                // 白方胜利
                objWhiteScore.setScore(objWhiteScore.getScore() + 30);
                objWhiteChessPlayerScoreRecord.setScore(30);
                objBlackScore.setScore((objBlackScore.getScore() - 30) > 0 ? (objBlackScore.getScore() - 30) : 0);
                objBlackChessPlayerScoreRecord.setScore(-30);
                winnerSide = "白方";
            }

            // 保存分数记录
            chessPlayerScoreService.saveOrUpdate(objBlackScore);
            chessPlayerScoreService.saveOrUpdate(objWhiteScore);
            chessPlayerScoreRecordService.save(objBlackChessPlayerScoreRecord);
            chessPlayerScoreRecordService.save(objWhiteChessPlayerScoreRecord);

            log.info("游戏{}结束，{} 获胜", chessMove.getChessGameId(), winnerSide);

            // 如果有注入SimpMessagingTemplate，发送游戏结束消息
            if (messagingTemplate != null) {
                try {
                    Map<String, Object> gameOverMessage = new HashMap<>();
                    gameOverMessage.put("type", "GAME_OVER");
                    gameOverMessage.put("payload", new HashMap<String, Object>() {
                        {
                            put("winner", chessMove.getPiecesType() == 1 ? "BLACK" : "WHITE");
                            put("reason", "CHECKMATE");
                            put("gameId", chessMove.getChessGameId());
                        }
                    });

                    messagingTemplate.convertAndSend("/topic/game/" + chessMove.getChessGameId(), gameOverMessage);
                    log.info("游戏结束消息已发送，游戏ID: {}, 获胜方: {}",
                            chessMove.getChessGameId(), winnerSide);
                } catch (Exception e) {
                    log.error("发送游戏结束消息时出错: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("处理游戏结束时发生错误", e);
        }
    }

    @Transactional
    @Override
    public ChessMoveResponseVO chessMovePieces(ChatChessMoveRequestVO chatChessMoveRequestVO) {
        ChessMoveResponseVO objChessMoveResponseVO = new ChessMoveResponseVO();
        try {
            log.info("收到移动请求: {}", chatChessMoveRequestVO);

            // 检查 chessGameId/gameId 是否存在
            String gameId = chatChessMoveRequestVO.getGameId();
            if (gameId == null || gameId.isEmpty()) {
                gameId = chatChessMoveRequestVO.getChessGameId();
            }
            if (gameId == null || gameId.isEmpty()) {
                log.error("chessMovePieces error: Game ID is missing");
                objChessMoveResponseVO.setSuccess(false);
                return objChessMoveResponseVO;
            }

            // 检查 userId 是否存在
            if (chatChessMoveRequestVO.getUserId() == null || chatChessMoveRequestVO.getUserId().isEmpty()) {
                log.error("chessMovePieces error: User ID is missing for game {}", gameId);
                objChessMoveResponseVO.setSuccess(false);
                return objChessMoveResponseVO;
            }

            // 获取当前游戏
            ChessGame objChessGame = chessGameService.getById(gameId);
            if (objChessGame == null) {
                log.error("chessMovePieces error: Game {} not found", gameId);
                objChessMoveResponseVO.setSuccess(false);
                return objChessMoveResponseVO;
            }

            // 获取棋手信息
            QueryWrapper<ChessPlayer> queryPlayerWrapper = new QueryWrapper<>();
            queryPlayerWrapper.eq("chess_game_id", gameId);
            queryPlayerWrapper.eq("user_id", chatChessMoveRequestVO.getUserId());
            ChessPlayer objChessPlayer = chessPlayerService.getOne(queryPlayerWrapper);

            if (objChessPlayer == null) {
                log.error("chessMovePieces error: Player not found for game {} and user {}",
                        gameId, chatChessMoveRequestVO.getUserId());
                objChessMoveResponseVO.setSuccess(false);
                return objChessMoveResponseVO;
            }

            // 检查是否轮到该玩家行棋
            if (objChessPlayer.getHoldChess() != objChessGame.getCurrentTurn()) {
                log.warn("chessMovePieces: Not player's turn. Player: {}, Current turn: {}",
                        objChessPlayer.getHoldChess(), objChessGame.getCurrentTurn());
                objChessMoveResponseVO.setSuccess(false);
                return objChessMoveResponseVO;
            }

            // 清理棋盘上重复的棋子。对于同一位置有多个活跃棋子的情况，保留一个，将其余状态设置为非活跃。
            cleanupDuplicatePieces(gameId);

            // 处理坐标转换，支持两种格式：
            // 1. fromPosition="F2", toPosition="F4" 的整体坐标格式
            // 2. fromPositionX="F", fromPositionY="2", toPositionX="F", toPositionY="4"
            // 的分离坐标格式
            String fromPositionX = chatChessMoveRequestVO.getFromPositionX();
            String fromPositionY = chatChessMoveRequestVO.getFromPositionY();
            String toPositionX = chatChessMoveRequestVO.getToPositionX();
            String toPositionY = chatChessMoveRequestVO.getToPositionY();

            // 如果提供了完整的位置格式 (如 "F2")，解析它
            if ((fromPositionX == null || fromPositionY == null) && chatChessMoveRequestVO.getFromPosition() != null) {
                String fromPos = chatChessMoveRequestVO.getFromPosition().toUpperCase();
                if (fromPos.length() >= 2) {
                    fromPositionX = fromPos.substring(0, 1);
                    fromPositionY = fromPos.substring(1, 2);
                    log.info("解析起始位置: {} -> X={}, Y={}", fromPos, fromPositionX, fromPositionY);
                }
            }

            if ((toPositionX == null || toPositionY == null) && chatChessMoveRequestVO.getToPosition() != null) {
                String toPos = chatChessMoveRequestVO.getToPosition().toUpperCase();
                if (toPos.length() >= 2) {
                    toPositionX = toPos.substring(0, 1);
                    toPositionY = toPos.substring(1, 2);
                    log.info("解析目标位置: {} -> X={}, Y={}", toPos, toPositionX, toPositionY);
                }
            }

            if (fromPositionX == null || fromPositionY == null || toPositionX == null || toPositionY == null) {
                log.error("chessMovePieces error: Invalid positions - from({},{}) to({},{})",
                        fromPositionX, fromPositionY, toPositionX, toPositionY);
                objChessMoveResponseVO.setSuccess(false);
                return objChessMoveResponseVO;
            }

            // 查找起始位置的棋子
            QueryWrapper<ChessPieces> fromQueryWrapper = new QueryWrapper<>();
            fromQueryWrapper.eq("chess_game_id", gameId);
            fromQueryWrapper.eq("position_x", fromPositionX);
            fromQueryWrapper.eq("position_y", fromPositionY);
            fromQueryWrapper.eq("pieces_state", 1); // 活跃的棋子
            // 使用list()代替getOne()来避免TooManyResultsException异常
            List<ChessPieces> fromPieceList = chessPiecesService.list(fromQueryWrapper);

            if (fromPieceList == null || fromPieceList.isEmpty()) {
                log.error("chessMovePieces error: No piece found at position ({},{})", fromPositionX, fromPositionY);
                objChessMoveResponseVO.setSuccess(false);
                objChessMoveResponseVO.setErrorMessage("未找到指定位置的棋子");
                return objChessMoveResponseVO;
            }

            // 如果发现多个棋子，记录警告并使用第一个
            if (fromPieceList.size() > 1) {
                log.warn("chessMovePieces warning: Found {} pieces at position ({},{}), using the first one",
                        fromPieceList.size(), fromPositionX, fromPositionY);
            }

            ChessPieces fromPiece = fromPieceList.get(0);

            // 确保玩家只能移动自己颜色的棋子
            if (fromPiece.getPiecesType() != objChessPlayer.getHoldChess()) {
                log.error("chessMovePieces error: Player {} cannot move piece of type {}",
                        objChessPlayer.getHoldChess(), fromPiece.getPiecesType());
                objChessMoveResponseVO.setSuccess(false);
                return objChessMoveResponseVO;
            }

            // 将WebSocket请求转换为ChessMove对象
            ChessMove chessMove = new ChessMove();
            chessMove.setId(UUIDGenerator.generate());
            chessMove.setChessGameId(gameId);
            chessMove.setChessPiecesId(fromPiece.getId());
            chessMove.setFromPositionX(fromPositionX);
            chessMove.setFromPositionY(fromPositionY);
            chessMove.setToPositionX(toPositionX);
            chessMove.setToPositionY(toPositionY);
            chessMove.setPiecesType(fromPiece.getPiecesType());
            chessMove.setUserId(chatChessMoveRequestVO.getUserId());
            
            // 设置必要的数据库字段
            chessMove.setCreateTime(new java.util.Date());
            chessMove.setCreateBy(chatChessMoveRequestVO.getUserId());
            chessMove.setUpdateTime(new java.util.Date());
            chessMove.setUpdateBy(chatChessMoveRequestVO.getUserId());
            chessMove.setDelFlag(0); // 确保不被删除标记过滤
            
            // 设置前端传递的走棋顺序和时间
            if (chatChessMoveRequestVO.getMoveSequence() != null) {
                chessMove.setMoveSequence(chatChessMoveRequestVO.getMoveSequence());
            }
            if (chatChessMoveRequestVO.getMoveDurationSeconds() != null) {
                chessMove.setMoveDurationSeconds(chatChessMoveRequestVO.getMoveDurationSeconds());
            }

            log.info("执行移动: 从({},{})到({},{}), 棋子ID: {}, 类型: {}",
                    fromPositionX, fromPositionY, toPositionX, toPositionY,
                    fromPiece.getId(), fromPiece.getChessPiecesName());

            // 执行移动棋子操作
            ChessPiecesVO chessPiecesVO = this.movePieces(chessMove, chatChessMoveRequestVO.getPromotionPieceType());

            if (chessPiecesVO.getErrorMessage() != null && !chessPiecesVO.getErrorMessage().isEmpty()) {
                log.warn("棋子移动失败: {}", chessPiecesVO.getErrorMessage());
                objChessMoveResponseVO.setSuccess(false);
                objChessMoveResponseVO.setErrorMessage(chessPiecesVO.getErrorMessage());
                return objChessMoveResponseVO;
            }

            // 给系统一点时间确保数据库更新完成
            try {
                log.info("等待数据库更新同步...");
                Thread.sleep(200); // 200毫秒的小延迟，确保数据库写入完成
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 验证移动是否成功，通过检查移动后的棋子
            ChessPieces movedPiece = chessPiecesService.getById(fromPiece.getId());
            if (movedPiece != null) {
                log.info("验证移动后的棋子: ID={}, 位置={}{}, 期望位置={}{}",
                        movedPiece.getId(),
                        movedPiece.getPositionX(), movedPiece.getPositionY(),
                        toPositionX, toPositionY);

                // 检查位置是否已经更新到目标位置
                if (!movedPiece.getPositionX().equals(toPositionX) || !movedPiece.getPositionY().equals(toPositionY)) {
                    log.warn("棋子位置未正确更新: 当前={}{}, 期望={}{}",
                            movedPiece.getPositionX(), movedPiece.getPositionY(),
                            toPositionX, toPositionY);
                }
            } else {
                log.error("无法验证移动，找不到棋子ID: {}", fromPiece.getId());
            }

            // 获取最新的游戏状态，并明确传递afterMove标记
            Map<String, Object> params = new HashMap<>();
            params.put("afterMove", true);
            log.info("获取移动后的游戏状态，已设置afterMove=true标记");
            ChessGameVO latestGameState = chessGameService.getChessGameChessPieces(
                    gameId,
                    chatChessMoveRequestVO.getUserId(),
                    params);

            objChessMoveResponseVO.setLatestGameState(latestGameState);
            objChessMoveResponseVO.setSuccess(true);

            // 获取并设置走棋历史
            QueryWrapper<ChessMove> moveHistoryQuery = new QueryWrapper<>();
            moveHistoryQuery.eq("chess_game_id", gameId);
            moveHistoryQuery.eq("del_flag", 0);
            moveHistoryQuery.orderByAsc("create_time"); // 按创建时间升序排列
            List<ChessMove> moveHistory = this.list(moveHistoryQuery);
            objChessMoveResponseVO.setMoveHistory(moveHistory);
            log.info("设置走棋历史，共{}条记录", moveHistory != null ? moveHistory.size() : 0);

            // 记录返回的棋子状态信息
            if (latestGameState != null && latestGameState.getChessPiecesList() != null) {
                log.info("移动后返回棋子数量: {}", latestGameState.getChessPiecesList().size());
                // 查找并记录我们移动的棋子的最终位置
                latestGameState.getChessPiecesList().stream()
                        .filter(p -> p.getId().equals(fromPiece.getId()))
                        .findFirst()
                        .ifPresent(piece -> log.info("移动的棋子 {} 当前位置: {}{}",
                                piece.getId(), piece.getPositionX(), piece.getPositionY()));
            }

            return objChessMoveResponseVO;
        } catch (Exception e) {
            log.error("WebSocket移动棋子错误：", e);
            objChessMoveResponseVO.setSuccess(false);
            objChessMoveResponseVO.setErrorMessage("服务器处理棋子移动时发生错误");
            return objChessMoveResponseVO;
        }
    }

    /**
     * 清理棋盘上重复的棋子。对于同一位置有多个活跃棋子的情况，保留一个，将其余状态设置为非活跃。
     *
     * @param gameId 游戏ID
     */
    @Transactional
    private void cleanupDuplicatePieces(String gameId) {
        try {
            // 获取游戏中所有活跃的棋子
            QueryWrapper<ChessPieces> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("chess_game_id", gameId);
            queryWrapper.eq("pieces_state", 1); // 活跃的棋子
            List<ChessPieces> allPieces = chessPiecesService.list(queryWrapper);

            // 使用Map保存每个位置的棋子
            Map<String, List<ChessPieces>> piecesByPosition = new HashMap<>();

            // 将棋子按位置分组
            for (ChessPieces piece : allPieces) {
                String position = piece.getPositionX() + piece.getPositionY();
                if (!piecesByPosition.containsKey(position)) {
                    piecesByPosition.put(position, new ArrayList<>());
                }
                piecesByPosition.get(position).add(piece);
            }

            // 处理每个有多个棋子的位置
            for (Map.Entry<String, List<ChessPieces>> entry : piecesByPosition.entrySet()) {
                List<ChessPieces> piecesAtPosition = entry.getValue();
                if (piecesAtPosition.size() > 1) {
                    log.warn("清理棋盘: 在位置 {} 发现 {} 个棋子", entry.getKey(), piecesAtPosition.size());

                    // 保留第一个棋子，将其余设为非活跃
                    for (int i = 1; i < piecesAtPosition.size(); i++) {
                        ChessPieces duplicatePiece = piecesAtPosition.get(i);
                        duplicatePiece.setPiecesState(0); // 设为非活跃
                        chessPiecesService.updateById(duplicatePiece);
                        log.info("清理棋盘: 设置棋子 {} (类型: {}) 为非活跃",
                                duplicatePiece.getId(), duplicatePiece.getChessPiecesName());
                    }
                }
            }

            // 检查棋子的状态是否正确
            for (ChessPieces piece : allPieces) {
                // 确保棋子状态为活跃
                if (piece.getPiecesState() != 1) {
                    piece.setPiecesState(1);
                    chessPiecesService.updateById(piece);
                    log.info("修正棋子 {} 的状态为活跃", piece.getId());
                }
            }
        } catch (Exception e) {
            log.error("清理重复棋子时出错", e);
        }
    }

    // 新增重载方法，支持升变
    @Transactional
    public ChessPiecesVO movePieces(ChessMove chessMove, String promotionPieceType) {
        ChessPiecesVO objChessPiecesVO = new ChessPiecesVO();
        try {
            // 首先清理棋盘上的重复棋子
            cleanupDuplicatePieces(chessMove.getChessGameId());

            // 获取当前游戏
            ChessGame objChessGame = chessGameService.getById(chessMove.getChessGameId());

            // 获取棋手信息
            QueryWrapper<ChessPlayer> queryPlayerWrapper = new QueryWrapper<>();
            queryPlayerWrapper.eq("chess_game_id", chessMove.getChessGameId());
            queryPlayerWrapper.eq("user_id", chessMove.getUserId());
            ChessPlayer objChessPlayer = chessPlayerService.getOne(queryPlayerWrapper);

            // 检查是否轮到该玩家行棋
            if (objChessPlayer.getHoldChess() != objChessGame.getCurrentTurn()) {
                objChessPiecesVO.setErrorMessage("不是您的回合，请等待对手下棋");
                return objChessPiecesVO;
            }

            // 获取要移动的棋子
            QueryWrapper<ChessPieces> fromQueryWrapper = new QueryWrapper<>();
            fromQueryWrapper.eq("chess_game_id", chessMove.getChessGameId());
            fromQueryWrapper.eq("id", chessMove.getChessPiecesId());
            ChessPieces objChessPieces = chessPiecesService.getOne(fromQueryWrapper);

            if (objChessPieces == null) {
                objChessPiecesVO.setErrorMessage("未找到要移动的棋子");
                return objChessPiecesVO;
            }

            // 前端已实现完整的移动验证，跳过后端验证
            log.info("跳过后端移动验证，允许 {} 从({},{})移动到({},{})",
                    objChessPieces.getChessPiecesName(),
                    objChessPieces.getPositionX(), objChessPieces.getPositionY(),
                    chessMove.getToPositionX(), chessMove.getToPositionY());

            // 检查目标位置是否有棋子
            QueryWrapper<ChessPieces> targetQueryWrapper = new QueryWrapper<>();
            targetQueryWrapper.eq("chess_game_id", chessMove.getChessGameId());
            targetQueryWrapper.eq("position_x", chessMove.getToPositionX());
            targetQueryWrapper.eq("position_y", chessMove.getToPositionY());
            targetQueryWrapper.eq("pieces_state", 1); // 活跃的棋子
            ChessPieces targetPiece = chessPiecesService.getOne(targetQueryWrapper);

            // 如果目标位置有棋子
            if (targetPiece != null) {
                // 不能吃自己的棋子
                if (targetPiece.getPiecesType() == objChessPieces.getPiecesType()) {
                    objChessPiecesVO.setErrorMessage("不能吃掉自己的棋子");
                    return objChessPiecesVO;
                }

                // 可以吃对方的棋子
                if (targetPiece.getChessPiecesName().equalsIgnoreCase("king")) {
                    // 如果吃掉的是王，则赢了
                    winChess(chessMove);
                }

                // 标记目标棋子为已被吃
                targetPiece.setPiecesState(2);
                boolean targetUpdateResult = chessPiecesService.updateById(targetPiece);
                if (!targetUpdateResult) {
                    log.error("更新被吃棋子状态失败");
                }

                // 记录被吃的棋子ID
                chessMove.setTookPiecesId(targetPiece.getId());
                objChessPiecesVO.setTargetChessPiecesId(targetPiece.getId());
            }

            // 备份原始位置，用于记录和验证
            String originalX = objChessPieces.getPositionX();
            String originalY = objChessPieces.getPositionY();

            // 更新棋子位置
            objChessPieces.setPositionX(chessMove.getToPositionX());
            objChessPieces.setPositionY(chessMove.getToPositionY());
            log.info("正在更新棋子位置: ID={}, 从 {} -> {}",
                    objChessPieces.getId(),
                    chessMove.getFromPositionX() + chessMove.getFromPositionY(),
                    chessMove.getToPositionX() + chessMove.getToPositionY());

            boolean updateResult = chessPiecesService.updateById(objChessPieces);
            if (!updateResult) {
                log.error("更新棋子位置失败");
                objChessPiecesVO.setErrorMessage("服务器无法更新棋子位置");
                return objChessPiecesVO;
            }
            log.info("棋子位置更新成功");

            // 升变处理（必须在棋子移动到目标位置后）
            if (promotionPieceType != null && !promotionPieceType.isEmpty()) {
                if (promotionPieceType.equalsIgnoreCase("QUEEN") || promotionPieceType.equalsIgnoreCase("ROOK")
                        || promotionPieceType.equalsIgnoreCase("BISHOP")
                        || promotionPieceType.equalsIgnoreCase("KNIGHT")) {
                    objChessPieces.setChessPiecesName(
                            promotionPieceType.substring(0, 1).toUpperCase()
                                    + promotionPieceType.substring(1).toLowerCase());
                    boolean promotionUpdateResult = chessPiecesService.updateById(objChessPieces);
                    if (!promotionUpdateResult) {
                        log.error("升变后更新棋子类型失败");
                    } else {
                        log.info("升变成功：棋子ID={} 升变为{}", objChessPieces.getId(), objChessPieces.getChessPiecesName());
                    }
                } else {
                    log.warn("不支持的升变类型: {}", promotionPieceType);
                }
            }

            // 移动和升变后，确保同ID棋子只在目标位置活跃，清理同ID在其他位置的活跃棋子
            QueryWrapper<ChessPieces> duplicateQuery = new QueryWrapper<>();
            duplicateQuery.eq("chess_game_id", objChessPieces.getChessGameId());
            duplicateQuery.eq("id", objChessPieces.getId());
            duplicateQuery.ne("position_x", objChessPieces.getPositionX());
            duplicateQuery.ne("position_y", objChessPieces.getPositionY());
            duplicateQuery.eq("pieces_state", 1);
            List<ChessPieces> duplicates = chessPiecesService.list(duplicateQuery);
            for (ChessPieces dup : duplicates) {
                dup.setPiecesState(0);
                chessPiecesService.updateById(dup);
                log.info("清理同ID棋子: 设置棋子 {} (原位置: {}{}) 为非活跃", dup.getId(), dup.getPositionX(), dup.getPositionY());
            }

            // 验证更新是否成功
            ChessPieces verifiedPiece = chessPiecesService.getById(objChessPieces.getId());
            if (verifiedPiece != null) {
                if (!verifiedPiece.getPositionX().equals(chessMove.getToPositionX()) ||
                        !verifiedPiece.getPositionY().equals(chessMove.getToPositionY())) {

                    log.error("验证失败：棋子ID={}位置未更新，当前={}{}, 期望={}{}",
                            verifiedPiece.getId(),
                            verifiedPiece.getPositionX(), verifiedPiece.getPositionY(),
                            chessMove.getToPositionX(), chessMove.getToPositionY());

                    // 尝试再次更新位置
                    verifiedPiece.setPositionX(chessMove.getToPositionX());
                    verifiedPiece.setPositionY(chessMove.getToPositionY());
                    chessPiecesService.updateById(verifiedPiece);
                } else {
                    log.info("验证成功：棋子ID={}位置已正确更新到{}{}",
                            verifiedPiece.getId(),
                            verifiedPiece.getPositionX(), verifiedPiece.getPositionY());
                }
            }

            // 保存移动记录
            this.save(chessMove);

            // 更新当前轮次
            if (objChessGame.getCurrentTurn() == 1) {
                objChessGame.setCurrentTurn(2); // 切换到白方
            } else {
                objChessGame.setCurrentTurn(1); // 切换到黑方
            }
            chessGameService.updateById(objChessGame);

            // 获取并更新棋盘缓存
            QueryWrapper<ChessPieces> allPiecesQuery = new QueryWrapper<>();
            allPiecesQuery.eq("chess_game_id", chessMove.getChessGameId());
            allPiecesQuery.eq("pieces_state", 1); // 只获取活跃的棋子
            allPiecesQuery.eq("del_flag", 0);
            List<ChessPieces> updatedPieces = chessPiecesService.list(allPiecesQuery);

            if (updatedPieces != null && !updatedPieces.isEmpty()) {
                log.info("更新棋盘缓存，包含{}个棋子", updatedPieces.size());
                // 调用GameService的缓存更新方法
                chessGameService.updateBoardCache(chessMove.getChessGameId(), updatedPieces);
            } else {
                log.warn("无法更新棋盘缓存：未找到活跃棋子");
            }

            // 设置返回值
            objChessPiecesVO.setChessGameId(chessMove.getChessGameId());
            objChessPiecesVO.setPositionX(chessMove.getToPositionX());
            objChessPiecesVO.setPositionY(chessMove.getToPositionY());
            objChessPiecesVO.setCurrentTurn(objChessGame.getCurrentTurn());

        } catch (Exception e) {
            log.error("棋子移动错误：", e);
            objChessPiecesVO.setErrorMessage("服务器处理棋子移动时发生错误");
        }
        return objChessPiecesVO;
    }
}
