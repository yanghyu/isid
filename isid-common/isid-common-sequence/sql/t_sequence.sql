CREATE TABLE `t_sequence` (
  `c_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'default' COMMENT '主键编号[键名]',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_current_number` bigint NOT NULL DEFAULT '1' COMMENT '当前编号',
  `c_default_step_size` int NOT NULL DEFAULT '1000' COMMENT '默认步长',
  `c_version` bigint NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`c_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='序列'