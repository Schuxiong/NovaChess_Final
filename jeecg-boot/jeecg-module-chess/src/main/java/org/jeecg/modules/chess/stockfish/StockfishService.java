package org.jeecg.modules.chess.stockfish;

import lombok.extern.slf4j.Slf4j;
import net.andreinc.neatchess.client.UCI;
import net.andreinc.neatchess.client.model.Analysis;
import net.andreinc.neatchess.client.model.BestMove;
import net.andreinc.neatchess.client.model.Move;
import net.andreinc.neatchess.client.exception.UCIRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Stockfish国际象棋引擎服务
 * 基于neat-chess库实现与Stockfish的UCI协议通信
 */
@Slf4j
@Service
public class StockfishService {

  /**
   * UCI客户端
   */
  private UCI uci;

  /**
   * 默认的分析深度
   */
  @Value("${chess.stockfish.defaultDepth:16}")
  private int defaultDepth;

  /**
   * 默认分析时间（毫秒）
   */
  @Value("${chess.stockfish.defaultTimeMs:5000}")
  private long defaultTimeMs;

  /**
   * 默认的超时时间（毫秒）
   */
  @Value("${chess.stockfish.timeoutMs:30000}")
  private long timeoutMs;

  /**
   * 默认的线路数（MultiPV）
   */
  @Value("${chess.stockfish.multiPv:1}")
  private int multiPv;

  /**
   * Stockfish可执行文件的路径
   * 如果为空，则尝试从PATH环境变量中查找"stockfish"或"stockfish.exe"
   */
  @Value("${chess.stockfish.executablePath:}")
  private String stockfishPath;

  /**
   * 初始化Stockfish引擎
   */
  @PostConstruct
  public void init() {
    try {
      log.info("初始化Stockfish引擎...");
      uci = new UCI(timeoutMs);

      String stockfishExecutable = System.getenv("STOCKFISH_EXECUTABLE_PATH");

      if (stockfishExecutable != null && !stockfishExecutable.trim().isEmpty()) {
        log.info("从环境变量 STOCKFISH_EXECUTABLE_PATH 获取Stockfish路径: {}", stockfishExecutable);
      } else if (stockfishPath != null && !stockfishPath.trim().isEmpty()) {
        log.info("从配置文件 chess.stockfish.executablePath 获取Stockfish路径: {}", stockfishPath);
        stockfishExecutable = stockfishPath;
      } else {
        log.info("环境变量和配置文件均未指定Stockfish路径，尝试从系统PATH中查找 'stockfish'");
        // 当 stockfishExecutable 为 null 或空时，uci.start() 会尝试执行 'stockfish'
        // 或者如果 uci.startStockfish() 存在并且是期望行为，也可以调用它
      }

      if (stockfishExecutable != null && !stockfishExecutable.trim().isEmpty()) {
        uci.start(stockfishExecutable);
      } else {
        // 假设 uci.startStockfish() 会尝试从PATH查找 'stockfish'
        uci.startStockfish();
      }

      // 设置MultiPV选项（分析多条线路）
      uci.setOption("MultiPV", String.valueOf(multiPv));

      log.info("Stockfish引擎初始化成功");
    } catch (Exception e) {
      log.error("初始化Stockfish引擎失败", e);
      throw new RuntimeException("无法初始化Stockfish引擎", e);
    }
  }

  /**
   * 关闭Stockfish引擎
   */
  @PreDestroy
  public void close() {
    if (uci != null) {
      try {
        log.info("关闭Stockfish引擎...");
        uci.close();
        log.info("Stockfish引擎已关闭");
      } catch (Exception e) {
        log.error("关闭Stockfish引擎时发生错误", e);
      }
    }
  }

  /**
   * 使用默认深度评估棋局
   * 
   * @param fen FEN表示法的棋局位置
   * @return 评估结果
   */
  public StockfishEvaluation evaluatePosition(String fen) {
    return evaluatePositionWithDepth(fen, defaultDepth);
  }

  /**
   * 使用指定深度评估棋局
   * 
   * @param fen   FEN表示法的棋局位置
   * @param depth 分析深度
   * @return 评估结果
   */
  public StockfishEvaluation evaluatePositionWithDepth(String fen, int depth) {
    try {
      log.debug("开始以深度[{}]评估棋局: {}", depth, fen);

      // 开始新游戏并设置位置
      uci.uciNewGame();
      uci.positionFen(fen);

      // 获取分析结果
      Analysis analysis = uci.analysis(depth).getResultOrThrow();

      // 将结果转换为我们的模型
      return convertToEvaluation(analysis);
    } catch (Exception e) {
      log.error("评估棋局失败", e);
      throw new RuntimeException("评估棋局失败: " + e.getMessage(), e);
    }
  }

  /**
   * 使用指定时间评估棋局
   * 
   * @param fen    FEN表示法的棋局位置
   * @param timeMs 分析时间（毫秒）
   * @return 评估结果
   */
  public StockfishEvaluation evaluatePositionWithTime(String fen, long timeMs) {
    try {
      log.debug("开始以时间[{}ms]评估棋局: {}", timeMs, fen);

      // 开始新游戏并设置位置
      uci.uciNewGame();
      uci.positionFen(fen);

      // 获取分析结果
      Analysis analysis = uci.analysis(timeMs).getResultOrThrow();

      // 将结果转换为我们的模型
      return convertToEvaluation(analysis);
    } catch (Exception e) {
      log.error("评估棋局失败", e);
      throw new RuntimeException("评估棋局失败: " + e.getMessage(), e);
    }
  }

  /**
   * 获取最佳移动（快速分析）
   * 
   * @param fen FEN表示法的棋局位置
   * @return 最佳移动
   */
  public String getBestMove(String fen) {
    try {
      log.debug("获取棋局的最佳移动: {}", fen);

      // 开始新游戏并设置位置
      uci.uciNewGame();
      uci.positionFen(fen);

      // 获取最佳移动
      BestMove bestMove = uci.bestMove(defaultTimeMs).getResultOrThrow();

      return bestMove.toString(); // BestMove提供toString方法返回走法
    } catch (Exception e) {
      log.error("获取最佳移动失败", e);
      throw new RuntimeException("获取最佳移动失败: " + e.getMessage(), e);
    }
  }

  /**
   * 将neat-chess的Analysis对象转换为自定义的StockfishEvaluation
   * 
   * @param analysis neat-chess的分析结果
   * @return 自定义评估结果
   */
  private StockfishEvaluation convertToEvaluation(Analysis analysis) {
    if (analysis == null) {
      return null;
    }

    StockfishEvaluation evaluation = new StockfishEvaluation();

    // 如果有结果
    if (!analysis.getAllMoves().isEmpty()) {
      // 获取最佳移动（第一名的线路）
      Move bestMove = analysis.getAllMoves().get(1);
      evaluation.setBestMove(bestMove.getLan());
      evaluation.setDepth(bestMove.getDepth());

      // 设置分数
      try {
        // 将分数转换为浮点数
        evaluation.setScore(Float.parseFloat(String.valueOf(bestMove.getStrength())));
      } catch (Exception e) {
        log.warn("无法解析分数: {}", bestMove.getStrength());
        evaluation.setScore(0.0f);
      }

      // 检查是否是将军
      evaluation.setIsMate(analysis.isMate());

      // 收集所有的最佳线路
      List<String> bestLines = new ArrayList<>();
      analysis.getAllMoves().forEach((idx, move) -> {
        StringBuilder line = new StringBuilder(move.getLan());
        line.append(" (分数: ").append(move.getStrength());

        // 添加后续步骤
        if (move.getContinuation() != null && move.getContinuation().length > 0) {
          line.append(", 后续: ");
          line.append(String.join(" ", move.getContinuation()));
        }

        line.append(")");
        bestLines.add(line.toString());
      });
      evaluation.setBestLines(bestLines);
    }

    return evaluation;
  }
}