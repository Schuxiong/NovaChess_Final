package org.jeecg.modules.chess.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 课程响应VO
 */
@Data
@Schema(description = "课程响应数据")
public class CourseResponseVO {
    
    @Schema(description = "课程ID")
    private String id;
    
    @Schema(description = "课程标题")
    private String title;
    
    @Schema(description = "课程描述")
    private String description;
    
    @Schema(description = "课程分类：basic/intermediate/advanced")
    private String category;
    
    @Schema(description = "课程图标URL")
    private String icon;
    
    @Schema(description = "课程步骤列表")
    private List<CourseStepResponseVO> steps;
    
    /**
     * 课程步骤响应VO
     */
    @Data
    @Schema(description = "课程步骤响应数据")
    public static class CourseStepResponseVO {
        
        @Schema(description = "步骤ID")
        private String id;
        
        @Schema(description = "步骤类型：info/task")
        private String type;
        
        @Schema(description = "步骤说明文字")
        private String message;
        
        @Schema(description = "棋盘设置")
        private BoardSetupResponseVO boardSetup;
        
        @Schema(description = "期望移动")
        private ExpectedMoveResponseVO expectedMove;
        
        @Schema(description = "正确时的提示")
        private String correctMessage;
        
        @Schema(description = "错误时的提示")
        private String errorMessage;
        
        @Schema(description = "提示信息")
        private String hintMessage;
        
        @Schema(description = "步骤顺序")
        private Integer order;
    }
    
    /**
     * 棋盘设置响应VO
     */
    @Data
    @Schema(description = "棋盘设置响应数据")
    public static class BoardSetupResponseVO {
        
        @Schema(description = "是否清空棋盘")
        private Boolean clear;
        
        @Schema(description = "棋子列表")
        private List<PieceResponseVO> pieces;
    }
    
    /**
     * 棋子响应VO
     */
    @Data
    @Schema(description = "棋子响应数据")
    public static class PieceResponseVO {
        
        @Schema(description = "棋子类型")
        private String piece;
        
        @Schema(description = "棋子位置")
        private PositionResponseVO position;
    }
    
    /**
     * 位置响应VO
     */
    @Data
    @Schema(description = "位置响应数据")
    public static class PositionResponseVO {
        
        @Schema(description = "行号 (0-7)")
        private Integer row;
        
        @Schema(description = "列号 (0-7)")
        private Integer col;
    }
    
    /**
     * 期望移动响应VO
     */
    @Data
    @Schema(description = "期望移动响应数据")
    public static class ExpectedMoveResponseVO {
        
        @Schema(description = "起始位置")
        private PositionResponseVO from;
        
        @Schema(description = "目标位置")
        private PositionResponseVO to;
    }
}