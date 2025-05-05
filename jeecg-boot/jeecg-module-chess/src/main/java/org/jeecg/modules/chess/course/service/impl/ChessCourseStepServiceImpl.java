package org.jeecg.modules.chess.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.chess.course.entity.ChessCourseStep;
import org.jeecg.modules.chess.course.mapper.ChessCourseStepMapper;
import org.jeecg.modules.chess.course.service.IChessCourseStepService;
import org.springframework.stereotype.Service;

@Service
public class ChessCourseStepServiceImpl extends ServiceImpl<ChessCourseStepMapper, ChessCourseStep> implements IChessCourseStepService {
}