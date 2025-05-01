-- 聊天消息表
CREATE TABLE `chat_message` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `sender_id` varchar(32) NOT NULL COMMENT '发送者用户ID',
  `receiver_id` varchar(32) NOT NULL COMMENT '接收者用户ID',
  `content` text COMMENT '消息内容',
  `message_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '消息类型：1-文本消息，2-图片消息，3-语音消息，4-视频消息，5-文件消息',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `read_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '已读状态：0-未读，1-已读',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_send_time` (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 消息附件表
CREATE TABLE `chat_message_attachment` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `message_id` varchar(32) NOT NULL COMMENT '消息ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `file_path` varchar(500) DEFAULT NULL COMMENT '文件路径',
  `thumbnail_path` varchar(500) DEFAULT NULL COMMENT '缩略图路径(图片和视频消息)',
  `duration` int(11) DEFAULT NULL COMMENT '时长(语音和视频消息，单位：秒)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_message_id` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息附件表';