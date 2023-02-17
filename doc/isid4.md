# 互联网系统基础架构设计·四
[TOC]

## 四、系统开放设计
### 1.应用
#### 1.1 概念与规则
在本系统中的注册的应用，这个应用可以是第三方团队开发的，也可以自己团队内部开发的。应用的形式不限，可以是移动应用、网站应用、小程序、公众号等多种形式中的一种。这里的应用归属于某个用户或者某个组织机构。对应用户或者组织机构的管理员为该应用的天然管理员，管理员也可以额外设置专属的应用管理员（本文不提供这种专属管理员的表设计，因为这不是核心的数据结构，有需要可以自行添加相应表）。

#### 1.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_app
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |应用

##### 1.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_app_secret        |varchar(100)|                 |NO      |应用密码
c_app_name          |varchar(100)|                 |NO      |应用名称
c_app_desc          |varchar(200)|                 |NO      |应用描述
c_app_number        |varchar(50) |                 |NO      |应用号
c_app_type          |tinyint     |                 |NO      |应用类型[1:移动应用 2:网站应用 3:小程序 4:公众号]
c_status            |tinyint     |                 |NO      |应用状态[-1:未激活 0:正常]
c_freeze_end_time   |datetime    |NULL             |YES     |冻结结束时间
c_owner_id          |varchar(20) |                 |NO      |应用归属人
c_owner_type        |tinyint     |                 |NO      |应用归属人类型[1:用户 2:组织机构]

##### 1.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
u_app_number        |BTREE|YES   |c_app_number   |应用号唯一索引
i_owner_id          |BTREE|NO    |c_owner_id     |应用归属人编号索引

##### 1.2.3 DDL
```sql
CREATE TABLE `t_app` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号（即AppID）',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_app_secret` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用密码',
  `c_app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
  `c_app_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用描述',
  `c_app_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用号',
  `c_app_type` tinyint NOT NULL COMMENT '应用类型[1:移动应用 2:网站应用 3:小程序 4:公众号]',
  `c_status` tinyint NOT NULL COMMENT '应用状态[-1:未激活 0:正常]',
  `c_freeze_end_time` datetime DEFAULT NULL COMMENT '冻结结束时间',
  `c_owner_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用归属人编号',
  `c_owner_type` tinyint NOT NULL COMMENT '应用归属人类型[1:用户 2:组织机构]',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_app_number` (`c_app_number`) USING BTREE COMMENT '应用号唯一索引',
  KEY `i_owner_id` (`c_owner_id`) USING BTREE COMMENT '应用归属人编号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='应用'
```

#### 1.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_id`进行数据切分。

##### 1.3.1 应用号唯度优化
使用字段`c_id`进行数据切分后会导致`u_app_number`的唯一性失效，解决方案可以是如下这样：
- 一、新增`t_app_number`表，仅一个字段应用号，并对此字段建立唯一索引。该表中记录的应用号都是正在使用或者已经使用过的应用号。
- 二、新增的`t_app_number`表分库分表时按应用号字段进行数据切分。
- 三、`t_app`表插入新的应用号时，需要检查`t_app_number`表中是否存在此应用号。若不存在才可进行插入`t_app`操作，并将此应用号也记录入`t_app_number`。

##### 1.3.2 应用归属人唯度优化
使用字段`c_id`进行数据切分后同时也会导致使用`c_owner_id`字段进行查询本表时无法定位出具体的库和表。和前面一样，方案如下：

- 一、新增`t_app_owner`表，该表结构仅含`t_app`表的核心字段。
- 二、新增的`t_app_owner`表分库分表时按`c_owner_id`字段进行数据切分。
- 三、`t_app_owner`表与`t_app`表数据同步维护更新，保持一致性。

###### 1.3.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_owner_id          |varchar(20) |                 |NO      |应用归属人
c_owner_type        |tinyint     |                 |NO      |应用归属人类型[1:用户 2:组织机构]

###### 1.3.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
i_owner_id          |BTREE|NO    |c_owner_id     |应用归属人编号索引

###### 1.3.2.3 DDL
```sql
CREATE TABLE `t_app_owner` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号（即AppID）',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_owner_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用归属人编号',
  `c_owner_type` tinyint NOT NULL COMMENT '应用归属人类型[1:用户 2:组织机构]',
  PRIMARY KEY (`c_id`),
  KEY `i_owner_id` (`c_owner_id`) USING BTREE COMMENT '应用归属人编号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='应用归属人'
```

### 2.组织机构订阅应用
#### 2.1 概念与规则
本表记录组织机构订阅的应用。部分应用必需组织机构管理员订阅该应用后，该组织机构的成员才可以使用这些应用。对于所有应用，在没有获得组织机构的订阅以及组织机构信息项授权前该应用都无法获取到属于该组织机构的相关信息。

#### 2.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_organization_subscribed_app
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |组织机构订阅应用

##### 2.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_organization_id   |varchar(20) |                 |NO      |组织机构编号
c_app_id            |varchar(20) |                 |NO      |应用编号


##### 2.2.2 Indexes
Key                     |Type |Unique|Columns        |Comments
------------------------|-----|------|---------------|--------
PRIMARY                 |BTREE|YES   |c_id           |主键索引
u_organization_id_app_id|BTREE|YES   |c_organization_id,c_app_id           |组织机构编号应用编号唯一索引


##### 2.2.3 DDL
```sql
CREATE TABLE `t_organization_subscribed_app` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构编号',
  `c_app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用编号',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_organization_id_app_id` (`c_organization_id`,`c_app_id`) USING BTREE COMMENT '组织机构编号应用编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组织机构订阅应用'
```

#### 2.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_organization_id`进行数据切分。使用`c_app_id`进行在线交易的查询场景很少，一般都是统计应用被订阅数量等需求，这可以采用其它数据聚合方案进行查询操作。


### 3.成员订阅应用
#### 3.1 概念与规则
本表记录成员订阅的应用。某些应用不必需组织机构统一订阅，成员可以按需要自行订阅使用，该表记录的就是成员对这类应用的订阅情况。这些应用在没有获得组织机构的订阅和信息项授权前无法获取到属于组织机构的相关信息。

#### 3.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_member_subscribed_app
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |成员订阅应用

##### 3.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_organization_id   |varchar(20) |                 |NO      |组织机构编号
c_member_id         |varchar(20) |                 |NO      |成员编号
c_app_id            |varchar(20) |                 |NO      |应用编号


##### 3.2.2 Indexes
Key               |Type |Unique|Columns             |Comments
------------------|-----|------|--------------------|--------
PRIMARY           |BTREE|YES   |c_id                |主键索引
u_member_id_app_id|BTREE|YES   |c_member_id,c_app_id|成员编号应用编号唯一索引

##### 3.2.3 DDL
```sql
CREATE TABLE `t_member_subscribed_app` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构编号',
  `c_member_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '成员编号',
  `c_app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用编号',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_member_id_app_id` (`c_member_id`,`c_app_id`) USING BTREE COMMENT '成员编号应用编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成员订阅应用'
```

#### 3.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_member_id`进行数据切分。使用`c_app_id`进行在线交易的查询场景很少，一般都是统计应用被订阅数量等需求，这可以采用其它数据聚合方案进行查询操作。


### 4.组织机构信息项
#### 4.1 概念与规则
本表是字典表，列举出了系统存在的且第三方应用使用时需要授权的组织机构信息项。例如：企业员工信息、企业客户信息、日程、会议、直播、审批等。

#### 4.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_organization_info_item
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |组织机构信息项

##### 4.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号[信息编码]
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_info_name         |varchar(20) |                 |NO      |信息名称
c_info_desc         |varchar(100)|                 |NO      |信息描述


##### 4.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
u_info_name         |BTREE|YES   |c_info_name    |信息名称唯一索引


##### 4.2.3 DDL
```sql
CREATE TABLE `t_organization_info_item` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号[信息编码]',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_info_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '信息名称',
  `c_info_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '信息描述',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_info_name` (`c_info_name`) USING BTREE COMMENT '信息名称唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组织机构信息项'
```

#### 4.3 性能优化
字典表无需性能优化，单表可以满足性能要求。


### 5.个人信息项
#### 5.1 概念与规则
本表是字典表，列举出了系统存在的且第三方应用使用时需要授权的个人信息项。例如：手机号、姓名、身份证号、昵称、头像、地址等。

#### 5.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_personal_info_item
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |个人信息项

##### 5.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号[信息编码]
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_info_name         |varchar(20) |                 |NO      |信息名称
c_info_desc         |varchar(100)|                 |NO      |信息描述


##### 5.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
u_info_name         |BTREE|YES   |c_info_name    |信息名称唯一索引


##### 5.2.3 DDL
```sql
CREATE TABLE `t_personal_info_item` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号[信息编码]',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_info_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '信息名称',
  `c_info_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '信息描述',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_info_name` (`c_info_name`) USING BTREE COMMENT '信息名称唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='个人信息项'
```

#### 5.3 性能优化
字典表无需性能优化，单表可以满足性能要求。


### 6.应用运行必需的组织机构信息项
#### 6.1 概念与规则
本表记录的是应用正常运行必需获取到的组织机构信息项。在组织机构的成员首次进入该应用时，本系统会引导提醒该组织机构的具有管理员权限的成员进行授权。

#### 6.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_organization_info_item_required
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |应用运行必需的组织机构信息项

##### 6.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_app_id            |varchar(20) |                 |NO      |应用编号
c_organization_info_item_id|varchar(20)|           |NO      |组织机构信息项编号


##### 6.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
u_app_id_organization_info_item_id|BTREE|YES   |c_app_id,c_organization_info_item_id             |应用编号组织机构信息项编号唯一索引


##### 6.2.3 DDL
```sql
CREATE TABLE `t_organization_info_item_required` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号[信息编码]',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用编号',
  `c_organization_info_item_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构信息项编号',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_app_id_organization_info_item_id` (`c_app_id`,`c_organization_info_item_id`) USING BTREE COMMENT '应用编号组织机构信息项编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='应用运行必需的组织机构信息项'
```

#### 6.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_app_id`进行数据切分。


### 7.应用运行必需的个人信息项
#### 7.1 概念与规则
本表记录的是应用正常运行必需获取到的个人信息项。在成员首次进入该应用时，本系统会引导提醒该成员进行授权。

#### 7.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_personal_info_item_required
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |应用运行必需的个人信息项

##### 7.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_app_id            |varchar(20) |                 |NO      |应用编号
c_user_info_item_id |varchar(20) |                 |NO      |个人信息项编号


##### 7.2.2 Indexes
Key                       |Type |Unique|Columns        |Comments
--------------------------|-----|------|---------------|--------
PRIMARY                   |BTREE|YES   |c_id           |主键索引
u_app_id_personal_info_item_id|BTREE|YES   |c_app_id,c_personal_info_item_id             |应用编号个人信息项编号唯一索引


##### 7.2.3 DDL
```sql
CREATE TABLE `t_personal_info_item_required` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用编号',
  `c_personal_info_item_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '个人信息项编号',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_app_id_personal_info_item_id` (`c_app_id`,`c_personal_info_item_id`) USING BTREE COMMENT '应用编号个人信息项编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='应用运行必需的个人信息项'
```

#### 7.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_app_id`进行数据切分。

### 8.组织机构授权给应用的组织机构信息项
#### 8.1 概念与规则
记录组织机构授权给应用的组织机构信息项内容。

#### 8.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_organization_info_item_authorized
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |组织机构授权给应用的组织机构信息项

##### 8.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_organization_id   |varchar(20) |                 |NO      |组织机构编号
c_app_id            |varchar(20) |                 |NO      |应用编号
c_organization_info_item_id|varchar(20)|           |NO      |组织机构信息项编号

##### 8.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
u_organization_id_app_id_organization_info_item_id|BTREE|YES   |c_organization_id,c_app_id,c_organization_info_item_id             |组织机构编号应用编号组织机构信息项编号唯一索引

##### 8.2.3 DDL
```sql
CREATE TABLE `t_organization_info_item_authorized` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号[信息编码]',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构编号',
  `c_app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用编号',
  `c_organization_info_item_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构信息项编号',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_organization_id_app_id_organization_info_item_id` (`c_organization_id`,`c_app_id`,`c_organization_info_item_id`) USING BTREE COMMENT '组织机构编号应用编号组织机构信息项编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组织机构授权给应用的组织机构信息项'
```

#### 8.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_organization_id`进行数据切分。


### 9.成员授权给应用的个人信息项
#### 9.1 概念与规则
记录成员授权给应用的个人信息项内容。

#### 9.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_personal_info_item_authorized
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |成员授权给应用的个人信息项

##### 9.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_organization_id   |varchar(20) |                 |NO      |组织机构编号
c_member_id         |varchar(20) |                 |NO      |成员编号
c_app_id            |varchar(20) |                 |NO      |应用编号
c_personal_info_item_id|varchar(20)|               |NO      |个人信息项编号

##### 9.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
u_member_id_app_id_personal_info_item_id|BTREE|YES   |c_member_id,c_app_id,c_personal_info_item_id     |成员编号应用编号个人信息项编号唯一索引

##### 9.2.3 DDL
```sql
CREATE TABLE `t_personal_info_item_authorized` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号[信息编码]',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构编号',
  `c_member_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '成员编号',
  `c_app_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用编号',
  `c_personal_info_item_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构信息项编号',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_member_id_app_id_personal_info_item_id` (`c_member_id`,`c_app_id`,`c_personal_info_item_id`) USING BTREE COMMENT '成员编号应用编号个人信息项编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成员授权给应用的个人信息项'
```

#### 9.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_member_id`进行数据切分。

