package org.jeecg.modules.chess.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.chess.course.entity.ChessCourse;
import org.jeecg.modules.chess.course.mapper.ChessCourseMapper;
import org.jeecg.modules.chess.course.service.IChessCourseService;
import org.springframework.stereotype.Service;

@Service
public class ChessCourseServiceImpl extends ServiceImpl<ChessCourseMapper, ChessCourse> implements IChessCourseService {
}