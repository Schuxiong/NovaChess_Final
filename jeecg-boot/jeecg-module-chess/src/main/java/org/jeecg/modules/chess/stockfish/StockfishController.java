package org.jeecg.modules.chess.stockfish;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Stockfish评估API控制器
 */
@Slf4j
@RestController
@RequestMapping("/chess/stockfish")
@Tag(name = "Stockfish国际象棋引擎接口", description = "提供Stockfish引擎评估和分析功能的API")
public class StockfishController {

  @Autowired
  private StockfishService stockfishService;

  /**
   * 使用默认设置评估棋局
   *
   * @param fen 棋局的FEN表示法
   * @return Stockfish评估结果
   */
  @GetMapping("/evaluate")
  @Operation(summary = "评估棋局", description = "使用默认设置评估棋局位置")
  public StockfishEvaluation evaluatePosition(
      @Parameter(description = "棋局的FEN表示法", required = true) @RequestParam String fen) {

    log.info("接收到评估棋局请求，FEN: {}", fen);
    return stockfishService.evaluatePosition(fen);
  }

  /**
   * 使用指定深度评估棋局
   *
   * @param fen   棋局的FEN表示法
   * @param depth 分析深度
   * @return Stockfish评估结果
   */
  @GetMapping("/evaluate/depth/{depth}")
  @Operation(summary = "按深度评估棋局", description = "使用指定深度评估棋局位置")
  public StockfishEvaluation evaluatePositionWithDepth(
      @Parameter(description = "棋局的FEN表示法", required = true) @RequestParam String fen,
      @Parameter(description = "分析深度", required = true) @PathVariable int depth) {

    log.info("接收到按深度评估棋局请求，FEN: {}, 深度: {}", fen, depth);
    return stockfishService.evaluatePositionWithDepth(fen, depth);
  }

  /**
   * 使用指定时间评估棋局
   *
   * @param fen    棋局的FEN表示法
   * @param timeMs 分析时间（毫秒）
   * @return Stockfish评估结果
   */
  @GetMapping("/evaluate/time/{timeMs}")
  @Operation(summary = "按时间评估棋局", description = "使用指定时间评估棋局位置")
  public StockfishEvaluation evaluatePositionWithTime(
      @Parameter(description = "棋局的FEN表示法", required = true) @RequestParam String fen,
      @Parameter(description = "分析时间（毫秒）", required = true) @PathVariable long timeMs) {

    log.info("接收到按时间评估棋局请求，FEN: {}, 时间: {}ms", fen, timeMs);
    return stockfishService.evaluatePositionWithTime(fen, timeMs);
  }

  /**
   * 获取最佳移动（快速分析）
   *
   * @param fen 棋局的FEN表示法
   * @return 最佳移动（例如：e2e4）
   */
  @GetMapping("/best-move")
  @Operation(summary = "获取最佳移动", description = "快速分析棋局获取最佳移动")
  public String getBestMove(
      @Parameter(description = "棋局的FEN表示法", required = true) @RequestParam String fen) {

    log.info("接收到获取最佳移动请求，FEN: {}", fen);
    return stockfishService.getBestMove(fen);
  }
}