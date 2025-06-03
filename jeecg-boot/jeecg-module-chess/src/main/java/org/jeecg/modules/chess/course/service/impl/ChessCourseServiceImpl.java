package org.jeecg.modules.chess.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.chess.course.entity.ChessCourse;
import org.jeecg.modules.chess.course.entity.ChessCourseStepExisting;
import org.jeecg.modules.chess.course.enums.PieceType;
import org.jeecg.modules.chess.course.mapper.ChessCourseMapper;
import org.jeecg.modules.chess.course.service.IChessCourseService;
import org.jeecg.modules.chess.course.service.IChessCourseStepExistingService;
import org.jeecg.modules.chess.course.vo.CourseRequestVO;
import org.jeecg.modules.chess.course.vo.CourseResponseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChessCourseServiceImpl extends ServiceImpl<ChessCourseMapper, ChessCourse> implements IChessCourseService {
    
    @Autowired
    private IChessCourseStepExistingService chessCourseStepExistingService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 创建课程
     */
    @Transactional(rollbackFor = Exception.class)
    public String createCourse(CourseRequestVO courseRequestVO) {
        // 验证数据
        validateCourseData(courseRequestVO);
        
        // 创建课程
        ChessCourse course = new ChessCourse();
        course.setTitle(courseRequestVO.getTitle());
        course.setDescription(courseRequestVO.getDescription());
        course.setIconUrl(courseRequestVO.getIcon());
        course.setCategory(courseRequestVO.getCategory());
        course.setCreatedAt(new Date());
        course.setUpdatedAt(new Date());
        
        this.save(course);
        
        // 创建步骤
        createCourseSteps(course.getId(), courseRequestVO.getSteps());
        
        return course.getId();
    }
    
    /**
     * 编辑课程
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCourse(CourseRequestVO courseRequestVO) {
        // 验证数据
        validateCourseData(courseRequestVO);
        
        // 更新课程基本信息
        ChessCourse course = this.getById(courseRequestVO.getId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        course.setTitle(courseRequestVO.getTitle());
        course.setDescription(courseRequestVO.getDescription());
        course.setIconUrl(courseRequestVO.getIcon());
        course.setCategory(courseRequestVO.getCategory());
        course.setUpdatedAt(new Date());
        this.updateById(course);
        
        // 删除原有步骤
        QueryWrapper<ChessCourseStepExisting> stepWrapper = new QueryWrapper<>();
        stepWrapper.eq("course_id", course.getId());
        chessCourseStepExistingService.remove(stepWrapper);
        
        // 重新创建步骤
        createCourseSteps(course.getId(), courseRequestVO.getSteps());
    }
    
    /**
     * 查询课程详情
     */
    public CourseResponseVO getCourseById(String courseId) {
        ChessCourse course = this.getById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        CourseResponseVO responseVO = new CourseResponseVO();
        responseVO.setId(course.getId());
        responseVO.setTitle(course.getTitle());
        responseVO.setDescription(course.getDescription());
        responseVO.setIcon(course.getIconUrl());
        responseVO.setCategory(course.getCategory());
        
        // 查询步骤
        QueryWrapper<ChessCourseStepExisting> stepWrapper = new QueryWrapper<>();
        stepWrapper.eq("course_id", courseId)
                  .orderByAsc("step_order");
        List<ChessCourseStepExisting> steps = chessCourseStepExistingService.list(stepWrapper);
        
        List<CourseResponseVO.CourseStepResponseVO> stepVOs = steps.stream()
                .map(this::convertToStepResponseVO)
                .collect(Collectors.toList());
        
        responseVO.setSteps(stepVOs);
        return responseVO;
    }
    
    /**
     * 创建课程步骤
     */
    private void createCourseSteps(String courseId, List<CourseRequestVO.CourseStepVO> stepVOs) {
        if (CollectionUtils.isEmpty(stepVOs)) {
            return;
        }
        
        List<ChessCourseStepExisting> steps = new ArrayList<>();
        for (int i = 0; i < stepVOs.size(); i++) {
            CourseRequestVO.CourseStepVO stepVO = stepVOs.get(i);
            ChessCourseStepExisting step = new ChessCourseStepExisting();
            
            step.setCourseId(courseId);
            step.setStepType(stepVO.getType());
            step.setMessage(stepVO.getMessage());
            step.setCorrectMessage(stepVO.getCorrectMessage());
            step.setErrorMessage(stepVO.getErrorMessage());
            step.setHintMessage(stepVO.getHintMessage());
            step.setStepOrder(i + 1);
            
            // 转换棋盘设置
            if (stepVO.getBoardSetup() != null) {
                step.setBoardClear(stepVO.getBoardSetup().getClear());
                Map<String, Object> boardSetupMap = convertBoardSetupToMap(stepVO.getBoardSetup());
                step.setBoardSetup(boardSetupMap);
            }
            
            // 转换期望移动
            if (stepVO.getExpectedMove() != null) {
                if (stepVO.getExpectedMove().getFrom() != null) {
                    step.setExpectedFromRow(stepVO.getExpectedMove().getFrom().getRow());
                    step.setExpectedFromCol(stepVO.getExpectedMove().getFrom().getCol());
                }
                if (stepVO.getExpectedMove().getTo() != null) {
                    step.setExpectedToRow(stepVO.getExpectedMove().getTo().getRow());
                    step.setExpectedToCol(stepVO.getExpectedMove().getTo().getCol());
                }
            }
            
            steps.add(step);
        }
        
        chessCourseStepExistingService.saveBatch(steps);
    }
    
    /**
     * 验证课程数据
     */
    private void validateCourseData(CourseRequestVO courseRequestVO) {
        if (courseRequestVO.getSteps() != null) {
            for (CourseRequestVO.CourseStepVO step : courseRequestVO.getSteps()) {
                // 验证步骤类型
                if (!"info".equals(step.getType()) && !"task".equals(step.getType()) && !"intro".equals(step.getType()) && !"demo".equals(step.getType())) {
                    throw new RuntimeException("无效的步骤类型: " + step.getType());
                }
                
                // 验证棋子类型
                if (step.getBoardSetup() != null && step.getBoardSetup().getPieces() != null) {
                    for (CourseRequestVO.PieceVO piece : step.getBoardSetup().getPieces()) {
                        // 将前端的连字符格式转换为后端的下划线格式
                        String normalizedPieceType = piece.getPiece().replace("-", "_");
                        if (!PieceType.isValidCode(normalizedPieceType)) {
                            throw new RuntimeException("无效的棋子类型: " + piece.getPiece());
                        }
                        
                        // 验证位置
                        validatePosition(piece.getPosition());
                    }
                }
                
                // 验证期望移动位置
                if (step.getExpectedMove() != null) {
                    if (step.getExpectedMove().getFrom() != null) {
                        validatePosition(step.getExpectedMove().getFrom());
                    }
                    if (step.getExpectedMove().getTo() != null) {
                        validatePosition(step.getExpectedMove().getTo());
                    }
                }
            }
        }
    }
    
    /**
     * 验证位置坐标
     */
    private void validatePosition(CourseRequestVO.PositionVO position) {
        if (position == null) return;
        
        if (position.getRow() == null || position.getRow() < 0 || position.getRow() > 7) {
            throw new RuntimeException("无效的行号: " + position.getRow());
        }
        if (position.getCol() == null || position.getCol() < 0 || position.getCol() > 7) {
            throw new RuntimeException("无效的列号: " + position.getCol());
        }
    }
    
    /**
     * 转换棋盘设置为Map
     */
    private Map<String, Object> convertBoardSetupToMap(CourseRequestVO.BoardSetupVO boardSetup) {
        try {
            // 规范化棋子类型格式（连字符转下划线）
            if (boardSetup.getPieces() != null) {
                for (CourseRequestVO.PieceVO piece : boardSetup.getPieces()) {
                    piece.setPiece(piece.getPiece().replace("-", "_"));
                }
            }
            
            String json = objectMapper.writeValueAsString(boardSetup);
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("转换棋盘设置失败", e);
            throw new RuntimeException("转换棋盘设置失败");
        }
    }
    
    /**
     * 转换期望移动为Map
     */
    private Map<String, Object> convertExpectedMoveToMap(CourseRequestVO.ExpectedMoveVO expectedMove) {
        try {
            String json = objectMapper.writeValueAsString(expectedMove);
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("转换期望移动失败", e);
            throw new RuntimeException("转换期望移动失败");
        }
    }
    
    /**
     * 转换步骤为响应VO
     */
    private CourseResponseVO.CourseStepResponseVO convertToStepResponseVO(ChessCourseStepExisting step) {
        CourseResponseVO.CourseStepResponseVO stepVO = new CourseResponseVO.CourseStepResponseVO();
        stepVO.setId(step.getId().toString());
        stepVO.setType(step.getStepType());
        stepVO.setMessage(step.getMessage());
        stepVO.setCorrectMessage(step.getCorrectMessage());
        stepVO.setErrorMessage(step.getErrorMessage());
        stepVO.setHintMessage(step.getHintMessage());
        stepVO.setOrder(step.getStepOrder());
        
        // 转换棋盘设置
        if (step.getBoardSetup() != null || step.getBoardClear() != null) {
            CourseResponseVO.BoardSetupResponseVO boardSetup = new CourseResponseVO.BoardSetupResponseVO();
            boardSetup.setClear(step.getBoardClear());
            
            if (step.getBoardSetup() != null) {
                try {
                    String json = objectMapper.writeValueAsString(step.getBoardSetup());
                    CourseResponseVO.BoardSetupResponseVO boardSetupFromJson = objectMapper.readValue(json, CourseResponseVO.BoardSetupResponseVO.class);
                    if (boardSetupFromJson.getPieces() != null) {
                        // 将数据库中的下划线格式转换为前端期望的连字符格式
                        for (CourseResponseVO.PieceResponseVO piece : boardSetupFromJson.getPieces()) {
                            piece.setPiece(piece.getPiece().replace("_", "-"));
                        }
                        boardSetup.setPieces(boardSetupFromJson.getPieces());
                    }
                } catch (Exception e) {
                    log.error("转换棋盘设置响应失败", e);
                }
            }
            stepVO.setBoardSetup(boardSetup);
        }
        
        // 转换期望移动
        if (step.getExpectedFromRow() != null || step.getExpectedFromCol() != null || 
            step.getExpectedToRow() != null || step.getExpectedToCol() != null) {
            CourseResponseVO.ExpectedMoveResponseVO expectedMove = new CourseResponseVO.ExpectedMoveResponseVO();
            
            if (step.getExpectedFromRow() != null && step.getExpectedFromCol() != null) {
                CourseResponseVO.PositionResponseVO from = new CourseResponseVO.PositionResponseVO();
                from.setRow(step.getExpectedFromRow());
                from.setCol(step.getExpectedFromCol());
                expectedMove.setFrom(from);
            }
            
            if (step.getExpectedToRow() != null && step.getExpectedToCol() != null) {
                CourseResponseVO.PositionResponseVO to = new CourseResponseVO.PositionResponseVO();
                to.setRow(step.getExpectedToRow());
                to.setCol(step.getExpectedToCol());
                expectedMove.setTo(to);
            }
            
            stepVO.setExpectedMove(expectedMove);
        }
        
        return stepVO;
    }
}