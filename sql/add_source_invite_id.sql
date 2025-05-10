-- 在chess_game表中添加source_invite_id字段，用于关联邀请记录
ALTER TABLE chess_game ADD COLUMN source_invite_id varchar(50) COMMENT '邀请ID，用于关联邀请记录';
 
-- 添加索引以提高查询性能
ALTER TABLE chess_game ADD INDEX idx_source_invite_id (source_invite_id); 