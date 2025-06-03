package org.jeecg.modules.chess.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.chess.game.entity.ChessGame;
import org.jeecg.modules.chess.game.service.IChessGameService;
import org.jeecg.modules.chess.score.entity.ChessPlayerScore;
import org.jeecg.modules.chess.score.service.IChessPlayerScoreService;
import org.jeecg.modules.chess.statistics.service.IUserGameStatisticsService;
import org.jeecg.modules.chess.statistics.vo.UserGameStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @Description: 用户对局统计服务实现类
 * @Author: jeecg-boot
 * @Date: 2025-01-27
 * @Version: V1.0
 */
@Service
public class UserGameStatisticsServiceImpl implements IUserGameStatisticsService {
    
    @Autowired
    private IChessGameService chessGameService;
    
    @Autowired
    private IChessPlayerScoreService chessPlayerScoreService;
    
    @Override
    public UserGameStatisticsVO getUserGameStatistics(String userId) {
        UserGameStatisticsVO statistics = new UserGameStatisticsVO();
        statistics.setUserId(userId);
        
        // 查询用户积分信息
        QueryWrapper<ChessPlayerScore> scoreWrapper = new QueryWrapper<>();
        scoreWrapper.eq("user_id", userId);
        scoreWrapper.eq("del_flag", 0);
        ChessPlayerScore playerScore = chessPlayerScoreService.getOne(scoreWrapper);
        if (playerScore != null) {
            statistics.setCurrentScore(playerScore.getScore());
            statistics.setUserAccount(playerScore.getUserAccount());
        } else {
            statistics.setCurrentScore(600); // 默认初始积分
        }
        
        // 查询用户参与的所有游戏（包括进行中和已结束的游戏）
        QueryWrapper<ChessGame> gameWrapper = new QueryWrapper<>();
        gameWrapper.and(wrapper -> wrapper
            .eq("black_play_id", userId)
            .or()
            .eq("white_play_id", userId)
        );
        // 统计所有游戏（状态1-8），但只计算已结束的游戏结果
        gameWrapper.in("game_state", 1, 2, 3, 4, 5, 6, 7, 8);
        gameWrapper.eq("del_flag", 0);
        
        List<ChessGame> games = chessGameService.list(gameWrapper);
        
        int totalGames = games.size();
        int winGames = 0;
        int loseGames = 0;
        int drawGames = 0;
        int finishedGames = 0; // 已结束的游戏数量
        
        for (ChessGame game : games) {
            Integer gameState = game.getGameState();
            boolean isBlackPlayer = userId.equals(game.getBlackPlayId());
            
            // 只统计已结束的游戏结果（状态2-8）
            if (gameState >= 2 && gameState <= 8) {
                finishedGames++;
                
                switch (gameState) {
                    case 2: // 正常结束 - 需要根据其他信息判断胜负，这里暂时不处理
                        break;
                    case 3: // 黑方胜利
                        if (isBlackPlayer) {
                            winGames++;
                        } else {
                            loseGames++;
                        }
                        break;
                    case 4: // 白方胜利
                        if (isBlackPlayer) {
                            loseGames++;
                        } else {
                            winGames++;
                        }
                        break;
                    case 5: // 和棋
                        drawGames++;
                        break;
                    case 6: // 流局(超时)
                        drawGames++;
                        break;
                    case 7: // 黑方退出
                        if (isBlackPlayer) {
                            loseGames++;
                        } else {
                            winGames++;
                        }
                        break;
                    case 8: // 白方退出
                        if (isBlackPlayer) {
                            winGames++;
                        } else {
                            loseGames++;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        
        statistics.setTotalGames(totalGames);
        statistics.setWinGames(winGames);
        statistics.setLoseGames(loseGames);
        statistics.setDrawGames(drawGames);
        
        // 计算胜率、和棋率、败率（基于已结束的游戏）
        if (finishedGames > 0) {
            BigDecimal winRate = new BigDecimal(winGames * 100.0 / finishedGames)
                .setScale(2, RoundingMode.HALF_UP);
            BigDecimal drawRate = new BigDecimal(drawGames * 100.0 / finishedGames)
                .setScale(2, RoundingMode.HALF_UP);
            BigDecimal loseRate = new BigDecimal(loseGames * 100.0 / finishedGames)
                .setScale(2, RoundingMode.HALF_UP);
            
            statistics.setWinRate(winRate.doubleValue());
            statistics.setDrawRate(drawRate.doubleValue());
            statistics.setLoseRate(loseRate.doubleValue());
        } else {
            statistics.setWinRate(0.0);
            statistics.setDrawRate(0.0);
            statistics.setLoseRate(0.0);
        }
        
        return statistics;
    }
}