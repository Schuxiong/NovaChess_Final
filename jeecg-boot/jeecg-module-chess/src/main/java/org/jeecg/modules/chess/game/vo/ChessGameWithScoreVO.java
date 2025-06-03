package org.jeecg.modules.chess.game.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 游戏对局信息（包含积分）
 * @Author: jeecg-boot
 * @Date: 2025-01-15
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "游戏对局信息（包含积分）")
public class ChessGameWithScoreVO {
    
    /** id */
    @Schema(description = "id")
    private java.lang.String id;
    
    /** 黑色id */
    @Excel(name = "黑色id", width = 15)
    @Schema(description = "黑色id")
    private java.lang.String blackPlayId;
    
    /** 黑色账号 */
    @Excel(name = "黑色账号", width = 15)
    @Schema(description = "黑色账号")
    private java.lang.String blackPlayAccount;
    
    /** 白色id */
    @Excel(name = "白色id", width = 15)
    @Schema(description = "白色id")
    private java.lang.String whitePlayId;
    
    /** 白色账号 */
    @Excel(name = "白色账号", width = 15)
    @Schema(description = "白色账号")
    private java.lang.String whitePlayAccount;
    
    /** 游戏状态。1:进行中 2:正常结束 3:黑方胜利 4:白方胜利 5:和棋 6:流局(超时) 7:黑方退出 8:白方退出 */
    @Excel(name = "游戏状态。1:进行中 2:正常结束 3:黑方胜利 4:白方胜利 5:和棋 6:流局(超时) 7:黑方退出 8:白方退出", width = 15)
    @Schema(description = "游戏状态。1:进行中 2:正常结束 3:黑方胜利 4:白方胜利 5:和棋 6:流局(超时) 7:黑方退出 8:白方退出")
    private java.lang.Integer gameState;
    
    /** 游戏类型，1:人人对赛 2：人机对赛3:天梯赛 */
    @Excel(name = "游戏类型，1:人人对赛 2：人机对赛3:天梯赛", width = 15)
    @Schema(description = "游戏类型，1:人人对赛 2：人机对赛3:天梯赛")
    private java.lang.Integer gameType;
    
    /** 当前轮到哪方下棋，1:黑方，2:白方 */
    @Excel(name = "当前轮到哪方下棋，1:黑方，2:白方", width = 15)
    @Schema(description = "当前轮到哪方下棋，1:黑方，2:白方")
    private java.lang.Integer currentTurn;
    
    /** 邀请ID，用于关联邀请记录 */
    @Excel(name = "邀请ID，用于关联邀请记录", width = 15)
    @Schema(description = "邀请ID，用于关联邀请记录")
    private java.lang.String sourceInviteId;
    
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
    
    // 积分相关字段
    /** 黑方积分 */
    @Schema(description = "黑方积分")
    private java.lang.Integer blackPlayerScore;
    
    /** 白方积分 */
    @Schema(description = "白方积分")
    private java.lang.Integer whitePlayerScore;
}