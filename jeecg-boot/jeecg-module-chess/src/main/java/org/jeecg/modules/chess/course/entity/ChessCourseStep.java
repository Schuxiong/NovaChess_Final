package org.jeecg.modules.chess.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Data
@TableName("chess_course_steps")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="国际象棋课程步骤")
public class ChessCourseStep implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "步骤ID，自增主键")
    private Integer id;

    @Excel(name = "所属课程ID", width = 15)
    @Schema(description = "所属课程ID")
    private String courseId;

    @Excel(name = "步骤顺序", width = 15)
    @Schema(description = "步骤顺序")
    private Integer stepOrder;

    @Excel(name = "步骤类型", width = 15, dicCode = "chess_step_type")
    @Dict(dicCode = "chess_step_type")
    @Schema(description = "步骤类型：intro(介绍), task(任务)")
    private String stepType;

    @Excel(name = "消息内容", width = 30)
    @Schema(description = "步骤展示的消息内容")
    private String message;

    @Excel(name = "是否清空棋盘", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @Schema(description = "是否清空棋盘")
    private Boolean boardClear;

    @Excel(name = "期望起始行", width = 15)
    @Schema(description = "期望起始行（用于验证移动）")
    private Integer expectedFromRow;

    @Excel(name = "期望起始列", width = 15)
    @Schema(description = "期望起始列")
    private Integer expectedFromCol;

    @Excel(name = "期望目标行", width = 15)
    @Schema(description = "期望目标行")
    private Integer expectedToRow;

    @Excel(name = "期望目标列", width = 15)
    @Schema(description = "期望目标列")
    private Integer expectedToCol;

    @Excel(name = "正确操作提示", width = 30)
    @Schema(description = "正确操作提示信息")
    private String correctMessage;

    @Excel(name = "错误操作提示", width = 30)
    @Schema(description = "错误操作提示信息")
    private String errorMessage;

    @Excel(name = "提示信息", width = 30)
    @Schema(description = "提示信息")
    private String hintMessage;
}