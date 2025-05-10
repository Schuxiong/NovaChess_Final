-- 修复chess_pieces表结构
-- 确保所有的字段名都是下划线风格

-- 1. 修正字段名 - 如果原字段名是驼峰式，则重命名为下划线式
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS chessGameId chess_game_id varchar(50);
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS chessPiecesName chess_pieces_name varchar(50);
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS piecesType pieces_type int;
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS positionX position_x varchar(10);
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS positionY position_y varchar(10);
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS piecesState pieces_state int;
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS createBy create_by varchar(50);
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS createTime create_time datetime;
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS updateBy update_by varchar(50);
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS updateTime update_time datetime;
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS sysOrgCode sys_org_code varchar(64);
ALTER TABLE chess_pieces CHANGE COLUMN IF EXISTS delFlag del_flag int;

-- 2. 添加索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_pieces_game_id ON chess_pieces (chess_game_id);
CREATE INDEX IF NOT EXISTS idx_pieces_position ON chess_pieces (position_x, position_y);
CREATE INDEX IF NOT EXISTS idx_pieces_del_flag ON chess_pieces (del_flag);

-- 3. 清理没有对应游戏ID的棋子记录
UPDATE chess_pieces p
SET p.del_flag = 1
WHERE p.del_flag = 0
AND NOT EXISTS (
    SELECT 1 FROM chess_game g
    WHERE g.id = p.chess_game_id
    AND g.del_flag = 0
);

-- 4. 物理删除标记为删除的记录
DELETE FROM chess_pieces WHERE del_flag = 1; 