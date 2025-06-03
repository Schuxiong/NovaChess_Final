package org.jeecg.modules.chess.game.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.chess.game.entity.ChessGame;
import org.jeecg.modules.chess.game.entity.ChessMove;
import org.jeecg.modules.chess.game.service.IChessGameService;
import org.jeecg.modules.chess.game.service.IChessMoveService;
import org.jeecg.modules.chess.game.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Controller
public class ChessMoveWebsocketController extends JeecgController<ChessMove, IChessMoveService> {

    @Autowired
    private IChessGameService chessGameService;

    @Autowired
    private IChessMoveService chessMoveService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 处理游戏加入请求
     * 
     * @param gameId         游戏ID
     * @param chatMessage    请求内容
     * @param headerAccessor WebSocket头信息
     */
    @MessageMapping("/game/join/{gameId}")
    public void joinGame(@DestinationVariable String gameId, @Payload ChatMessageRequestVO chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        log.info("玩家加入游戏, gameId: {}, userId: {}", gameId, chatMessage.getUserId());

        if (chatMessage.getUserId() == null || chatMessage.getUserId().isBlank()) {
            log.error("joinGame error: User ID is missing for game {}", gameId);
            return;
        }

        try {
            // 获取游戏信息，传递skipInitialization参数，避免重复初始化棋盘
            Map<String, Object> params = new HashMap<>();
            params.put("skipInitialization", true);
            ChessGameVO gameVO = chessGameService.getChessGameChessPieces(gameId, chatMessage.getUserId(), params);

            if (gameVO == null) {
                log.error("joinGame error: Game {} not found or user not authorized", gameId);
                return;
            }

            // 向玩家发送棋盘状态
            messagingTemplate.convertAndSend("/topic/chessboard", Result.ok(gameVO));

            // 向所有玩家广播有玩家加入的消息
            Map<String, Object> joinMessage = new HashMap<>();
            joinMessage.put("type", "PLAYER_JOIN");
            joinMessage.put("payload", new HashMap<String, String>() {
                {
                    put("userId", chatMessage.getUserId());
                    put("username", chatMessage.getUsername());
                }
            });

            messagingTemplate.convertAndSend("/topic/game/" + gameId, joinMessage);
        } catch (Exception e) {
            log.error("Error processing join request for game {}: {}", gameId, e.getMessage(), e);
        }
    }

    /**
     * 获取棋盘信息
     * 
     * @param chatMessage
     * @return
     */
    @MessageMapping("/chessboard")
    @SendTo("/topic/chessboard")
    public Result<?> chessBoard(@Payload ChatMessageRequestVO chatMessage) {
        log.info("chessBoard enter");
        if (chatMessage.getGameId() == null || chatMessage.getGameId().isBlank()) {
            return Result.error(1, "gameId必填");
        }
        if (chatMessage.getUserId() == null || chatMessage.getUserId().isBlank()) {
            return Result.error(1, "userId必填");
        }

        // 传递skipInitialization参数，避免重复初始化棋盘
        Map<String, Object> params = new HashMap<>();
        params.put("skipInitialization", true);
        ChessGameVO obj = chessGameService.getChessGameChessPieces(chatMessage.getGameId(), chatMessage.getUserId(),
                params);
        return Result.ok(obj);
    }

    @MessageMapping("/movepieces")
    public void movePieces(@Payload ChatChessMoveRequestVO chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {

        String gameId = chatMessage.getGameId();
        if (gameId == null || gameId.isEmpty()) {
            gameId = chatMessage.getChessGameId();
        }

        if (gameId == null || gameId.isEmpty()) {
            log.error("movePieces error: Game ID is missing.");
            return;
        }
        if (chatMessage.getUserId() == null || chatMessage.getUserId().isEmpty()) {
            log.error("movePieces error: User ID is missing for game {}", gameId);
            return;
        }

        ChessMoveResponseVO objChessMoveResponseVO;
        ChessGameVO latestGameState = null;

        try {
            ChessGame game = chessGameService.getById(gameId);
            if (game == null) {
                log.error("movePieces error: Game {} not found.", gameId);
                return;
            }

            // 处理移动请求
            objChessMoveResponseVO = chessMoveService.chessMovePieces(chatMessage);

            // 检查移动是否成功
            if (objChessMoveResponseVO == null || !objChessMoveResponseVO.getSuccess()) {
                log.warn("移动失败: {}",
                        (objChessMoveResponseVO != null ? objChessMoveResponseVO.getErrorMessage() : "未知错误"));
                if (objChessMoveResponseVO == null) {
                    objChessMoveResponseVO = new ChessMoveResponseVO();
                    objChessMoveResponseVO.setSuccess(false);
                }
            } else {
                log.info("移动成功，发送更新消息");
            }

            // 不使用chessMovePieces返回的游戏状态，而是强制重新从数据库获取
            try {
                log.info("强制从数据库重新获取游戏状态，确保数据最新");
                // 添加标志，指示这是一次棋子移动后的查询，强制从数据库获取
                Map<String, Object> params = new HashMap<>();
                params.put("afterMove", true);
                log.info("使用afterMove=true标记获取最新游戏状态");
                latestGameState = chessGameService.getChessGameChessPieces(gameId, chatMessage.getUserId(), params);
                objChessMoveResponseVO.setLatestGameState(latestGameState);
                
                // 获取并设置走棋历史
                QueryWrapper<ChessMove> moveHistoryQuery = new QueryWrapper<>();
                moveHistoryQuery.eq("chess_game_id", gameId);
                moveHistoryQuery.eq("del_flag", 0);
                moveHistoryQuery.orderByAsc("create_time"); // 按创建时间升序排列
                List<ChessMove> moveHistory = chessMoveService.list(moveHistoryQuery);
                objChessMoveResponseVO.setMoveHistory(moveHistory);
                log.info("WebSocket设置走棋历史，共{}条记录", moveHistory != null ? moveHistory.size() : 0);
            } catch (Exception fetchEx) {
                log.error("Failed to fetch game state after move for game {}: {}", gameId, fetchEx.getMessage());
            }

        } catch (Exception e) {
            log.error("Error processing move for game {}: {}", gameId, e.getMessage(), e);
            objChessMoveResponseVO = new ChessMoveResponseVO();
            objChessMoveResponseVO.setSuccess(false);
            objChessMoveResponseVO.setErrorMessage("服务器处理移动请求时发生错误: " + e.getMessage());

            try {
                // 添加标志，指示这是一次棋子移动后的查询
                Map<String, Object> params = new HashMap<>();
                params.put("afterMove", true);
                log.info("错误处理中，使用afterMove=true标记获取游戏状态");
                latestGameState = chessGameService.getChessGameChessPieces(gameId, chatMessage.getUserId(), params);
                objChessMoveResponseVO.setLatestGameState(latestGameState);
                
                // 即使出错也获取并设置走棋历史
                QueryWrapper<ChessMove> moveHistoryQuery = new QueryWrapper<>();
                moveHistoryQuery.eq("chess_game_id", gameId);
                moveHistoryQuery.eq("del_flag", 0);
                moveHistoryQuery.orderByAsc("create_time"); // 按创建时间升序排列
                List<ChessMove> moveHistory = chessMoveService.list(moveHistoryQuery);
                objChessMoveResponseVO.setMoveHistory(moveHistory);
                log.info("错误处理中设置走棋历史，共{}条记录", moveHistory != null ? moveHistory.size() : 0);
            } catch (Exception fetchEx) {
                log.error("Failed to fetch game state after move error for game {}: {}", gameId, fetchEx.getMessage());
            }
        }

        // 构建与发送响应
        String destination = "/topic/game/" + gameId;
        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("type", "MOVE_UPDATE");
        messagePayload.put("payload", objChessMoveResponseVO);

        log.info("发送走棋结果到: {}", destination);
        messagingTemplate.convertAndSend(destination, messagePayload);
    }

    /**
     * 初始化并获取棋盘信息，当棋盘为空时使用
     */
    @MessageMapping("/game/initialize")
    public void initializeChessboard(@Payload ChatMessageRequestVO chatMessage) {
        log.info("初始化棋盘 gameId: {}, userId: {}", chatMessage.getGameId(), chatMessage.getUserId());

        if (chatMessage.getGameId() == null || chatMessage.getGameId().isBlank()) {
            log.error("initializeChessboard error: Game ID is missing.");
            return;
        }
        if (chatMessage.getUserId() == null || chatMessage.getUserId().isBlank()) {
            log.error("initializeChessboard error: User ID is missing.");
            return;
        }

        try {
            // 获取棋盘数据，此处允许初始化
            Map<String, Object> params = new HashMap<>();
            params.put("skipInitialization", false); // 明确允许初始化
            ChessGameVO gameVO = chessGameService.getChessGameChessPieces(chatMessage.getGameId(),
                    chatMessage.getUserId(), params);

            if (gameVO == null) {
                log.error("棋盘初始化失败: {}", chatMessage.getGameId());
                return;
            }

            // 向请求者发送棋盘状态
            messagingTemplate.convertAndSend("/topic/chessboard", Result.ok(gameVO));

            // 向游戏频道广播初始化完成消息
            Map<String, Object> initMessage = new HashMap<>();
            initMessage.put("type", "GAME_INITIALIZED");
            initMessage.put("payload", gameVO);

            messagingTemplate.convertAndSend("/topic/game/" + chatMessage.getGameId(), initMessage);

        } catch (Exception e) {
            log.error("棋盘初始化异常: {}", e.getMessage(), e);
        }
    }
}
