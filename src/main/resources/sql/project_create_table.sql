create table t_gr_project (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `oid` char(32) NOT NULL DEFAULT '' COMMENT '业务id',
    `project_name` varchar(128) NOT NULL DEFAULT '' COMMENT '项目名称',
    `project_desc` varchar(300) NOT NULL DEFAULT '' COMMENT '项目描述',
    `owner_id` varchar(20) NOT NULL DEFAULT '' COMMENT '项目主负责人id',
    `owner_name` varchar(50) NOT NULL DEFAULT '' COMMENT '项目主负责人姓名',
    `back_up_owner_id` varchar(20) NOT NULL DEFAULT '' COMMENT '项目备负责人id',
    `back_up_owner_name` varchar(50) NOT NULL DEFAULT '' COMMENT '项目备负责人姓名',
    `c_user` varchar(20) NOT NULL DEFAULT '' COMMENT '创建人',
    `c_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `u_user` varchar(20) NOT NULL DEFAULT '' COMMENT '更新人',
    `u_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态 0 未删除  1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_oid` (`oid`)
) ENGINE=InnoDB COMMENT='项目表';

create table t_gr_iteration (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `oid` char(32) NOT NULL DEFAULT '' COMMENT '业务id',
    `project_id` char(32) NOT NULL DEFAULT '' COMMENT '项目oid',
    `project_name` varchar(128) NOT NULL DEFAULT '' COMMENT '项目名称',
    `iteration_name` varchar(128) NOT NULL DEFAULT '' COMMENT '迭代名称',
    `iteration_desc` varchar(300) NOT NULL DEFAULT '' COMMENT '迭代描述',
    `except_complete_time` varchar(64) NOT NULL DEFAULT '' COMMENT '预计完成时间',
    `actual_complete_time` varchar(64) NOT NULL DEFAULT '' COMMENT '实际完成时间',
    `iteration_status` varchar(64) NOT NULL DEFAULT '' COMMENT '迭代状态(未执行unexecuted  迭代中iteration  已完成completed  已提前关闭stopped)',
    `c_user` varchar(20) NOT NULL DEFAULT '' COMMENT '创建人',
    `c_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `u_user` varchar(20) NOT NULL DEFAULT '' COMMENT '更新人',
    `u_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态 0 未删除  1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_oid` (`oid`)
) ENGINE=InnoDB COMMENT='迭代表';