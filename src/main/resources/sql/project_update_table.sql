ALTER TABLE `gr_change`
ADD COLUMN `iteration_id` varchar(32) NOT NULL DEFAULT '' COMMENT '关联迭代id' AFTER `version`;