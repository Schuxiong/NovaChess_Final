package org.jeecg.modules.chess.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.chess.course.entity.ChessBoardSetup;
import org.jeecg.modules.chess.course.mapper.ChessBoardSetupMapper;
import org.jeecg.modules.chess.course.service.IChessBoardSetupService;
import org.springframework.stereotype.Service;

@Service
public class ChessBoardSetupServiceImpl extends ServiceImpl<ChessBoardSetupMapper, ChessBoardSetup> implements IChessBoardSetupService {
}