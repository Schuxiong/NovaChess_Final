package org.jeecg.modules.chess.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.chess.score.entity.ChessPlayerScore;
import org.jeecg.modules.chess.score.service.IChessPlayerScoreService;
import org.jeecg.modules.system.event.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 用户注册事件监听器
 * @Description: 监听用户注册事件，自动初始化用户积分
 * @Author: jeecg-boot
 * @Date: 2025-01-20
 * @Version: V1.0
 */
@Slf4j
@Component
public class UserRegisteredEventListener {
    
    @Autowired
    private IChessPlayerScoreService chessPlayerScoreService;
    
    /**
     * 监听用户注册事件，初始化用户积分
     * @param event 用户注册事件
     */
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        String userId = event.getUserId();
        String username = event.getUsername();
        
        try {
            // 检查用户是否已有积分记录
            QueryWrapper<ChessPlayerScore> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            ChessPlayerScore existingScore = chessPlayerScoreService.getOne(queryWrapper);
            
            if (existingScore == null) {
                // 创建新的积分记录
                ChessPlayerScore playerScore = new ChessPlayerScore();
                playerScore.setUserId(userId);
                playerScore.setUserAccount(username);
                playerScore.setScore(600); // 初始积分600
                playerScore.setCreateTime(new Date());
                playerScore.setDelFlag(0);
                chessPlayerScoreService.save(playerScore);
                log.info("用户 {} 积分初始化成功，初始积分：600", username);
            } else {
                log.info("用户 {} 已有积分记录，跳过初始化", username);
            }
        } catch (Exception e) {
            log.error("用户 {} 积分初始化失败：{}", username, e.getMessage(), e);
        }
    }
}