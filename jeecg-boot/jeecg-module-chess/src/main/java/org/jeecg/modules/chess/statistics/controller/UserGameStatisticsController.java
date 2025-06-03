package org.jeecg.modules.chess.statistics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.chess.statistics.service.IUserGameStatisticsService;
import org.jeecg.modules.chess.statistics.vo.UserGameStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 用户对局统计控制器
 * @Author: jeecg-boot
 * @Date: 2025-01-27
 * @Version: V1.0
 */
@Slf4j
@Tag(name = "用户对局统计")
@RestController
@RequestMapping("/statistics/userGame")
public class UserGameStatisticsController {
    
    @Autowired
    private IUserGameStatisticsService userGameStatisticsService;
    
    /**
     * 获取当前用户的对局统计信息
     *
     * @return 用户对局统计信息
     */
    @AutoLog(value = "用户对局统计-获取当前用户统计信息")
    @Operation(summary = "获取当前用户的对局统计信息")
    @GetMapping(value = "/current")
    public Result<UserGameStatisticsVO> getCurrentUserStatistics() {
        try {
            // 从Shiro中获取当前登录用户
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (loginUser == null) {
                return Result.error("用户未登录");
            }
            
            String userId = loginUser.getId();
            UserGameStatisticsVO statistics = userGameStatisticsService.getUserGameStatistics(userId);
            return Result.OK(statistics);
        } catch (Exception e) {
            log.error("获取当前用户对局统计信息失败", e);
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定用户的对局统计信息
     *
     * @param userId 用户ID
     * @return 用户对局统计信息
     */
    @AutoLog(value = "用户对局统计-获取指定用户统计信息")
    @Operation(summary = "获取指定用户的对局统计信息")
    @GetMapping(value = "/user/{userId}")
    public Result<UserGameStatisticsVO> getUserStatistics(@PathVariable String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return Result.error("用户ID不能为空");
            }
            
            UserGameStatisticsVO statistics = userGameStatisticsService.getUserGameStatistics(userId);
            return Result.OK(statistics);
        } catch (Exception e) {
            log.error("获取用户对局统计信息失败，用户ID: {}", userId, e);
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
}