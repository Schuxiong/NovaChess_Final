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
@TableName("chess_board_setups")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="棋盘初始设置")
public class ChessBoardSetup implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "初始设置ID，自增主键")
    private Integer id;

    @Excel(name = "关联步骤ID", width = 15)
    @Schema(description = "关联步骤ID")
    private Integer stepId;

    @Excel(name = "棋子类型", width = 15, dicCode = "chess_piece_type")
    @Dict(dicCode = "chess_piece_type")
    @Schema(description = "棋子类型，例如pawn、rook等")
    private String pieceType;

    @Excel(name = "棋子颜色", width = 15, dicCode = "chess_piece_color")
    @Dict(dicCode = "chess_piece_color")
    @Schema(description = "棋子颜色：white(白), black(黑)")
    private String pieceColor;

    @Excel(name = "棋子所在行", width = 15)
    @Schema(description = "棋子所在行")
    private Integer positionRow;

    @Excel(name = "棋子所在列", width = 15)
    @Schema(description = "棋子所在列")
    private Integer positionCol;
}