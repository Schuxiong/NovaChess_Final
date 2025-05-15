package org.jeecg.modules.chess.stockfish;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Stockfish 引擎的评估结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockfishEvaluation {

  /**
   * 最佳移动（例如：e2e4）
   */
  private String bestMove;

  /**
   * 评估分数
   * 正值表示白方优势，负值表示黑方优势
   */
  private Float score;

  /**
   * 评估深度
   */
  private Integer depth;

  /**
   * 是否是获胜移动
   */
  private Boolean isMate;

  /**
   * 如果是获胜移动，还需要多少步能够将军
   */
  private Integer mateInMoves;

  /**
   * 所有最佳线路的列表（当设置了MultiPV > 1时）
   */
  private List<String> bestLines;

  /**
   * 原始的UCI输出
   */
  private String rawOutput;
}