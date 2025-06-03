package org.jeecg.modules.chess.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@TableName(value = "chess_course_step", autoResultMap = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="国际象棋课程步骤")
public class ChessCourseStepNew implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "步骤ID")
    private String id;

    @Excel(name = "所属课程ID", width = 15)
    @Schema(description = "所属课程ID")
    private String courseId;

    @Excel(name = "步骤类型", width = 15)
    @Schema(description = "步骤类型：info/task")
    private String type;

    @Excel(name = "消息内容", width = 30)
    @Schema(description = "步骤展示的消息内容")
    private String message;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "棋盘设置JSON")
    private Map<String, Object> boardSetup;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "期望移动JSON")
    private Map<String, Object> expectedMove;

    @Excel(name = "正确操作提示", width = 30)
    @Schema(description = "正确操作提示信息")
    private String correctMessage;

    @Excel(name = "错误操作提示", width = 30)
    @Schema(description = "错误操作提示信息")
    private String errorMessage;

    @Excel(name = "提示信息", width = 30)
    @Schema(description = "提示信息")
    private String hintMessage;

    @Excel(name = "步骤顺序", width = 15)
    @Schema(description = "步骤顺序")
    private Integer stepOrder;

    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;
}