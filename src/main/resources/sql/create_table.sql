create table t_gr_work_order_type (
    `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `order_type_code` varchar(128) NOT NULL DEFAULT '' COMMENT '工单类型编码',
    `order_type_name` varchar(128) NOT NULL DEFAULT '' COMMENT '工单类型名称',
    `order_type_desc` varchar(128) DEFAULT '' COMMENT '工单类型描述',
    `order_type_belong_user_id` varchar(128) NOT NULL DEFAULT '' COMMENT '工单类型归属用户id',
    `order_type_belong_user` varchar(128) NOT NULL DEFAULT '' COMMENT '工单类型归属用户',
    `c_user` varchar(20) NOT NULL DEFAULT '' COMMENT '创建人',
    `c_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `u_user` varchar(20) NOT NULL DEFAULT '' COMMENT '更新人',
    `u_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态 0 未删除  1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_order_type_code` (`order_type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工单类型表';

-- 工单类型，状态，上报人，处理人，环境流转，钉钉通知
-- 工单类型：nginx配置，oss，日志，账号申请，申请MQ，故障排查，其他
-- 状态：待接单，处理完成，已驳回，主动关闭
-- 环境：dev、test、pre、pro
create table t_gr_work_order (
    `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `order_code` varchar(128) NOT NULL DEFAULT '' COMMENT '工单编码',
    `order_name` varchar(128) NOT NULL DEFAULT '' COMMENT '工单名称',
    `order_type_code` varchar(128) NOT NULL DEFAULT '' COMMENT '工单类型编码',
    `order_type_name` varchar(128) NOT NULL DEFAULT '' COMMENT '工单类型名字',
    `order_status` varchar(64) NOT NULL DEFAULT '' COMMENT '工单状态(待接单，处理完成，已驳回，主动关闭)',
    `order_belong_env` varchar(128) NOT NULL DEFAULT '' COMMENT '工单归属环境(多个环境使用逗号隔开)',
    `order_apply_user_id` varchar(20) NOT NULL DEFAULT '' COMMENT '工单申请人id',
    `order_apply_user` varchar(20) NOT NULL DEFAULT '' COMMENT '工单申请人',
    `order_apply_reason` varchar(256) NOT NULL DEFAULT '' COMMENT '工单申请原因',
    `order_handler_user_id` varchar(20) DEFAULT '' COMMENT '工单处理人id',
    `order_handler_user` varchar(20) DEFAULT '' COMMENT '工单处理人',
    `order_handler_feedback` varchar(256) DEFAULT '' COMMENT '工单处理反馈',
    `c_user` varchar(20) NOT NULL DEFAULT '' COMMENT '创建人',
    `c_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `u_user` varchar(20) NOT NULL DEFAULT '' COMMENT '更新人',
    `u_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态 0 未删除  1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_order_code` (`order_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工单表';