package org.jeecg.modules.chess.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.chess.course.entity.ChessCourse;
import org.jeecg.modules.chess.course.vo.CourseRequestVO;
import org.jeecg.modules.chess.course.vo.CourseResponseVO;

public interface IChessCourseService extends IService<ChessCourse> {
    
    /**
     * 创建课程
     */
    String createCourse(CourseRequestVO courseRequestVO);
    
    /**
     * 更新课程
     */
    void updateCourse(CourseRequestVO courseRequestVO);
    
    /**
     * 根据ID查询课程详情
     */
    CourseResponseVO getCourseById(String courseId);
}