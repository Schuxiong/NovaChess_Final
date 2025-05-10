package org.jeecg.modules.chess.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.chess.game.entity.ChessGame;
import org.jeecg.modules.chess.game.entity.ChessMove;
import org.jeecg.modules.chess.game.service.IChessGameService;
import org.jeecg.modules.chess.game.service.IChessMoveService;
import org.jeecg.modules.chess.game.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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

        ChessGameVO obj = chessGameService.getChessGameChessPieces(chatMessage.getGameId(), chatMessage.getUserId());
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

            objChessMoveResponseVO = chessMoveService.chessMovePieces(chatMessage);

            latestGameState = chessGameService.getChessGameChessPieces(
                    gameId,
                    chatMessage.getUserId());

            if (objChessMoveResponseVO == null) {
                objChessMoveResponseVO = new ChessMoveResponseVO();
                objChessMoveResponseVO.setSuccess(false);
            }
            objChessMoveResponseVO.setLatestGameState(latestGameState);

        } catch (Exception e) {
            log.error("Error processing move for game {}: {}", gameId, e.getMessage(), e);
            objChessMoveResponseVO = new ChessMoveResponseVO();
            objChessMoveResponseVO.setSuccess(false);

            try {
                latestGameState = chessGameService.getChessGameChessPieces(gameId, chatMessage.getUserId());
                objChessMoveResponseVO.setLatestGameState(latestGameState);
            } catch (Exception fetchEx) {
                log.error("Failed to fetch game state after move error for game {}: {}", gameId, fetchEx.getMessage());

            }
        }

        String destination = "/topic/game/" + gameId;

        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("type", "MOVE_UPDATE");
        messagePayload.put("payload", objChessMoveResponseVO);

        messagingTemplate.convertAndSend(destination, messagePayload);

    }
}
