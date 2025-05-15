# Stockfish 国际象棋引擎集成

此模块提供了与 Stockfish 国际象棋引擎的集成，使用 [neat-chess](https://github.com/nomemory/neat-chess) Java UCI 库来与 Stockfish 引擎通信。

## 前提条件

1. 需要在服务器上安装 Stockfish 引擎。

   **方法一：配置环境变量（推荐）**
   - 从 [Stockfish 官方网站](https://stockfishchess.org/download/) 下载适合您服务器操作系统的版本
   - 将可执行文件重命名为 `stockfish`（Windows 上为 `stockfish.exe`）
   - 将可执行文件放置在系统 PATH 环境变量包含的目录中

   **方法二：指定完整路径**
   - 从 [Stockfish 官方网站](https://stockfishchess.org/download/) 下载适合您服务器操作系统的版本
   - 将可执行文件放置在任意目录中
   - 在配置文件中指定可执行文件的完整路径（参见下面的配置说明）

## 配置

在 `application-stockfish.yml` 文件中提供了 Stockfish 引擎的基本配置：

```yaml
chess:
  stockfish:
    # 默认分析深度
    defaultDepth: 16
    # 默认分析时间（毫秒）
    defaultTimeMs: 5000
    # 分析时间限制（毫秒）
    timeoutMs: 30000
    # 多路线分析（MultiPV）的数量
    multiPv: 3
    # Stockfish可执行文件的完整路径
    # 例如: C:/Program Files/Stockfish/stockfish.exe
    # 如果为空，则尝试从PATH环境变量中查找"stockfish"或"stockfish.exe"
    executablePath: 
```

**重要说明：**
- 如果您使用**方法一**安装 Stockfish，可以保留 `executablePath` 为空。
- 如果您使用**方法二**安装 Stockfish，必须在 `executablePath` 中指定 Stockfish 可执行文件的完整路径。
- Windows 系统路径示例：`C:/Program Files/Stockfish/stockfish.exe`（注意使用正斜杠 `/`）
- Linux/macOS 系统路径示例：`/usr/local/bin/stockfish`

要启用这些配置，请在您的主 `application.yml` 或 `bootstrap.yml` 文件中添加：

```yaml
spring:
  profiles:
    include:
      - stockfish
```

## API 接口

Stockfish 服务提供以下 RESTful API 接口：

1. `GET /chess/stockfish/evaluate?fen={fen}` - 使用默认设置评估棋局
2. `GET /chess/stockfish/evaluate/depth/{depth}?fen={fen}` - 使用指定深度评估棋局
3. `GET /chess/stockfish/evaluate/time/{timeMs}?fen={fen}` - 使用指定时间评估棋局
4. `GET /chess/stockfish/best-move?fen={fen}` - 获取棋局的最佳移动

其中：
- `fen` 参数是棋局的 FEN（Forsyth-Edwards Notation）表示法
- `depth` 参数是分析深度
- `timeMs` 参数是分析时间（毫秒）

## 示例用法

### 在其他 Java 服务中使用

```java
@Autowired
private StockfishService stockfishService;

// 使用默认设置评估棋局
StockfishEvaluation evaluation = stockfishService.evaluatePosition(
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

// 获取最佳移动
String bestMove = stockfishService.getBestMove(
    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
```

### HTTP 请求示例

```
GET /chess/stockfish/evaluate?fen=rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR%20w%20KQkq%20-%200%201
```

响应示例：

```json
{
  "bestMove": "e2e4",
  "score": 0.32,
  "depth": 16,
  "isMate": false,
  "mateInMoves": null,
  "bestLines": [
    "e2e4 (分数: 0.32, 后续: e7e5 g1f3 b8c6)",
    "d2d4 (分数: 0.28, 后续: d7d5 c2c4 e7e6)",
    "c2c4 (分数: 0.21, 后续: e7e5 g1f3 b8c6)"
  ]
}
``` 