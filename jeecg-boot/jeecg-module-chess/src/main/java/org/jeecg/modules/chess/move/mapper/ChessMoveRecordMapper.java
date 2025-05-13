package org.jeecg.modules.chess.move.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.chess.move.entity.ChessMove;

/**
 * @Description: 棋子移动记录Mapper接口
 * @Author: jeecg-boot
 * @Date: 2025-05-11
 * @Version: V1.0
 */
@Mapper
public interface ChessMoveRecordMapper extends BaseMapper<ChessMove> {

}