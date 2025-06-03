package org.jeecg.modules.chess.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 用户对局统计信息VO
 * @Author: jeecg-boot
 * @Date: 2025-01-27
 * @Version: V1.0
 */
@Data
@Schema(description = "用户对局统计信息")
public class UserGameStatisticsVO {
    
    /** 用户ID */
    @Schema(description = "用户ID")
    private String userId;
    
    /** 用户账号 */
    @Schema(description = "用户账号")
    private String userAccount;
    
    /** 总对局场次 */
    @Schema(description = "总对局场次")
    private Integer totalGames;
    
    /** 胜利场次 */
    @Schema(description = "胜利场次")
    private Integer winGames;
    
    /** 失败场次 */
    @Schema(description = "失败场次")
    private Integer loseGames;
    
    /** 和棋场次 */
    @Schema(description = "和棋场次")
    private Integer drawGames;
    
    /** 当前积分 */
    @Schema(description = "当前积分")
    private Integer currentScore;
    
    /** 胜率（百分比，保留两位小数） */
    @Schema(description = "胜率（百分比，保留两位小数）")
    private Double winRate;
    
    /** 和棋率（百分比，保留两位小数） */
    @Schema(description = "和棋率（百分比，保留两位小数）")
    private Double drawRate;
    
    /** 败率（百分比，保留两位小数） */
    @Schema(description = "败率（百分比，保留两位小数）")
    private Double loseRate;
}