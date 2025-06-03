package org.jeecg.modules.chess.game.service;

import org.jeecg.modules.chess.game.entity.ChessDrawRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 和棋请求
 * @Author: jeecg-boot
 * @Date: 2025-06-03
 * @Version: V1.0
 */
public interface IChessDrawRequestService extends IService<ChessDrawRequest> {

    /**
     * 发起和棋请求
     * @param gameId 游戏ID
     * @param requestUserId 发起请求的用户ID
     * @return 请求结果
     */
    String requestDraw(String gameId, String requestUserId);

    /**
     * 响应和棋请求
     * @param gameId 游戏ID
     * @param userId 响应用户ID
     * @param accept 是否接受
     * @return 响应结果
     */
    String respondDraw(String gameId, String userId, boolean accept);

    /**
     * 获取游戏的和棋请求状态
     * @param gameId 游戏ID
     * @return 和棋请求信息
     */
    ChessDrawRequest getDrawStatus(String gameId);

    /**
     * 取消和棋请求
     * @param gameId 游戏ID
     * @param userId 用户ID
     * @return 取消结果
     */
    String cancelDrawRequest(String gameId, String userId);
}