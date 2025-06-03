# NovaChess 国际象棋对战和教学系统

<div align="center">
  <img src="https://pic1.imgdb.cn/item/67fbede088c538a9b5ceb4a3.png" alt="NovaChess Logo" width="200" />
  <br>
  <h3>现代化的国际象棋对战与教学平台</h3>
  
  <p>
    <img src="https://img.shields.io/badge/版本-1.2.0-blue" alt="版本" />
    <img src="https://img.shields.io/badge/许可证-MIT-green" alt="许可证" />
    <img src="https://img.shields.io/badge/平台-Web%20%7C%20小程序%20%7C%20App-orange" alt="平台" />
    <img src="https://img.shields.io/badge/Java-8%2B-red" alt="Java" />
    <img src="https://img.shields.io/badge/Vue.js-3.x-brightgreen" alt="Vue" />
  </p>
</div>

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/Schuxiong/NovaChess_Final)

## 📋 项目简介

NovaChess 是一个功能完善的国际象棋应用程序，采用前后端分离架构，包含了用户友好的前端界面和强大的后端服务。项目旨在提供一个多平台、功能丰富的国际象棋体验，包括在线对战、人机对弈、棋局分析、课程学习等。

### 🎯 核心特性

- 🌐 **多平台支持**：Web端、微信小程序、Android/iOS应用
- ⚡ **实时对战**：基于WebSocket的实时在线对战系统
- 🤖 **智能AI**：集成Stockfish引擎和大语言模型的多层次AI对手
- 📚 **教学系统**：完整的国际象棋学习课程和战术训练
- 🏆 **竞技系统**：积分排行榜和匹配系统
- 🔄 **棋局回放**：完整的对局记录和分析功能

## 🏗️ 项目架构

### 系统架构图

![NovaChess架构图](./NovaChess-Architecture.svg)

### 项目组成

本项目采用前后端分离的微服务架构，主要由以下几个部分组成：

#### 📱 前端应用 (`NovaChess-App/`)
- **技术栈**: Vue.js + uni-app
- **支持平台**: Web端、微信小程序、Android/iOS应用
- **核心功能**: 用户界面、棋盘渲染、实时通信、AI集成
- **智能机器人**: 集成DeepSeek/Kimi大语言模型和Stockfish引擎

#### ⚙️ 后端服务 (`jeecg-boot/`)
- **技术栈**: Spring Boot + JeecgBoot低代码平台
- **架构模式**: 微服务架构，支持分布式部署
- **核心模块**: 
  - `jeecg-module-chess`: 国际象棋业务模块
  - `jeecg-module-system`: 系统管理模块
  - `jeecg-module-demo`: 演示模块

#### 🗄️ 数据层
- **`DDL_chess.sql`**: 核心数据库表结构定义
- **`sql/`**: 数据迁移和修复脚本集合
- **数据库**: MySQL 5.7+ 支持

#### 🔧 辅助工具
- **`testSocket.html`**: WebSocket连接测试工具
- **`NovaChess-Architecture.svg`**: 系统架构图
- **配置文件**: 各种环境配置和部署脚本

## 💻 技术栈

### 🎨 前端技术栈 (`NovaChess-App`)

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue.js** | 3.x | 渐进式JavaScript框架 |
| **uni-app** | 最新版 | 跨平台开发框架 |
| **Vuex** | 4.x | 状态管理 |
| **WebSocket** | - | 实时通信 |
| **SockJS + STOMP** | - | WebSocket协议栈 |
| **JavaScript** | ES6+ | 开发语言 |
| **npm** | - | 包管理器 |

#### 🤖 AI集成
- **Stockfish引擎**: 传统国际象棋AI引擎
- **DeepSeek API**: 大语言模型AI对手
- **Kimi API**: 备用大语言模型

### ⚙️ 后端技术栈 (`jeecg-boot`)

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 2.7.18 | 核心框架 |
| **JeecgBoot** | 3.7.4 | 低代码开发平台 |
| **Java** | 8+ (支持17) | 开发语言 |
| **Maven** | 3.6+ | 依赖管理 |
| **MyBatis-Plus** | 3.5.3.2 | ORM框架 |
| **Apache Shiro** | 1.12.0 | 安全框架 |
| **JWT** | 3.11.0 | 身份认证 |
| **MySQL** | 5.7+ | 关系型数据库 |
| **Redis** | 6.0+ | 缓存数据库 |

#### 🌐 微服务支持 (可选)
- **Spring Cloud Alibaba**: 微服务生态
- **Nacos**: 服务发现与配置管理
- **Gateway**: API网关
- **Sentinel**: 流量控制与熔断

### 🗄️ 数据库设计

#### 核心数据表

| 表名 | 功能 | 说明 |
|------|------|------|
| **课程系统** | | |
| `chess_courses` | 课程管理 | 国际象棋教学课程 |
| `chess_course_steps` | 课程步骤 | 课程的具体学习步骤 |
| `chess_board_setups` | 棋盘设置 | 课程中的棋盘布局 |
| **对局系统** | | |
| `chess_game` | 对局记录 | 游戏对局的基本信息 |
| `chess_move` | 走棋记录 | 每一步棋的详细记录 |
| `chess_pieces` | 棋子状态 | 棋盘上棋子的位置状态 |
| `chess_player` | 玩家信息 | 对局中的玩家信息 |
| **社交系统** | | |
| `chess_friend_pair` | 好友关系 | 好友约战功能 |
| **积分系统** | | |
| `chess_player_score` | 玩家积分 | 用户积分和等级 |
| `chess_player_score_record` | 积分记录 | 积分变化历史 |
| **系统管理** | | |
| `sys_user` | 用户管理 | 系统用户信息 |
| `sys_depart` | 部门管理 | 组织架构 |
| `sys_message` | 消息管理 | 系统通知和消息 |

### 3.4. 版本控制

*   Git

## ✨ 主要功能

### 🎮 核心游戏功能

#### 🏆 在线对战系统
- **实时匹配**: 基于积分的智能匹配算法
- **好友约战**: 邀请好友进行私人对局
- **观战功能**: 观看其他玩家的对局
- **实时通信**: WebSocket实现的低延迟通信
- **断线重连**: 自动恢复对局状态

#### 🤖 人机对战系统
- **多层次AI**: 
  - Stockfish引擎 (传统象棋AI)
  - DeepSeek/Kimi大语言模型 (智能对话式AI)
- **难度调节**: 可调整AI思考深度和强度
- **AI分析**: 实时局面评估和最佳走法建议

#### ♟️ 完整棋局功能
- **标准规则**: 完整的国际象棋规则实现
  - 王车易位 (Castling)
  - 吃过路兵 (En Passant)
  - 兵升变 (Pawn Promotion)
- **游戏状态检测**:
  - 将军 (Check) 检测
  - 将杀 (Checkmate) 判定
  - 和棋 (Draw) 条件判断
- **计时系统**: 多种时间控制模式
- **走棋记录**: 完整的PGN格式棋谱

### 📚 教学与学习系统

#### 📖 规则入门
- **交互式教程**: 图文并茂的规则讲解
- **实战演练**: 边学边练的教学模式
- **进度跟踪**: 学习进度和成就系统

#### 🎯 战术训练
- **分级题库**: 从初级到高级的战术题目
- **主题训练**: 针对特定战术主题的专项练习
- **错题回顾**: 智能错题本功能

#### 📊 开局库学习
- **经典开局**: 常见开局的详细讲解
- **变化分析**: 开局变化的深度分析
- **实战应用**: 开局理论与实战结合

### 👥 社交与竞技系统

#### 🏅 积分排行榜
- **实时排名**: 基于ELO积分系统
- **历史记录**: 积分变化趋势图
- **段位系统**: 类似围棋的段位划分

#### 💬 社交功能
- **好友系统**: 添加好友、好友列表管理
- **消息中心**: 系统通知和好友消息
- **对局邀请**: 向好友发送对局邀请

#### 📈 数据分析
- **对局统计**: 胜负比、常用开局等统计
- **棋力分析**: 基于对局表现的棋力评估
- **进步轨迹**: 棋力提升的可视化展示

### 🔄 对局回放与分析

#### 📹 完整回放
- **步骤回放**: 逐步回放对局过程
- **关键时刻**: 标记对局中的关键节点
- **多速度播放**: 支持不同播放速度

#### 🔍 深度分析
- **引擎分析**: Stockfish引擎的局面评估
- **最佳走法**: AI建议的最优走法
- **失误标记**: 自动标记对局中的失误

## 🚀 快速开始

### 📋 环境要求

#### 基础环境
- **Node.js**: 16.0+ (推荐 18.x)
- **Java**: JDK 8+ (支持 JDK 17)
- **Maven**: 3.6+
- **MySQL**: 5.7+ (推荐 8.0)
- **Redis**: 6.0+ (可选，用于缓存)

#### 开发工具
- **IDE**: IntelliJ IDEA (需安装Lombok插件)
- **数据库工具**: Navicat、DBeaver等
- **API测试**: Postman、Apifox等

### 🗄️ 数据库初始化

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE novachess DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 导入核心表结构
mysql -u root -p novachess < DDL_chess.sql

# 3. 导入JeecgBoot系统表
mysql -u root -p novachess < jeecg-boot/db/jeecgboot-mysql-5.7.sql

# 4. 执行数据修复脚本（可选）
mysql -u root -p novachess < sql/run_all_fixes.sql
```

### ⚙️ 后端启动 (`jeecg-boot`)

#### 1. 配置文件修改

编辑 `jeecg-boot/jeecg-module-system/jeecg-system-start/src/main/resources/application-dev.yml`：

```yaml
# 数据库配置
spring:
  datasource:
    master:
      url: jdbc:mysql://localhost:3306/novachess?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
      username: root
      password: your_password
      driver-class-name: com.mysql.cj.jdbc.Driver
  
  # Redis配置（可选）
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

#### 2. 启动步骤

```bash
# 进入后端目录
cd jeecg-boot

# 安装依赖
mvn clean install -DskipTests

# 启动应用
cd jeecg-module-system/jeecg-system-start
mvn spring-boot:run

# 或者使用IDE直接运行 JeecgSystemApplication.java
```

#### 3. 验证启动

- 后端服务: http://localhost:8080
- API文档: http://localhost:8080/doc.html
- WebSocket测试: 打开 `testSocket.html`

### 🎨 前端启动 (`NovaChess-App`)

#### 1. 安装依赖

```bash
# 进入前端目录
cd NovaChess-App

# 安装依赖
npm install
# 或使用 yarn
yarn install
```

#### 2. 配置API地址

编辑 `config.js`：

```javascript
const config = {
  // 开发环境
  development: {
    baseUrl: 'http://localhost:8080',
    websocketUrl: 'http://localhost:8080/ws'
  },
  // 生产环境
  production: {
    baseUrl: 'http://47.111.122.119:8080',
    websocketUrl: 'http://47.111.122.119:8080/ws'
  }
}
```

#### 3. 启动开发服务器

```bash
# Web端开发
npm run dev:h5

# 微信小程序开发
npm run dev:mp-weixin

# App开发
npm run dev:app
```

#### 4. 构建生产版本

```bash
# 构建Web版本
npm run build:h5

# 构建微信小程序
npm run build:mp-weixin

# 构建App
npm run build:app
```

### 🐳 Docker部署 (可选)

```bash
# 使用docker-compose一键启动
cd jeecg-boot
docker-compose up -d
```

### 🔧 开发工具配置

#### IDEA配置
1. 安装Lombok插件
2. 启用注解处理器
3. 设置代码格式化规则

#### WebSocket测试
使用 `testSocket.html` 测试WebSocket连接：
```html
<!-- 修改连接地址 -->
const socket = new SockJS('http://localhost:8080/ws');
```

## 🌐 API 和通信

### 📡 通信架构

NovaChess 采用混合通信模式，结合 RESTful API 和 WebSocket 实现高效的数据交互：

```
前端应用 ←→ API网关 ←→ 后端服务
    ↓           ↓         ↓
WebSocket ←→ STOMP ←→ 消息处理器
```

### 🔌 WebSocket 实时通信

#### 连接配置
```javascript
// 连接地址
const WEBSOCKET_URL = process.env.NODE_ENV === 'production' 
  ? 'http://47.111.122.119:8080/ws' 
  : 'http://localhost:8080/ws';

// 使用 SockJS + STOMP 协议
import { connectWebSocket, subscribeToTopic, sendMessage } from '@/utils/websocket.js';
```

#### 核心功能端点

| 功能 | 发送地址 | 订阅地址 | 说明 |
|------|----------|----------|------|
| **棋盘状态** | `/app/chessboard` | `/topic/chessboard` | 获取当前棋盘状态 |
| **走棋操作** | `/app/movepieces` | `/topic/game/{gameId}` | 处理走棋并广播 |
| **游戏通知** | - | `/topic/game/{gameId}` | 游戏状态变化通知 |
| **用户消息** | - | `/topic/user/{userId}` | 个人消息通知 |

#### 消息格式

**走棋请求**:
```json
{
  "gameId": "game123",
  "userId": "user456",
  "from": "e2",
  "to": "e4",
  "piece": "pawn",
  "moveType": "normal"
}
```

**走棋响应**:
```json
{
  "type": "MOVE_UPDATE",
  "data": {
    "gameId": "game123",
    "currentPlayer": "white",
    "boardState": "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
    "lastMove": {
      "from": "e2",
      "to": "e4",
      "piece": "pawn"
    },
    "gameStatus": "ongoing"
  }
}
```

### 🔗 RESTful API

#### 认证相关
```
POST /sys/login          # 用户登录
POST /sys/logout         # 用户登出
GET  /sys/user/info      # 获取用户信息
```

#### 游戏相关
```
GET    /chess/game/list           # 获取游戏列表
POST   /chess/game/create         # 创建新游戏
GET    /chess/game/{id}           # 获取游戏详情
POST   /chess/game/{id}/join      # 加入游戏
POST   /chess/game/{id}/move      # 走棋（备用API）
GET    /chess/game/{id}/history   # 获取走棋历史
```

#### 用户相关
```
GET    /chess/player/profile      # 获取玩家资料
PUT    /chess/player/profile      # 更新玩家资料
GET    /chess/player/score        # 获取积分信息
GET    /chess/player/games        # 获取历史对局
```

#### 课程相关
```
GET    /chess/course/list         # 获取课程列表
GET    /chess/course/{id}         # 获取课程详情
POST   /chess/course/{id}/progress # 更新学习进度
```

### 📚 API 文档

- **Swagger UI**: http://localhost:8080/doc.html
- **API 文档**: 详细的接口文档和测试界面
- **WebSocket 说明**: 参考 `NovaChess-App/WebSocket调用说明.md`
- **业务流程**: 参考 `NovaChess-App/websocket业务流程.md`

### 🔒 安全机制

#### JWT 认证
```javascript
// 请求头设置
headers: {
  'Authorization': 'Bearer ' + token,
  'Content-Type': 'application/json'
}
```

#### CORS 配置
```yaml
# 跨域配置
jeecg:
  cors:
    allowed-origins: "*"
    allowed-methods: "*"
    allowed-headers: "*"
```

### 🧪 测试工具

#### WebSocket 测试
使用 `testSocket.html` 进行连接测试：
```html
<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Test</title>
</head>
<body>
    <script>
        const socket = new SockJS('http://localhost:8080/ws');
        const stompClient = Stomp.over(socket);
        
        stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            // 订阅测试
            stompClient.subscribe('/topic/test', function(message) {
                console.log('Received: ' + message.body);
            });
        });
    </script>
</body>
</html>
```

## 📄 许可证

| 组件 | 许可证 | 说明 |
|------|--------|------|
| **NovaChess-App** | [MIT License](LICENSE) | 前端应用，开源友好 |
| **jeecg-boot** | [Apache License 2.0](jeecg-boot/LICENSE) | 后端框架，企业级开源 |
| **整体项目** | MIT License | 项目整体遵循MIT协议 |

## ⚠️ 注意事项

### 🔧 开发环境
- **Lombok插件**: IDEA必须安装Lombok插件并启用注解处理
- **JDK版本**: 推荐使用JDK 8或11，支持JDK 17
- **Node.js版本**: 建议使用Node.js 16+，避免兼容性问题

### 🌐 部署配置
- **跨域配置**: 前后端分离部署时需要正确配置CORS
- **WebSocket代理**: 生产环境需要配置WebSocket代理
- **数据库编码**: 确保MySQL使用utf8mb4编码
- **时区设置**: 数据库和应用服务器时区保持一致

### 🔐 安全建议
- **API密钥**: DeepSeek/Kimi API密钥不要提交到代码仓库
- **数据库密码**: 生产环境使用强密码并定期更换
- **JWT密钥**: 使用足够复杂的JWT签名密钥

## 🤝 贡献指南

### 🐛 问题反馈
1. 在GitHub Issues中描述问题
2. 提供详细的错误信息和复现步骤
3. 标明使用的环境和版本信息

### 💡 功能建议
1. 在Issues中提出功能需求
2. 详细描述功能的使用场景
3. 如果可能，提供设计思路或原型

### 🔧 代码贡献
1. Fork项目到个人仓库
2. 创建功能分支进行开发
3. 遵循项目的代码规范
4. 提交Pull Request并描述修改内容

### 📝 代码规范
- **前端**: 遵循Vue.js官方风格指南
- **后端**: 遵循阿里巴巴Java开发手册
- **提交信息**: 使用语义化的commit message

## ❓ 常见问题

### Q: WebSocket连接失败怎么办？
A: 
1. 检查后端服务是否正常启动
2. 确认WebSocket端口没有被防火墙阻挡
3. 查看浏览器控制台的错误信息
4. 使用`testSocket.html`进行连接测试

### Q: 前端无法连接后端API？
A:
1. 检查`config.js`中的API地址配置
2. 确认后端服务正常运行在指定端口
3. 检查CORS配置是否正确
4. 查看网络请求的状态码和错误信息

### Q: 数据库连接失败？
A:
1. 确认MySQL服务正常运行
2. 检查数据库连接配置（地址、端口、用户名、密码）
3. 确认数据库已创建且字符集为utf8mb4
4. 检查数据库用户权限

### Q: AI功能无法使用？
A:
1. 检查DeepSeek/Kimi API密钥是否正确配置
2. 确认API密钥有足够的调用额度
3. 检查网络连接是否正常
4. 查看控制台的API调用错误信息

## 🔗 相关链接

- **项目主页**: [GitHub Repository](https://github.com/LucusorShan/NovaChess)
- **在线演示**: [Demo Site](http://boot3.jeecg.com)
- **技术文档**: [DeepWiki](https://deepwiki.com/Schuxiong/NovaChess_Final)
- **JeecgBoot官网**: [http://www.jeecg.com](http://www.jeecg.com)
- **uni-app官网**: [https://uniapp.dcloud.io](https://uniapp.dcloud.io)

## 👨‍💻 开发团队

感谢所有为NovaChess项目做出贡献的开发者！

### 核心贡献者
- **项目负责人**: [LucusorShan](https://github.com/LucusorShan)
- **前端开发**: NovaChess前端团队
- **后端开发**: NovaChess后端团队
- **AI集成**: 智能机器人开发团队

### 特别感谢
- **JeecgBoot团队**: 提供优秀的低代码开发平台
- **uni-app团队**: 提供强大的跨平台开发框架
- **开源社区**: 提供各种优秀的开源组件和工具

---

<div align="center">
  <p>🎯 <strong>NovaChess - 让国际象棋学习更简单，对战更精彩！</strong></p>
  <p>如果这个项目对您有帮助，请给我们一个 ⭐ Star！</p>
</div>

---

*本文档持续更新中，更详细的信息请参考各子项目内的README文件和相关文档。*
