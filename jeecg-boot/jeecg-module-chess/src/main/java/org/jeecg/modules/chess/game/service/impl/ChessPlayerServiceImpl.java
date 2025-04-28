package org.jeecg.modules.chess.game.service.impl;

import org.jeecg.modules.chess.game.entity.ChessPlayer;
import org.jeecg.modules.chess.game.mapper.ChessPlayerMapper;
import org.jeecg.modules.chess.game.service.IChessPlayerService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 游戏参与者
 * @Author: jeecg-boot
 * @Date:   2025-04-26
 * @Version: V1.0
 */
@Service
public class ChessPlayerServiceImpl extends ServiceImpl<ChessPlayerMapper, ChessPlayer> implements IChessPlayerService {

}
