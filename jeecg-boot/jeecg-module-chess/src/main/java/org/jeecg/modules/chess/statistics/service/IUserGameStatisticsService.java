package org.jeecg.modules.chess.statistics.service;

import org.jeecg.modules.chess.statistics.vo.UserGameStatisticsVO;

/**
 * @Description: 用户对局统计服务接口
 * @Author: jeecg-boot
 * @Date: 2025-01-27
 * @Version: V1.0
 */
public interface IUserGameStatisticsService {
    
    /**
     * 获取用户对局统计信息
     * @param userId 用户ID
     * @return 用户对局统计信息
     */
    UserGameStatisticsVO getUserGameStatistics(String userId);
}