-- 为chess_move表添加move_sequence和move_duration_seconds字段
ALTER TABLE `chess_move` 
ADD COLUMN `move_sequence` int DEFAULT NULL COMMENT '走棋顺序',
ADD COLUMN `move_duration_seconds` int DEFAULT NULL COMMENT '走棋耗时（秒）';