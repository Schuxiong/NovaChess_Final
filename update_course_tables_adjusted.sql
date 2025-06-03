-- 根据现有表结构调整的SQL脚本

-- chess_courses表已经存在，只需要添加缺失的字段
ALTER TABLE chess_courses 
ADD COLUMN create_by VARCHAR(32) COMMENT '创建人',
ADD COLUMN update_by VARCHAR(32) COMMENT '更新人';

-- chess_course_steps表已经存在，但需要添加JSON字段来支持复杂的棋盘设置
ALTER TABLE chess_course_steps 
ADD COLUMN board_setup JSON COMMENT '棋盘设置：{"clear": true, "pieces": [...]}' AFTER board_clear;

-- 为了保持兼容性，我们保留原有字段，但添加新的JSON字段来支持更复杂的数据结构
-- 这样既不会破坏现有数据，又能支持新的功能需求

-- 创建复合索引优化查询性能
CREATE INDEX idx_course_steps_order ON chess_course_steps(course_id, step_order);