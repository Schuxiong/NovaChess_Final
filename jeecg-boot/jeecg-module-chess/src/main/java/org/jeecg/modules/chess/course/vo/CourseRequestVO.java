package org.jeecg.modules.chess.course.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 课程请求VO
 */
@Data
@Schema(description = "课程请求数据")
public class CourseRequestVO {
    
    @Schema(description = "课程ID（编辑时需要）")
    private String id;
    
    @NotBlank(message = "课程标题不能为空")
    @Schema(description = "课程标题")
    private String title;
    
    @Schema(description = "课程描述")
    private String description;
    
    @Schema(description = "课程分类：basic/intermediate/advanced")
    private String category = "basic";
    
    @Schema(description = "课程图标URL或base64")
    private String icon;  // 对应数据库的icon_url字段
    
    @NotEmpty(message = "课程步骤不能为空")
    @Valid
    @Schema(description = "课程步骤列表")
    private List<CourseStepVO> steps;
    
    /**
     * 课程步骤VO
     */
    @Data
    @Schema(description = "课程步骤数据")
    public static class CourseStepVO {
        
        @NotBlank(message = "步骤类型不能为空")
        @Schema(description = "步骤类型：intro/info/task/demo")
        private String type;
        
        @Schema(description = "步骤说明文字")
        private String message;
        
        @Schema(description = "棋盘设置")
        private BoardSetupVO boardSetup;
        
        @Schema(description = "期望移动")
        private ExpectedMoveVO expectedMove;
        
        @Schema(description = "正确时的提示")
        private String correctMessage;
        
        @Schema(description = "错误时的提示")
        private String errorMessage;
        
        @Schema(description = "提示信息")
        private String hintMessage;
    }
    
    /**
     * 棋盘设置VO
     */
    @Data
    @Schema(description = "棋盘设置数据")
    public static class BoardSetupVO {
        
        @Schema(description = "是否清空棋盘")
        private Boolean clear = true;
        
        @Schema(description = "棋子列表")
        private List<PieceVO> pieces;
    }
    
    /**
     * 棋子VO
     */
    @Data
    @Schema(description = "棋子数据")
    public static class PieceVO {
        
        @NotBlank(message = "棋子类型不能为空")
        @Schema(description = "棋子类型")
        private String piece;
        
        @NotNull(message = "棋子位置不能为空")
        @Valid
        @Schema(description = "棋子位置")
        private PositionVO position;
    }
    
    /**
     * 位置VO
     */
    @Data
    @Schema(description = "位置数据")
    public static class PositionVO {
        
        @NotNull(message = "行号不能为空")
        @Schema(description = "行号 (0-7)")
        private Integer row;
        
        @NotNull(message = "列号不能为空")
        @Schema(description = "列号 (0-7)")
        private Integer col;
    }
    
    /**
     * 期望移动VO
     */
    @Data
    @Schema(description = "期望移动数据")
    public static class ExpectedMoveVO {
        
        @Valid
        @Schema(description = "起始位置")
        private PositionVO from;
        
        @Valid
        @Schema(description = "目标位置")
        private PositionVO to;
    }
}