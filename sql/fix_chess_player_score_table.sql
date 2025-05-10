-- 修复chess_player_score表结构
-- 确保所有的字段名都是下划线风格

-- 1. 修正字段名 - 如果原字段名是驼峰式，则重命名为下划线式
ALTER TABLE chess_player_score CHANGE COLUMN IF EXISTS userId user_id varchar(50);
ALTER TABLE chess_player_score CHANGE COLUMN IF EXISTS userAccount user_account varchar(50);
ALTER TABLE chess_player_score CHANGE COLUMN IF EXISTS createBy create_by varchar(50);
ALTER TABLE chess_player_score CHANGE COLUMN IF EXISTS createTime create_time datetime;
ALTER TABLE chess_player_score CHANGE COLUMN IF EXISTS updateBy update_by varchar(50);
ALTER TABLE chess_player_score CHANGE COLUMN IF EXISTS updateTime update_time datetime;
ALTER TABLE chess_player_score CHANGE COLUMN IF EXISTS sysOrgCode sys_org_code varchar(64);
ALTER TABLE chess_player_score CHANGE COLUMN IF EXISTS delFlag del_flag int;

-- 2. 添加索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_score_user_id ON chess_player_score (user_id);
CREATE INDEX IF NOT EXISTS idx_score_del_flag ON chess_player_score (del_flag);

-- 3. 检查和处理重复记录
-- 创建临时表存储需要保留的记录
CREATE TEMPORARY TABLE IF NOT EXISTS tmp_valid_scores AS
SELECT MIN(id) as id, user_id
FROM chess_player_score
WHERE del_flag = 0
GROUP BY user_id
HAVING COUNT(*) > 1;

-- 将未在临时表中的重复记录标记为删除
UPDATE chess_player_score p
SET p.del_flag = 1
WHERE p.del_flag = 0
AND EXISTS (
    SELECT 1 FROM tmp_valid_scores t
    WHERE t.user_id = p.user_id
    AND t.id <> p.id
);

-- 4. 物理删除标记为删除的记录
DELETE FROM chess_player_score WHERE del_flag = 1; 