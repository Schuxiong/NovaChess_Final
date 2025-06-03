package org.jeecg.modules.chess.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 课程步骤实体类（匹配现有表结构）
 */
@Data
@TableName("chess_course_steps")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "课程步骤")
public class ChessCourseStepExisting implements Serializable {
    private static final long serialVersionUID = 1L;

    /**步骤ID，自增主键*/
    @TableId(type = IdType.AUTO)
    @Schema(description = "步骤ID")
    private Integer id;

    /**所属课程ID*/
    @Excel(name = "课程ID", width = 15)
    @Schema(description = "所属课程ID")
    private String courseId;

    /**步骤顺序*/
    @Excel(name = "步骤顺序", width = 15)
    @Schema(description = "步骤顺序")
    private Integer stepOrder;

    /**步骤类型：介绍或任务*/
    @Excel(name = "步骤类型", width = 15)
    @Schema(description = "步骤类型：intro/task")
    private String stepType;

    /**步骤展示的消息内容*/
    @Excel(name = "消息内容", width = 30)
    @Schema(description = "步骤展示的消息内容")
    private String message;

    /**是否清空棋盘*/
    @Excel(name = "是否清空棋盘", width = 15)
    @Schema(description = "是否清空棋盘")
    private Boolean boardClear;

    /**棋盘设置JSON*/
    @Schema(description = "棋盘设置JSON")
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> boardSetup;

    /**期望起始行*/
    @Excel(name = "期望起始行", width = 15)
    @Schema(description = "期望起始行")
    private Integer expectedFromRow;

    /**期望起始列*/
    @Excel(name = "期望起始列", width = 15)
    @Schema(description = "期望起始列")
    private Integer expectedFromCol;

    /**期望目标行*/
    @Excel(name = "期望目标行", width = 15)
    @Schema(description = "期望目标行")
    private Integer expectedToRow;

    /**期望目标列*/
    @Excel(name = "期望目标列", width = 15)
    @Schema(description = "期望目标列")
    private Integer expectedToCol;

    /**正确操作提示信息*/
    @Excel(name = "正确提示", width = 30)
    @Schema(description = "正确操作提示信息")
    private String correctMessage;

    /**错误操作提示信息*/
    @Excel(name = "错误提示", width = 30)
    @Schema(description = "错误操作提示信息")
    private String errorMessage;

    /**提示信息*/
    @Excel(name = "提示信息", width = 30)
    @Schema(description = "提示信息")
    private String hintMessage;
}