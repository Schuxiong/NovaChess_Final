DROP TABLE IF EXISTS `chess_player`;
CREATE TABLE `chess_player`  (
                                 `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                 `chess_game_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '游戏id',
                                 `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户id',
                                 `user_account` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户账号',
                                 `hold_chess` int(1) DEFAULT 1  COMMENT '执子方：1:黑 深色，2: 白 浅色',
                                 `play_level` int(3) DEFAULT 1  COMMENT '参与者水平：1:一级，2: 二级',
                                 `play_type` int(3) DEFAULT 1  COMMENT '参与者类型：1:人，2: 机器',
                                 `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
                                 `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                 `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
                                 `sys_org_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门',
                                 `del_flag` int(11) NULL DEFAULT NULL COMMENT '删除状态',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '游戏参与者' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `chess_game`;
CREATE TABLE `chess_game`  (
                               `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                               `black_play_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '黑色id',
                               `black_play_account` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '黑色账号',
                               `white_play_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '白色id',
                               `white_play_account` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '白色用户账号',
    'game_state' int(1) NULL DEFAULT 1 COMMENT '游戏进行状态，1:进行中 2：结束',
    'game_type'  int(1) DEFAULT 1 COMMENT '游戏类型，1:人人对赛 2：人机对赛3:天梯赛',
    `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
    `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
    `sys_org_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门',
    `del_flag` int(11) NULL DEFAULT NULL COMMENT '删除状态',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '游戏' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `chess_pieces`;
CREATE TABLE `chess_pieces`  (
                                 `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                 `chess_game_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '游戏id',
                                 `chess_pieces_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '棋子名',
                                 `pieces_type` int(1) DEFAULT 1  COMMENT '棋子类别 1:黑 深色，2: 白 浅色',
                                 `position_x` varchar(3) DEFAULT 1  COMMENT 'X轴',
                                 `position_y` varchar(3) DEFAULT 1  COMMENT 'Y轴',
                                 `pieces_state` int(3) DEFAULT 1  COMMENT '棋子状态 1:正常，2:被吃了，不显示',
                                 `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
                                 `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                 `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
                                 `sys_org_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门',
                                 `del_flag` int(11) NULL DEFAULT NULL COMMENT '删除状态',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '当前棋子位置' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `chess_move`;
CREATE TABLE `chess_move`  (
                               `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                               `chess_game_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '游戏id',
                               `chess_pieces_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '棋子id',
                               `pieces_type` int(1) DEFAULT 1  COMMENT '棋子类别 1:黑 深色，2: 白 浅色',
                               `from_position_x` varchar(3) DEFAULT 1  COMMENT '开始X轴位置',
                               `from_position_y` varchar(3) DEFAULT 1  COMMENT '开始Y轴位置',
                               `to_position_x` varchar(3) DEFAULT 1  COMMENT '目标X轴位置',
                               `to_position_y` varchar(3) DEFAULT 1  COMMENT '目标Y轴位置',
                               `took_pieces_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '被吃的棋子id',
                               `move_state` int(3) DEFAULT 1  COMMENT '行棋状态 1:正常，2:悔棋',
                               `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
                               `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
                               `sys_org_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门',
                               `del_flag` int(11) NULL DEFAULT NULL COMMENT '删除状态',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '下棋' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `chess_player_score`;
CREATE TABLE `chess_player_score`  (
                                       `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                       `user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户id',
                                       `user_account` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户账号',
                                       `score` int(11) DEFAULT 600  COMMENT '得分，初始值600',
                                       `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
                                       `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                       `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
                                       `sys_org_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门',
                                       `del_flag` int(11) NULL DEFAULT NULL COMMENT '删除状态',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '游戏选手积分' ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `chess_player_score_record`;
CREATE TABLE `chess_player_score_record`  (
                                              `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                              `chess_player_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '游戏选手id',
                                              `chess_game_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '游戏id',
                                              `score` int(11) DEFAULT 1  COMMENT '得分，>0为获取，<0为失去',
                                              `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期',
                                              `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                              `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期',
                                              `sys_org_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门',
                                              `del_flag` int(11) NULL DEFAULT NULL COMMENT '删除状态',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '游戏选手得分记录' ROW_FORMAT = DYNAMIC;