-- 创建和棋请求表
CREATE TABLE `chess_draw_request` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `game_id` varchar(36) NOT NULL COMMENT '游戏ID',
  `request_user_id` varchar(36) NOT NULL COMMENT '发起请求的用户ID',
  `request_user_account` varchar(100) DEFAULT NULL COMMENT '发起请求的用户账号',
  `target_user_id` varchar(36) NOT NULL COMMENT '目标用户ID（接收请求的用户）',
  `target_user_account` varchar(100) DEFAULT NULL COMMENT '目标用户账号',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '请求状态：1-待响应，2-已接受，3-已拒绝，4-已过期',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='和棋请求表';

-- 添加外键约束（可选，根据实际情况决定是否添加）
-- ALTER TABLE `chess_draw_request` ADD CONSTRAINT `fk_draw_request_game` FOREIGN KEY (`game_id`) REFERENCES `chess_game` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `chess_draw_request` ADD CONSTRAINT `fk_draw_request_user` FOREIGN KEY (`request_user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `chess_draw_request` ADD CONSTRAINT `fk_draw_target_user` FOREIGN KEY (`target_user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE;