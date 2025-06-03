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
 * @Description: 行棋
 * @Author: jeecg-boot
 * @Date: 2025-04-27
 * @Version: V1.0
 */
@Data
@TableName("chess_move")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "行棋")
public class ChessMove {

	/** id */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "id")
	private java.lang.String id;
	/** 游戏id */
	@Excel(name = "游戏id", width = 15)
	@Schema(description = "游戏id")
	private java.lang.String chessGameId;
	/** 棋子id */
	@Excel(name = "棋子id", width = 15)
	@Schema(description = "棋子id")
	private java.lang.String chessPiecesId;
	/** 棋子类别 1:黑 深色，2: 白 浅色 */
	@Excel(name = "棋子类别 1:黑 深色，2: 白 浅色", width = 15)
	@Schema(description = "棋子类别 1:黑 深色，2: 白 浅色")
	private java.lang.Integer piecesType;
	/** 用户ID */
	@Excel(name = "用户ID", width = 15)
	@Schema(description = "用户ID")
	private java.lang.String userId;
	/** 开始X轴位置 */
	@Excel(name = "开始X轴位置", width = 15)
	@Schema(description = "开始X轴位置")
	private java.lang.String fromPositionX;
	/** 开始Y轴位置 */
	@Excel(name = "开始Y轴位置", width = 15)
	@Schema(description = "开始Y轴位置")
	private java.lang.String fromPositionY;
	/** 目标X轴位置 */
	@Excel(name = "目标X轴位置", width = 15)
	@Schema(description = "目标X轴位置")
	private java.lang.String toPositionX;
	/** 目标Y轴位置 */
	@Excel(name = "目标Y轴位置", width = 15)
	@Schema(description = "目标Y轴位置")
	private java.lang.String toPositionY;
	/** 被吃的棋子id */
	@Excel(name = "被吃的棋子id", width = 15)
	@Schema(description = "被吃的棋子id")
	private java.lang.String tookPiecesId;
	/** 行棋状态 1:正常，2:悔棋 */
	@Excel(name = "行棋状态 1:正常，2:悔棋", width = 15)
	@Schema(description = "行棋状态 1:正常，2:悔棋")
	private java.lang.Integer moveState;
	/** 创建人 */
	@Excel(name = "创建人", width = 15)
	@Schema(description = "创建人")
	private java.lang.String createBy;
	/** 创建日期 */
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "创建日期")
	private java.util.Date createTime;
	/** 更新人 */
	@Excel(name = "更新人", width = 15)
	@Schema(description = "更新人")
	private java.lang.String updateBy;
	/** 更新日期 */
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "更新日期")
	private java.util.Date updateTime;
	/** 所属部门 */
	@Excel(name = "所属部门", width = 15)
	@Schema(description = "所属部门")
	private java.lang.String sysOrgCode;
	/** 删除状态 */
	@Excel(name = "删除状态", width = 15)
	@Schema(description = "删除状态")
	private java.lang.Integer delFlag;
	
	/** 走棋顺序 */
	@Excel(name = "走棋顺序", width = 15)
	@Schema(description = "走棋顺序")
	private java.lang.Integer moveSequence;
	
	/** 走棋耗时（秒） */
	@Excel(name = "走棋耗时（秒）", width = 15)
	@Schema(description = "走棋耗时（秒）")
	private java.lang.Integer moveDurationSeconds;
}
