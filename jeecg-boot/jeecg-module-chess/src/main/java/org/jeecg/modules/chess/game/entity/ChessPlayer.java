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
 * @Description: 游戏参与者
 * @Author: jeecg-boot
 * @Date:   2025-04-26
 * @Version: V1.0
 */
@Data
@TableName("chess_player")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="游戏参与者")
public class ChessPlayer {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
	private java.lang.String id;
	/**游戏id*/
	@Excel(name = "游戏id", width = 15)
    @Schema(description = "游戏id")
	private java.lang.String chessGameId;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
    @Schema(description = "用户id")
	private java.lang.String userId;
	/**用户账号*/
	@Excel(name = "用户账号", width = 15)
    @Schema(description = "用户账号")
	private java.lang.String userAccount;
	/**执子方：1:黑 深色，2: 白 浅色*/
	@Excel(name = "执子方：1:黑 深色，2: 白 浅色", width = 15)
    @Schema(description = "执子方：1:黑 深色，2: 白 浅色")
	private java.lang.Integer holdChess;
	/**参与者水平：1:一级，2: 二级*/
	@Excel(name = "参与者水平：1:一级，2: 二级", width = 15)
    @Schema(description = "参与者水平：1:一级，2: 二级")
	private java.lang.Integer playLevel;
	/**参与者类型：1:人，2: 机器*/
	@Excel(name = "参与者类型：1:人，2: 机器", width = 15)
    @Schema(description = "参与者类型：1:人，2: 机器")
	private java.lang.Integer playType;
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
