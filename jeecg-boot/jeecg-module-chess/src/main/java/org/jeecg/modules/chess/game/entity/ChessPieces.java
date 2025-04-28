package org.jeecg.modules.chess.game.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 棋子位置
 * @Author: jeecg-boot
 * @Date:   2025-04-27
 * @Version: V1.0
 */
@Data
@TableName("chess_pieces")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="棋子位置")
public class ChessPieces {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
	private java.lang.String id;
	/**游戏id*/
	@Excel(name = "游戏id", width = 15)
    @Schema(description = "游戏id")
	private java.lang.String chessGameId;
	/**棋子名*/
	@Excel(name = "棋子名", width = 15)
    @Schema(description = "棋子名")
	private java.lang.String chessPiecesName;
	/**棋子类别 1:黑 深色，2: 白 浅色*/
	@Excel(name = "棋子类别 1:黑 深色，2: 白 浅色", width = 15)
    @Schema(description = "棋子类别 1:黑 深色，2: 白 浅色")
	private java.lang.Integer piecesType;
	/**X轴*/
	@Excel(name = "X轴", width = 15)
    @Schema(description = "X轴")
	private java.lang.String positionX;
	/**Y轴*/
	@Excel(name = "Y轴", width = 15)
    @Schema(description = "Y轴")
	private java.lang.String positionY;
	/**棋子状态 1:正常，2:被吃了，不显示*/
	@Excel(name = "棋子状态 1:正常，2:被吃了，不显示", width = 15)
    @Schema(description = "棋子状态 1:正常，2:被吃了，不显示")
	private java.lang.Integer piecesState;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @Schema(description = "创建人")
	private java.lang.String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @Schema(description = "更新人")
	private java.lang.String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
	private java.util.Date updateTime;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    @Schema(description = "所属部门")
	private java.lang.String sysOrgCode;
	/**删除状态*/
	@Excel(name = "删除状态", width = 15)
    @Schema(description = "删除状态")
	private java.lang.Integer delFlag;
}
