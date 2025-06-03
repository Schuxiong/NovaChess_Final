package org.jeecg.modules.chess.game.entity;

import java.io.Serializable;
import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 和棋请求
 * @Author: jeecg-boot
 * @Date: 2025-06-03
 * @Version: V1.0
 */
@Data
@TableName("chess_draw_request")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "和棋请求")
public class ChessDrawRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;

    /** 游戏ID */
    @Excel(name = "游戏ID", width = 15)
    @Schema(description = "游戏ID")
    private String gameId;

    /** 发起和棋请求的用户ID */
    @Excel(name = "发起和棋请求的用户ID", width = 15)
    @Schema(description = "发起和棋请求的用户ID")
    private String requestUserId;

    /** 发起和棋请求的用户账号 */
    @Excel(name = "发起和棋请求的用户账号", width = 15)
    @Schema(description = "发起和棋请求的用户账号")
    private String requestUserAccount;

    /** 接收和棋请求的用户ID */
    @Excel(name = "接收和棋请求的用户ID", width = 15)
    @Schema(description = "接收和棋请求的用户ID")
    private String targetUserId;

    /** 接收和棋请求的用户账号 */
    @Excel(name = "接收和棋请求的用户账号", width = 15)
    @Schema(description = "接收和棋请求的用户账号")
    private String targetUserAccount;

    /** 请求状态：1-待响应 2-已接受 3-已拒绝 4-已过期 */
    @Excel(name = "请求状态", width = 15)
    @Schema(description = "请求状态：1-待响应 2-已接受 3-已拒绝 4-已过期")
    private Integer status;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 创建日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;

    /** 更新人 */
    @Schema(description = "更新人")
    private String updateBy;

    /** 更新日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;

    /** 响应时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "响应时间")
    private Date responseTime;
}