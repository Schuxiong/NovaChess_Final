package org.jeecg.modules.chess.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.chess.course.entity.ChessCourseStepExisting;
import org.jeecg.modules.chess.course.mapper.ChessCourseStepExistingMapper;
import org.jeecg.modules.chess.course.service.IChessCourseStepExistingService;
import org.springframework.stereotype.Service;

/**
 * 课程步骤服务实现类（现有表结构）
 */
@Service
public class ChessCourseStepExistingServiceImpl extends ServiceImpl<ChessCourseStepExistingMapper, ChessCourseStepExisting> implements IChessCourseStepExistingService {
}