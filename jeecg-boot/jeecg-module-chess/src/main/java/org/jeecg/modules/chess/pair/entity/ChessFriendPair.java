package org.jeecg.modules.chess.pair.entity;

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
 * @Description: 对弈关系
 * @Author: jeecg-boot
 * @Date:   2025-04-25
 * @Version: V1.0
 */
@Data
@TableName("chess_friend_pair")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="对弈关系")
public class ChessFriendPair {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
	private java.lang.String id;
	/**发起用户id*/
	@Excel(name = "发起用户id", width = 15)
    @Schema(description = "发起用户id")
	private java.lang.String sourceUserId;
	/**发起用户账号*/
	@Excel(name = "发起用户账号", width = 15)
    @Schema(description = "发起用户账号")
	private java.lang.String sourceUserAccount;
	/**1：黑色；2: 白色*/
	@Excel(name = "1：黑色；2: 白色", width = 15)
    @Schema(description = "1：黑色；2: 白色")
	private java.lang.Integer sourceOnePart;
	/**接受用户id*/
	@Excel(name = "接受用户id", width = 15)
    @Schema(description = "接受用户id")
	private java.lang.String acceptUserId;
	/**接受用户账号*/
	@Excel(name = "接受用户账号", width = 15)
    @Schema(description = "接受用户账号")
	private java.lang.String acceptUserAccount;
	/**1：黑色；2: 白色*/
	@Excel(name = "1：黑色；2: 白色", width = 15)
    @Schema(description = "1：黑色；2: 白色")
	private java.lang.Integer acceptOnePart;
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
