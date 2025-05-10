-- 主脚本：按顺序执行所有修复脚本

-- 1. 修复游戏表字段名
SOURCE fix_chess_game_table.sql;

-- 2. 修复棋手表字段名和重复记录
SOURCE fix_chess_player_table.sql;

-- 3. 修复棋子表字段名
SOURCE fix_chess_pieces_table.sql;

-- 4. 修复玩家分数表字段名和重复记录
SOURCE fix_chess_player_score_table.sql;

-- 输出修复完成信息
SELECT 'Database structure fix completed successfully!' AS message; 