-- 校验、修正chess_game表结构，确保所有的字段名称都是下划线风格
-- 如果原字段名是驼峰式，则需要修正为下划线式

-- 检查gameState字段，如果存在则重命名为game_state
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS gameState game_state int;

-- 检查delFlag字段，如果存在则重命名为del_flag
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS delFlag del_flag int;

-- 确保所有字段名称是下划线风格，方便后续查询
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS blackPlayId black_play_id varchar(50);
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS blackPlayAccount black_play_account varchar(50);
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS whitePlayId white_play_id varchar(50);
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS whitePlayAccount white_play_account varchar(50);
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS gameType game_type int;
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS currentTurn current_turn int;
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS sourceInviteId source_invite_id varchar(50);
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS createBy create_by varchar(50);
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS createTime create_time datetime;
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS updateBy update_by varchar(50);
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS updateTime update_time datetime;
ALTER TABLE chess_game CHANGE COLUMN IF EXISTS sysOrgCode sys_org_code varchar(64); 