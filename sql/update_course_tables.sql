-- 更新课程表结构
ALTER TABLE chess_course 
ADD COLUMN category VARCHAR(20) DEFAULT 'basic' COMMENT '课程分类：basic/intermediate/advanced',
ADD COLUMN icon VARCHAR(500) COMMENT '课程图标URL',
ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
ADD COLUMN create_by VARCHAR(32),
ADD COLUMN update_by VARCHAR(32);

-- 创建课程步骤表
CREATE TABLE chess_course_step (
  id VARCHAR(32) PRIMARY KEY,
  course_id VARCHAR(32) NOT NULL COMMENT '课程ID',
  type VARCHAR(20) NOT NULL COMMENT '步骤类型：info/task',
  message TEXT COMMENT '步骤说明文字',
  board_setup JSON COMMENT '棋盘设置：{"clear": true, "pieces": [...]}',
  expected_move JSON COMMENT '期望移动：{"from": {"row": 0, "col": 0}, "to": {"row": 1, "col": 1}}',
  correct_message VARCHAR(200) COMMENT '正确时的提示',
  error_message VARCHAR(200) COMMENT '错误时的提示',
  hint_message VARCHAR(200) COMMENT '提示信息',
  step_order INT DEFAULT 0 COMMENT '步骤顺序',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (course_id) REFERENCES chess_course(id) ON DELETE CASCADE
);

-- 如果原来的表名不是chess_course，需要重命名
-- RENAME TABLE chess_courses TO chess_course;