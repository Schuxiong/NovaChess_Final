package org.jeecg.modules.chess.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.chess.course.entity.ChessBoardSetup;
import org.jeecg.modules.chess.course.entity.ChessCourse;
import org.jeecg.modules.chess.course.entity.ChessCourseStep;
import org.jeecg.modules.chess.course.mapper.ChessCourseMapper;
import org.jeecg.modules.chess.course.service.IChessBoardSetupService;
import org.jeecg.modules.chess.course.service.IChessCourseService;
import org.jeecg.modules.chess.course.service.IChessCourseStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChessCourseModuleServiceImpl extends ServiceImpl<ChessCourseMapper, ChessCourse> {

    @Autowired
    private IChessCourseStepService chessCourseStepService;
    
    @Autowired
    private IChessBoardSetupService chessBoardSetupService;

    @Transactional(rollbackFor = Exception.class)
    public void saveCourseWithSteps(ChessCourse chessCourse, List<ChessCourseStep> steps, List<ChessBoardSetup> setups) {
        // 保存课程
        this.save(chessCourse);
        
        // 保存步骤
        steps.forEach(step -> {
            step.setCourseId(chessCourse.getId());
            chessCourseStepService.save(step);
            
            // 保存棋盘设置
            setups.forEach(setup -> {
                if(setup.getStepId().equals(step.getId())) {
                    setup.setStepId(step.getId());
                    chessBoardSetupService.save(setup);
                }
            });
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCourseWithSteps(ChessCourse chessCourse, List<ChessCourseStep> steps, List<ChessBoardSetup> setups) {
        // 更新课程
        this.updateById(chessCourse);
        
        // 更新步骤
        steps.forEach(step -> {
            if(step.getId() == null) {
                step.setCourseId(chessCourse.getId());
                chessCourseStepService.save(step);
            } else {
                chessCourseStepService.updateById(step);
            }
            
            // 更新棋盘设置
            setups.forEach(setup -> {
                if(setup.getStepId().equals(step.getId())) {
                    if(setup.getId() == null) {
                        setup.setStepId(step.getId());
                        chessBoardSetupService.save(setup);
                    } else {
                        chessBoardSetupService.updateById(setup);
                    }
                }
            });
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCourseWithSteps(String courseId) {
        // 删除课程
        this.removeById(courseId);
        
        // 删除关联步骤
        QueryWrapper<ChessCourseStep> stepWrapper = new QueryWrapper<>();
        stepWrapper.eq("course_id", courseId);
        List<ChessCourseStep> steps = chessCourseStepService.list(stepWrapper);
        
        // 删除关联棋盘设置
        steps.forEach(step -> {
            QueryWrapper<ChessBoardSetup> setupWrapper = new QueryWrapper<>();
            setupWrapper.eq("step_id", step.getId());
            chessBoardSetupService.remove(setupWrapper);
        });
        
        // 删除步骤
        chessCourseStepService.remove(stepWrapper);
    }
}