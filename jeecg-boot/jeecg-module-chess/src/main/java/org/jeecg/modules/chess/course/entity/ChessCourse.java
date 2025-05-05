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
import java.util.Date;

@Data
@TableName("chess_courses")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="国际象棋课程")
public class ChessCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "课程唯一标识")
    private String id;

    @Excel(name = "课程标题", width = 15)
    @Schema(description = "课程标题")
    private String title;

    @Excel(name = "课程描述", width = 30)
    @Schema(description = "课程描述")
    private String description;

    @Excel(name = "课程图标URL", width = 30)
    @Schema(description = "课程图标URL")
    private String iconUrl;

    @Excel(name = "课程分类", width = 15, dicCode = "chess_course_category")
    @Dict(dicCode = "chess_course_category")
    @Schema(description = "课程分类：basic(基础), advanced(高级), strategy(策略), other(其他)")
    private String category;

    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createdAt;

    @Excel(name = "最后更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后更新时间")
    private Date updatedAt;
}