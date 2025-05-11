# NovaChess WebSocket 使用指南

## 连接建立

1. 建立WebSocket连接:

```javascript
// 建立STOMP连接
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, onConnected, onError);

function onConnected() {
  console.log("已建立连接");
  
  // 进入对局
  joinChessGame(gameId, userId, username);

  // 订阅棋盘信息变更
  stompClient.subscribe('/topic/chessboard', onChessboardUpdated);
  
  // 订阅特定游戏的消息
  stompClient.subscribe('/topic/game/' + gameId, onGameEvent);
}

function onError(error) {
  console.error('WebSocket连接失败:', error);
}
```

## 加入游戏

```javascript
function joinChessGame(gameId, userId, username) {
  // 加入游戏
  stompClient.send(
    `/app/game/join/${gameId}`,
    {},
    JSON.stringify({
      gameId: gameId,
      userId: userId,
      username: username
    })
  );
}
```

## 处理棋盘信息

```javascript
function onChessboardUpdated(payload) {
  const response = JSON.parse(payload.body);
  console.log("收到棋盘信息:", response);
  
  if (response.success && response.result) {
    const gameState = response.result;
    
    // 检查棋子列表
    if (!gameState.chessPiecesList || gameState.chessPiecesList.length === 0) {
      console.warn("警告：棋子列表为空，正在请求初始化棋盘");
      initializeChessboard(gameId, userId);
      return;
    }
    
    // 更新棋盘
    updateGameBoard(gameState);
  }
}

function initializeChessboard(gameId, userId) {
  stompClient.send(
    "/app/game/initialize",
    {},
    JSON.stringify({
      gameId: gameId,
      userId: userId
    })
  );
}
```

## 处理游戏事件

```javascript
function onGameEvent(payload) {
  const message = JSON.parse(payload.body);
  console.log("收到游戏事件:", message);
  
  // 根据事件类型处理
  switch(message.type) {
    case "MOVE_UPDATE":
      handleMoveUpdate(message.payload);
      break;
    case "PLAYER_JOIN":
      handlePlayerJoin(message.payload);
      break;
    case "GAME_INITIALIZED":
      handleGameInitialized(message.payload);
      break;
    default:
      console.log("未处理的事件类型:", message.type);
  }
}

function handleMoveUpdate(payload) {
  // 获取最新棋盘状态
  if (payload.latestGameState) {
    updateGameBoard(payload.latestGameState);
    
    // 检查是否轮到当前玩家
    checkPlayerTurn(payload.latestGameState);
  }
}
```

## 走棋操作

```javascript
function makeMove(fromX, fromY, toX, toY) {
  stompClient.send(
    "/app/movepieces",
    {},
    JSON.stringify({
      gameId: gameId,
      userId: userId,
      fromPosition: {
        x: fromX,
        y: fromY
      },
      toPosition: {
        x: toX,
        y: toY
      }
    })
  );
}
```

## 坐标系统说明

棋盘坐标系统为:
- X坐标: A-H (对应0-7)
- Y坐标: 1-8 (对应7-0)

## 提示

1. 如果收到空棋盘，主动调用`/app/game/initialize`请求初始化
2. 确保gameId和userId都是字符串格式
3. 游戏中的所有更新都通过`/topic/game/{gameId}`接收
4. 每次走棋后，都会收到包含最新棋盘状态的消息 