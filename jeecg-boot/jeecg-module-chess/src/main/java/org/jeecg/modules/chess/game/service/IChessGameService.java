package org.jeecg.modules.chess.game.service;

import org.jeecg.modules.chess.game.entity.ChessGame;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.chess.game.entity.ChessPieces;
import org.jeecg.modules.chess.game.vo.ChessGameBatchVO;
import org.jeecg.modules.chess.game.vo.ChessGameVO;
import org.jeecg.modules.chess.game.vo.PlayerPairVO;

import java.util.List;

/**
 * @Description: 游戏
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
public interface IChessGameService extends IService<ChessGame> {

    /**
     * 初始化游戏信息
     * @param playerPairVO
     */
    public ChessGameVO init(PlayerPairVO sourcePlayerPariVO, PlayerPairVO targetPlayerPairVO);


    public List<ChessGameBatchVO> enter(String userId);
}
