package org.jeecg.modules.chess.game.service.impl;

import org.jeecg.modules.chess.game.entity.ChessDrawRequest;
import org.jeecg.modules.chess.game.entity.ChessGame;
import org.jeecg.modules.chess.game.mapper.ChessDrawRequestMapper;
import org.jeecg.modules.chess.game.service.IChessDrawRequestService;
import org.jeecg.modules.chess.game.service.IChessGameService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 和棋请求
 * @Author: jeecg-boot
 * @Date: 2025-06-03
 * @Version: V1.0
 */
@Slf4j
@Service
public class ChessDrawRequestServiceImpl extends ServiceImpl<ChessDrawRequestMapper, ChessDrawRequest> implements IChessDrawRequestService {

    @Autowired
    private IChessGameService chessGameService;
    
    @Autowired
    private ISysUserService sysUserService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public String requestDraw(String gameId, String requestUserId) {
        // 1. 验证游戏是否存在且正在进行中
        ChessGame game = chessGameService.getById(gameId);
        if (game == null) {
            return "游戏不存在";
        }
        
        if (game.getGameState() != 1) {
            return "游戏已结束，无法发起和棋请求";
        }
        
        // 2. 验证用户是否为游戏参与者
        boolean isBlackPlayer = requestUserId.equals(game.getBlackPlayId());
        boolean isWhitePlayer = requestUserId.equals(game.getWhitePlayId());
        
        if (!isBlackPlayer && !isWhitePlayer) {
            return "您不是该游戏的参与者";
        }
        
        // 3. 检查是否已有待处理的和棋请求
        QueryWrapper<ChessDrawRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("game_id", gameId);
        queryWrapper.eq("status", 1); // 待响应状态
        ChessDrawRequest existingRequest = this.getOne(queryWrapper);
        
        if (existingRequest != null) {
            if (existingRequest.getRequestUserId().equals(requestUserId)) {
                return "您已发起和棋请求，请等待对方响应";
            } else {
                return "对方已发起和棋请求，请先响应对方的请求";
            }
        }
        
        // 4. 获取用户信息
        SysUser requestUser = sysUserService.getById(requestUserId);
        if (requestUser == null) {
            return "用户信息不存在";
        }
        
        // 5. 确定目标用户
        String targetUserId = isBlackPlayer ? game.getWhitePlayId() : game.getBlackPlayId();
        String targetUserAccount = isBlackPlayer ? game.getWhitePlayAccount() : game.getBlackPlayAccount();
        
        // 6. 创建和棋请求记录
        ChessDrawRequest drawRequest = new ChessDrawRequest();
        drawRequest.setGameId(gameId);
        drawRequest.setRequestUserId(requestUserId);
        drawRequest.setRequestUserAccount(requestUser.getUsername());
        drawRequest.setTargetUserId(targetUserId);
        drawRequest.setTargetUserAccount(targetUserAccount);
        drawRequest.setStatus(1); // 待响应
        drawRequest.setCreateTime(new Date());
        drawRequest.setCreateBy(requestUserId);
        
        this.save(drawRequest);
        
        // 7. 通过WebSocket通知对方
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "draw_request");
            message.put("gameId", gameId);
            message.put("requestUser", requestUser.getUsername());
            message.put("message", requestUser.getUsername() + " 发起了和棋请求");
            message.put("requestId", drawRequest.getId());
            
            // 通知目标用户
            if (targetUserAccount != null) {
                messagingTemplate.convertAndSendToUser(
                    targetUserAccount,
                    "/queue/game/" + gameId,
                    message
                );
            }
            
            // 同时通知发起者确认请求已发送
            message.put("type", "draw_request_sent");
            message.put("message", "和棋请求已发送，等待对方响应");
            messagingTemplate.convertAndSendToUser(
                requestUser.getUsername(),
                "/queue/game/" + gameId,
                message
            );
            
        } catch (Exception e) {
            log.error("发送和棋请求通知失败：{}", e.getMessage(), e);
        }
        
        return "和棋请求已发送";
    }

    @Override
    @Transactional
    public String respondDraw(String gameId, String userId, boolean accept) {
        // 1. 验证游戏是否存在且正在进行中
        ChessGame game = chessGameService.getById(gameId);
        if (game == null) {
            return "游戏不存在";
        }
        
        if (game.getGameState() != 1) {
            return "游戏已结束，无法响应和棋请求";
        }
        
        // 2. 查找待处理的和棋请求
        QueryWrapper<ChessDrawRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("game_id", gameId);
        queryWrapper.eq("target_user_id", userId);
        queryWrapper.eq("status", 1); // 待响应状态
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        
        ChessDrawRequest drawRequest = this.getOne(queryWrapper);
        if (drawRequest == null) {
            return "没有找到待处理的和棋请求";
        }
        
        // 3. 获取用户信息
        SysUser responseUser = sysUserService.getById(userId);
        if (responseUser == null) {
            return "用户信息不存在";
        }
        
        // 4. 更新请求状态
        drawRequest.setStatus(accept ? 2 : 3); // 2-已接受 3-已拒绝
        drawRequest.setResponseTime(new Date());
        drawRequest.setUpdateTime(new Date());
        drawRequest.setUpdateBy(userId);
        this.updateById(drawRequest);
        
        String resultMessage;
        
        if (accept) {
            // 5. 如果接受，更新游戏状态为和棋
            game.setGameState(5); // 和棋状态
            game.setUpdateTime(new Date());
            chessGameService.updateById(game);
            
            resultMessage = "和棋请求已接受，游戏结束";
        } else {
            resultMessage = "和棋请求已拒绝";
        }
        
        // 6. 通过WebSocket通知双方
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", accept ? "draw_accepted" : "draw_rejected");
            message.put("gameId", gameId);
            message.put("responseUser", responseUser.getUsername());
            message.put("message", responseUser.getUsername() + (accept ? " 接受了和棋请求" : " 拒绝了和棋请求"));
            
            if (accept) {
                message.put("gameState", 5);
                message.put("gameResult", "和棋");
            }
            
            // 通知发起者
            if (drawRequest.getRequestUserAccount() != null) {
                messagingTemplate.convertAndSendToUser(
                    drawRequest.getRequestUserAccount(),
                    "/queue/game/" + gameId,
                    message
                );
            }
            
            // 通知响应者
            messagingTemplate.convertAndSendToUser(
                responseUser.getUsername(),
                "/queue/game/" + gameId,
                message
            );
            
        } catch (Exception e) {
            log.error("发送和棋响应通知失败：{}", e.getMessage(), e);
        }
        
        return resultMessage;
    }

    @Override
    public ChessDrawRequest getDrawStatus(String gameId) {
        QueryWrapper<ChessDrawRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("game_id", gameId);
        queryWrapper.eq("status", 1); // 只查询待响应的请求
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public String cancelDrawRequest(String gameId, String userId) {
        // 查找用户发起的待处理和棋请求
        QueryWrapper<ChessDrawRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("game_id", gameId);
        queryWrapper.eq("request_user_id", userId);
        queryWrapper.eq("status", 1); // 待响应状态
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        
        ChessDrawRequest drawRequest = this.getOne(queryWrapper);
        if (drawRequest == null) {
            return "没有找到可取消的和棋请求";
        }
        
        // 更新请求状态为已过期
        drawRequest.setStatus(4); // 4-已过期
        drawRequest.setUpdateTime(new Date());
        drawRequest.setUpdateBy(userId);
        this.updateById(drawRequest);
        
        // 通过WebSocket通知对方
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "draw_request_cancelled");
            message.put("gameId", gameId);
            message.put("message", drawRequest.getRequestUserAccount() + " 取消了和棋请求");
            
            if (drawRequest.getTargetUserAccount() != null) {
                messagingTemplate.convertAndSendToUser(
                    drawRequest.getTargetUserAccount(),
                    "/queue/game/" + gameId,
                    message
                );
            }
            
        } catch (Exception e) {
            log.error("发送和棋请求取消通知失败：{}", e.getMessage(), e);
        }
        
        return "和棋请求已取消";
    }
}