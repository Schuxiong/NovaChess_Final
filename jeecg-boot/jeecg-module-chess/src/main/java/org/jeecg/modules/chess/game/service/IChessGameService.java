package org.jeecg.modules.chess.game.service;

import org.jeecg.modules.chess.game.entity.ChessGame;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.chess.game.entity.ChessPieces;
import org.jeecg.modules.chess.game.vo.ChessGameBatchVO;
import org.jeecg.modules.chess.game.vo.ChessGameVO;
import org.jeecg.modules.chess.game.vo.ChessGameWithScoreVO;
import org.jeecg.modules.chess.game.vo.PlayerPairVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;
import java.util.Map;

/**
 * @Description: 游戏
 * @Author: jeecg-boot
 * @Date: 2025-04-27
 * @Version: V1.0
 */
public interface IChessGameService extends IService<ChessGame> {

    /**
     * 初始化游戏
     * 
     * @param sourcePlayerPariVO 挑战者
     * @param targetPlayerPairVO 被挑战者
     * @return
     */
    ChessGameVO init(PlayerPairVO sourcePlayerPariVO, PlayerPairVO targetPlayerPairVO);

    /**
     * 进入游戏
     * 
     * @param userId
     * @return
     */
    List<ChessGameBatchVO> enter(String userId);

    /**
     * 获取当前棋子位置信息
     * 
     * @param chessGameId 游戏ID
     * @param userId      用户ID
     * @return 游戏棋盘状态
     */
    ChessGameVO getChessGameChessPieces(String chessGameId, String userId);

    /**
     * 获取当前棋子位置信息（带参数）
     * 
     * @param chessGameId 游戏ID
     * @param userId      用户ID
     * @param params      附加参数，如afterMove标志
     * @return 游戏棋盘状态
     */
    ChessGameVO getChessGameChessPieces(String chessGameId, String userId, Map<String, Object> params);

    /**
     * 更新棋盘缓存
     * 
     * @param chessGameId 游戏ID
     * @param pieces      棋子列表
     */
    void updateBoardCache(String chessGameId, List<ChessPieces> pieces);

    /**
     * 分页查询游戏列表（关联用户积分）
     * 
     * @param page 分页对象
     * @param queryWrapper 查询条件
     * @return 游戏列表（包含积分信息）
     */
    IPage<ChessGameWithScoreVO> pageWithScore(Page<ChessGame> page, QueryWrapper<ChessGame> queryWrapper);
}
