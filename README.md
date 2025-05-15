# NovaChess 国际象棋项目

## 1. 项目简介

NovaChess 是一个功能完善的国际象棋应用程序，包含了用户友好的前端界面和强大的后端服务。项目旨在提供一个多平台、功能丰富的国际象棋体验，包括在线对战、人机对弈、棋局分析、课程学习等。

## 2. 项目组成

本项目主要由以下几个部分组成：

*   **`NovaChess-App/`**: 前端应用程序，基于 uni-app 开发，支持多端发布（Web、小程序、App）。
*   **`jeecg-boot/`**: 后端应用程序，基于 JeecgBoot 低代码平台开发，使用 Java 和 SpringBoot。
*   **`sql/`**: 包含数据库表结构定义（DDL）和数据迁移/修复脚本。
*   **`DDL_chess.sql`**: 核心的数据库表结构定义文件。
*   **`testSocket.html`**: 用于测试 WebSocket 连接的HTML文件。

## 3. 技术栈

### 3.1. 前端 (`NovaChess-App`)

*   **核心框架**: [Vue.js](https://vuejs.org/)
*   **跨平台方案**: [uni-app](https://uniapp.dcloud.io/)
*   **状态管理**: Vuex
*   **UI**: 自定义组件
*   **开发语言**: JavaScript
*   **包管理器**: npm

### 3.2. 后端 (`jeecg-boot`)

*   **核心框架**: Spring Boot 2.7.18
*   **低代码平台**: JeecgBoot
*   **开发语言**: Java 8+ (支持17)
*   **依赖管理**: Maven
*   **持久层框架**: MybatisPlus 3.5.3.2
*   **安全框架**: Apache Shiro 1.12.0, JWT 3.11.0
*   **微服务框架 (可选)**: Spring Cloud Alibaba (Nacos, Gateway, Sentinel 等)
*   **数据库**: MySQL 5.7+
*   **缓存**: Redis

### 3.3. 数据库

*   **类型**: MySQL
*   **表结构定义**: 主要在 `DDL_chess.sql` 文件中定义，`sql/` 目录包含其他辅助脚本。
*   **主要数据表**:
    *   `chess_courses`, `chess_course_steps`, `chess_board_setups`: 课程与教学相关。
    *   `chess_game`, `chess_move`, `chess_pieces`, `chess_player`: 对局与棋谱相关。
    *   `chess_friend_pair`: 好友约战相关。
    *   `chess_player_score`, `chess_player_score_record`: 用户积分与排名相关。
    *   `sys_user`, `sys_depart`, `sys_message`: 系统用户及消息管理 (可能由JeecgBoot提供)。

### 3.4. 版本控制

*   Git

## 4. 主要功能

结合前后端信息，NovaChess 提供以下主要功能：

*   **用户系统**: 注册、登录、个人信息管理。
*   **在线对战**:
    *   与全球玩家实时匹配对战。
    *   邀请好友进行对局。
*   **人机对战**: 与不同难度的 AI 进行对弈。
*   **学习系统**:
    *   国际象棋规则入门教程。
    *   战术训练和题目。
    *   开局库学习。
*   **棋局功能**:
    *   完整的国际象棋规则支持 (包括王车易位、吃过路兵、兵升变)。
    *   将军和将杀检测。
    *   对局计时器。
    *   对局回放与分析。
*   **社交功能**:
    *   信箱系统，用于接收系统通知和好友消息。
*   **排行榜**: 展示用户积分和排名。

## 5. 如何运行

### 5.1. 前端 (`NovaChess-App`)

请参考 `NovaChess-App/README.md` 文件获取详细的启动和构建指南。通常步骤包括：

1.  安装 Node.js 和 npm/yarn。
2.  进入 `NovaChess-App` 目录。
3.  运行 `npm install` 安装依赖。
4.  根据目标平台运行相应的编译或运行命令 (例如 `npm run dev:h5` 启动 H5 开发服务器)。

### 5.2. 后端 (`jeecg-boot`)

请参考 `jeecg-boot/README.md` 文件获取详细的启动和部署指南。通常步骤包括：

1.  安装 Java JDK (8 或更高版本) 和 Maven。
2.  配置好数据库 (MySQL) 并导入 `DDL_chess.sql` 和 `jeecg-boot/db/` 下的初始化SQL。
3.  修改后端项目中的数据库连接配置。
4.  进入 `jeecg-boot` 目录。
5.  使用 Maven 构建项目 (例如 `mvn clean install`)。
6.  运行生成的 SpringBoot 应用 (通常是 `jeecg-system-start` 模块)。

### 5.3. 数据库

1.  确保已安装并运行 MySQL 数据库服务。
2.  创建一个新的数据库 (例如 `novachess`)。
3.  使用数据库客户端导入 `DDL_chess.sql` 文件以创建表结构。
4.  根据需要，执行 `sql/` 目录下的其他脚本。
5.  后端 JeecgBoot 项目可能还需要其自带的初始化SQL脚本 (通常位于 `jeecg-boot/db/` 目录下)。

## 6. API 和通信

*   前后端之间的主要通信方式可能包括 RESTful API 和 WebSocket。
*   `testSocket.html` 文件可用于初步测试 WebSocket 连接。
*   前端 `NovaChess-App/WebSocket调用说明.md` 和 `NovaChess-App/websocket业务流程.md` 提供了 WebSocket 相关的详细说明。

## 7. 许可证

*   前端 `NovaChess-App` 项目采用 MIT 许可证。
*   后端 `jeecg-boot` 项目基于 Apache License 2.0。

## 8. 注意事项

*   后端 JeecgBoot 项目需要安装 Lombok 插件到 IDEA。
*   前后端分离部署时，注意配置跨域请求 (CORS)。

---

本文档是对 NovaChess 项目的整体概览。更详细的信息请参考各子项目内的 README 文件和相关文档。
