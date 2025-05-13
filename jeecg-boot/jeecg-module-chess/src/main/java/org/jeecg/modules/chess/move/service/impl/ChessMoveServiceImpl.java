package org.jeecg.modules.chess.move.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.chess.move.entity.ChessMove;
import org.jeecg.modules.chess.move.mapper.ChessMoveRecordMapper;
import org.jeecg.modules.chess.move.service.IChessMoveService;
import org.springframework.stereotype.Service;

/**
 * @Description: 棋子移动记录服务实现类
 * @Author: jeecg-boot
 * @Date: 2025-05-11
 * @Version: V1.0
 */
@Service("chessMoveServiceMoveImpl")
public class ChessMoveServiceImpl extends ServiceImpl<ChessMoveRecordMapper, ChessMove> implements IChessMoveService {

}