-- 在chess_game表中添加current_turn字段，表示当前轮到哪方下棋（1表示黑方，2表示白方）
ALTER TABLE chess_game ADD COLUMN current_turn int COMMENT '当前轮到哪方下棋，1:黑方，2:白方';

-- 更新现有记录，设置默认值为1（黑方先行）
UPDATE chess_game SET current_turn = 1 WHERE current_turn IS NULL;

-- 添加索引以提高查询性能
ALTER TABLE chess_game ADD INDEX idx_current_turn (current_turn);

-- 在chess_move表中添加user_id字段，表示执行下棋操作的用户ID
ALTER TABLE chess_move ADD COLUMN user_id varchar(50) COMMENT '用户ID'; 