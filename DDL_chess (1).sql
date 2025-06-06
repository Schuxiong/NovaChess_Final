

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_course_steps   */
/******************************************/
CREATE TABLE `chess_course_steps` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '步骤ID，自增主键',
  `course_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属课程ID',
  `step_order` int NOT NULL COMMENT '步骤顺序',
  `step_type` enum('intro','task') COLLATE utf8mb4_general_ci NOT NULL COMMENT '步骤类型：介绍或任务',
  `message` text COLLATE utf8mb4_general_ci COMMENT '步骤展示的消息内容',
  `board_clear` tinyint(1) DEFAULT '0' COMMENT '是否清空棋盘',
  `board_setup` json DEFAULT NULL COMMENT '棋盘设置：{"clear": true, "pieces": [...]}',
  `expected_from_row` int DEFAULT NULL COMMENT '期望起始行（用于验证移动）',
  `expected_from_col` int DEFAULT NULL COMMENT '期望起始列',
  `expected_to_row` int DEFAULT NULL COMMENT '期望目标行',
  `expected_to_col` int DEFAULT NULL COMMENT '期望目标列',
  `correct_message` text COLLATE utf8mb4_general_ci COMMENT '正确操作提示信息',
  `error_message` text COLLATE utf8mb4_general_ci COMMENT '错误操作提示信息',
  `hint_message` text COLLATE utf8mb4_general_ci COMMENT '提示信息',
  PRIMARY KEY (`id`),
  KEY `idx_course_steps_order` (`course_id`,`step_order`),
  CONSTRAINT `chess_course_steps_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `chess_courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程步骤表'
;

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_courses   */
/******************************************/
CREATE TABLE `chess_courses` (
  `id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程唯一标识',
  `title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程标题',
  `description` text COLLATE utf8mb4_general_ci COMMENT '课程描述',
  `icon_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '课程图标URL',
  `category` enum('basic','advanced','strategy','other') COLLATE utf8mb4_general_ci DEFAULT 'basic' COMMENT '课程分类：基础、高级、策略、其他',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程主表'
;

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_draw_request   */
/******************************************/
CREATE TABLE `chess_draw_request` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `game_id` varchar(36) NOT NULL COMMENT '游戏ID',
  `request_user_id` varchar(36) NOT NULL COMMENT '发起请求的用户ID',
  `request_user_account` varchar(100) DEFAULT NULL COMMENT '发起请求的用户账号',
  `target_user_id` varchar(36) NOT NULL COMMENT '目标用户ID（接收请求的用户）',
  `target_user_account` varchar(100) DEFAULT NULL COMMENT '目标用户账号',
  `status` int NOT NULL DEFAULT '1' COMMENT '请求状态：1-待响应，2-已接受，3-已拒绝，4-已过期',
  `response_time` datetime DEFAULT NULL COMMENT '响应时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `idx_game_id` (`game_id`),
  KEY `idx_request_user_id` (`request_user_id`),
  KEY `idx_target_user_id` (`target_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='和棋请求表'
;

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_friend_pair   */
/******************************************/
CREATE TABLE `chess_friend_pair` (
  `id` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `source_user_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '发起用户id',
  `source_user_account` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '发起用户账号',
  `source_one_part` int NOT NULL COMMENT '1：黑色；2: 白色',
  `accept_user_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '接受用户id',
  `accept_user_account` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '接受用户账号',
  `accept_one_part` int NOT NULL COMMENT '1：黑色；2: 白色',
  `create_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '所属部门',
  `del_flag` int DEFAULT NULL COMMENT '删除状态',
  `invite_status` tinyint(1) DEFAULT '0' COMMENT '邀请状态：0-待接受，1-已接受，2-已拒绝',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC
;

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_game   */
/******************************************/
CREATE TABLE `chess_game` (
  `id` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `black_play_id` varchar(50) DEFAULT NULL COMMENT '黑色id',
  `black_play_account` varchar(50) DEFAULT NULL COMMENT '黑色账号',
  `white_play_id` varchar(50) DEFAULT NULL COMMENT '白色id',
  `white_play_account` varchar(50) DEFAULT NULL COMMENT '白色账号',
  `game_state` int DEFAULT NULL COMMENT '游戏状态。1:进行中2：结束',
  `game_type` int DEFAULT '1' COMMENT '游戏类型，1:人人对赛 2：人机对赛3:天梯赛',
  `create_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '所属部门',
  `del_flag` int DEFAULT NULL COMMENT '删除状态',
  `current_turn` int DEFAULT NULL COMMENT '当前回合，1:黑方回合，2:白方回合',
  `source_invite_id` varchar(50) DEFAULT NULL COMMENT '邀请ID，用于关联邀请记录',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_current_turn` (`current_turn`),
  KEY `idx_source_invite_id` (`source_invite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='游戏'
;

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_move   */
/******************************************/
CREATE TABLE `chess_move` (
  `id` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `chess_game_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '游戏id',
  `chess_pieces_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '棋子id',
  `pieces_type` int DEFAULT '1' COMMENT '棋子类别 1:黑 深色，2: 白 浅色',
  `from_position_x` varchar(3) DEFAULT '1' COMMENT '开始X轴位置',
  `from_position_y` varchar(3) DEFAULT '1' COMMENT '开始Y轴位置',
  `to_position_x` varchar(3) DEFAULT '1' COMMENT '目标X轴位置',
  `to_position_y` varchar(3) DEFAULT '1' COMMENT '目标Y轴位置',
  `took_pieces_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '被吃的棋子id',
  `move_state` int DEFAULT '1' COMMENT '行棋状态 1:正常，2:悔棋',
  `create_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '所属部门',
  `del_flag` int DEFAULT NULL COMMENT '删除状态',
  `user_id` varchar(50) DEFAULT NULL COMMENT '用户ID',
  `move_sequence` int DEFAULT NULL COMMENT '走棋顺序，表示该走棋是游戏中的第几步',
  `move_duration_seconds` int DEFAULT NULL COMMENT '该步走棋所花费的时间（秒）',
  `is_en_passant` tinyint(1) DEFAULT '0' COMMENT '是否为过路兵移动',
  `en_passant_captured_x` varchar(2) DEFAULT NULL COMMENT '过路兵被吃掉的兵的X位置',
  `en_passant_captured_y` varchar(2) DEFAULT NULL COMMENT '过路兵被吃掉的兵的Y位置',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_chess_move_en_passant` (`is_en_passant`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='下棋'
;

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_pieces   */
/******************************************/
CREATE TABLE `chess_pieces` (
  `id` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `chess_game_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '游戏id',
  `chess_pieces_name` varchar(50) DEFAULT NULL COMMENT '棋子名',
  `pieces_type` int DEFAULT '1' COMMENT '棋子类别 1:黑 深色，2: 白 浅色',
  `position_x` varchar(3) DEFAULT '1' COMMENT 'X轴',
  `position_y` varchar(3) DEFAULT '1' COMMENT 'Y轴',
  `pieces_state` int DEFAULT '1' COMMENT '棋子状态 1:正常，2:被吃了，不显示',
  `create_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '所属部门',
  `del_flag` int DEFAULT NULL COMMENT '删除状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='当前棋子位置'
;

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_player   */
/******************************************/
CREATE TABLE `chess_player` (
  `id` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `chess_game_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '游戏id',
  `user_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '用户id',
  `user_account` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '用户账号',
  `hold_chess` int DEFAULT '1' COMMENT '执子方：1:黑 深色，2: 白 浅色',
  `play_level` int DEFAULT '1' COMMENT '参与者水平：1:一级，2: 二级',
  `play_type` int DEFAULT '1' COMMENT '参与者类型：1:人，2: 机器',
  `create_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '所属部门',
  `del_flag` int DEFAULT NULL COMMENT '删除状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='游戏参与者'
;

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_player_score   */
/******************************************/
CREATE TABLE `chess_player_score` (
  `id` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `user_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '用户id',
  `user_account` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '用户账号',
  `score` int DEFAULT '600' COMMENT '得分，初始值600',
  `create_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '所属部门',
  `del_flag` int DEFAULT NULL COMMENT '删除状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='游戏选手积分'
;

/******************************************/
/*   DatabaseName = chess   */
/*   TableName = chess_player_score_record   */
/******************************************/
CREATE TABLE `chess_player_score_record` (
  `id` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `chess_player_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '游戏选手id',
  `chess_game_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '游戏id',
  `score` int DEFAULT '1' COMMENT '得分，>0为获取，<0为失去',
  `create_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '所属部门',
  `del_flag` int DEFAULT NULL COMMENT '删除状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='游戏选手得分记录'
;


/******************************************/
/*   DatabaseName = chess   */
/*   TableName = sys_depart   */
/******************************************/
CREATE TABLE `sys_depart` (
  `id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'ID',
  `parent_id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '父机构ID',
  `depart_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '机构/部门名称',
  `depart_name_en` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '英文名',
  `depart_name_abbr` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '缩写',
  `depart_order` int DEFAULT '0' COMMENT '排序',
  `description` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '描述',
  `org_category` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '1' COMMENT '机构类别 1公司，2组织机构，3岗位',
  `org_type` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '机构类型 1一级部门 2子部门',
  `org_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '机构编码',
  `mobile` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '手机号',
  `fax` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '传真',
  `address` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '地址',
  `memo` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '备注',
  `status` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '状态（1启用，0不启用）',
  `del_flag` varchar(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '删除状态（0，正常，1已删除）',
  `qywx_identifier` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '对接企业微信的ID',
  `ding_identifier` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '对接钉钉部门的ID',
  `create_by` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `tenant_id` int DEFAULT '0' COMMENT '租户ID',
  `iz_leaf` tinyint(1) DEFAULT '0' COMMENT '是否有叶子节点: 1是0否',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_depart_org_code` (`org_code`) USING BTREE,
  KEY `idx_sd_parent_id` (`parent_id`) USING BTREE,
  KEY `idx_sd_depart_order` (`depart_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='组织机构表'
;


/******************************************/
/*   DatabaseName = chess   */
/*   TableName = sys_message   */
/******************************************/
CREATE TABLE `sys_message` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `es_title` varchar(100) DEFAULT NULL COMMENT '标题',
  `es_content` text COMMENT '内容',
  `es_sender_id` varchar(32) DEFAULT NULL COMMENT '发送人ID',
  `es_receiver_id` varchar(32) DEFAULT NULL COMMENT '接收人ID',
  `es_send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `es_category` varchar(10) DEFAULT NULL COMMENT '消息类型(1:私信)',
  `es_read_status` varchar(10) DEFAULT '0' COMMENT '阅读状态(0:未读,1:已读)',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `es_send_status` varchar(10) DEFAULT '0' COMMENT '发送状态',
  `es_param` varchar(1000) DEFAULT NULL COMMENT '发送所需参数Json格式',
  `es_send_num` int DEFAULT NULL COMMENT '发送次数 超过5次不再发送',
  `es_result` varchar(255) DEFAULT NULL COMMENT '推送失败原因',
  `es_type` varchar(10) DEFAULT NULL COMMENT '消息类型',
  `remark` varchar(255) DEFAULT '0' COMMENT '备注',
  `es_sender_name` varchar(100) DEFAULT NULL COMMENT '发送人姓名',
  `es_receiver_name` varchar(100) DEFAULT NULL COMMENT '接收人姓名',
  `es_receiver_avatar` varchar(255) DEFAULT NULL COMMENT '接收者头像',
  `es_sender_avatar` varchar(255) DEFAULT NULL COMMENT '发送者头像',
  PRIMARY KEY (`id`),
  KEY `idx_receiver` (`es_receiver_id`),
  KEY `idx_sender_receiver` (`es_sender_id`,`es_receiver_id`),
  KEY `idx_category` (`es_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统消息表'
;


/******************************************/
/*   DatabaseName = chess   */
/*   TableName = sys_user   */
/******************************************/
CREATE TABLE `sys_user` (
  `id` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '主键id',
  `username` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '登录账号',
  `realname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '密码',
  `salt` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'md5密码盐',
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '头像',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别(0-默认未知,1-男,2-女)',
  `email` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '电子邮件',
  `phone` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '电话',
  `org_code` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '登录会话的机构编码',
  `status` tinyint(1) DEFAULT NULL COMMENT '性别(1-正常,2-冻结)',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除状态(0-正常,1-已删除)',
  `third_id` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '第三方登录的唯一标识',
  `third_type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '第三方类型',
  `activiti_sync` tinyint(1) DEFAULT NULL COMMENT '同步工作流引擎(1-同步,0-不同步)',
  `work_no` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '工号，唯一键',
  `telephone` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '座机号',
  `create_by` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `user_identity` tinyint(1) DEFAULT NULL COMMENT '身份（1普通成员 2上级）',
  `depart_ids` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '负责部门',
  `client_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '设备ID',
  `login_tenant_id` int DEFAULT NULL COMMENT '上次登录选择租户ID',
  `bpm_status` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '流程入职离职状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_sys_user_work_no` (`work_no`) USING BTREE,
  UNIQUE KEY `uniq_sys_user_username` (`username`) USING BTREE,
  UNIQUE KEY `uniq_sys_user_phone` (`phone`) USING BTREE,
  UNIQUE KEY `uniq_sys_user_email` (`email`) USING BTREE,
  KEY `idx_su_status` (`status`) USING BTREE,
  KEY `idx_su_del_flag` (`del_flag`) USING BTREE,
  KEY `idx_su_del_username` (`username`,`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='用户表'
;





