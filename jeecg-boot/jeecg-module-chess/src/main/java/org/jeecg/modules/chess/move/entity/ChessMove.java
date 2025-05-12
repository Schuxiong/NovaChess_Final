package org.jeecg.modules.chess.move.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 棋子移动记录
 * @Author: jeecg-boot
 * @Date: 2025-05-11
 * @Version: V1.0
 */
@Data
@TableName("chess_move")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "棋子移动记录")
public class ChessMove implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 主键
   */
  @TableId(type = IdType.ASSIGN_ID)
  @Schema(description = "主键")
  private String id;

  /**
   * 游戏ID
   */
  @Excel(name = "游戏ID", width = 15)
  @Schema(description = "游戏ID")
  private String chessGameId;

  /**
   * 棋子名称
   */
  @Excel(name = "棋子名称", width = 15)
  @Schema(description = "棋子名称")
  private String chessPiecesName;

  /**
   * 棋子类型（1-黑，2-白）
   */
  @Excel(name = "棋子类型", width = 15)
  @Schema(description = "棋子类型（1-黑，2-白）")
  private String piecesType;

  /**
   * 横坐标
   */
  @Excel(name = "横坐标", width = 15)
  @Schema(description = "横坐标")
  private String positionX;

  /**
   * 纵坐标
   */
  @Excel(name = "纵坐标", width = 15)
  @Schema(description = "纵坐标")
  private String positionY;

  /**
   * 棋子状态（1-正常，2-被吃，3-升变）
   */
  @Excel(name = "棋子状态", width = 15)
  @Schema(description = "棋子状态（1-正常，2-被吃，3-升变）")
  private String piecesState;

  /**
   * 创建人
   */
  @Excel(name = "创建人", width = 15)
  @Schema(description = "创建人")
  private String createBy;

  /**
   * 创建时间
   */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "创建时间")
  private Date createTime;

  /**
   * 更新人
   */
  @Excel(name = "更新人", width = 15)
  @Schema(description = "更新人")
  private String updateBy;

  /**
   * 更新时间
   */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "更新时间")
  private Date updateTime;

  /**
   * 组织机构编码
   */
  @Excel(name = "组织机构编码", width = 15)
  @Schema(description = "组织机构编码")
  private String sysOrgCode;

  /**
   * 删除标志（0-正常，1-已删除）
   */
  @Excel(name = "删除标志", width = 15)
  @Schema(description = "删除标志（0-正常，1-已删除）")
  private Integer delFlag;
}