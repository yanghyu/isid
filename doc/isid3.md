# 互联网系统基础架构设计·三
[TOC]

## 三、客户管理设计
### 1 国家或地区
#### 1.1 概念与规则
国家或地区概念是指事实上经济社会运行较为独立的一个个实体，建立本概念的目的是为了应用功能可以扩展到全球范围。本表依据国际标准进行设计，包含国际统一的二字码、三字码、数字码等信息。

#### 1.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_country_or_region
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |国家或地区    

##### 1.2.1 Columns
Column            |Type        |Default Value    |Nullable|Comments
------------------|------------|-----------------|--------|--------
c_id              |varchar(20) |                 |NO      |主键编号
c_create_datetime |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_name_en         |varchar(100)|                 |NO      |英文名称
c_name_zh         |varchar(20) |                 |NO      |中文名称
c_alpha_2_code    |varchar(2)  |                 |NO      |二字码
c_alpha_3_code    |varchar(3)  |                 |NO      |三字码
c_numeric_code    |varchar(3)  |                 |NO      |数字码

##### 1.2.2 Indexes
Key            |Type    |Unique |Columns        |Comments
---------------|--------|-------|---------------|--------
PRIMARY        |BTREE   |YES    |c_id           |主键索引
u_alpha_2_code |BTREE   |YES    |c_alpha_2_code |二字码唯一索引
u_alpha_3_code |BTREE   |YES    |c_alpha_3_code |三字码唯一索引
u_numeric_code |BTREE   |YES    |c_numeric_code |数字码唯一索引

##### 1.2.3 DDL
```sql
CREATE TABLE `t_country_or_region` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_name_en` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '英文名称',
  `c_name_zh` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中文名称',
    `c_alpha_2_code` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '二字码',
    `c_alpha_3_code` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '三字码',
    `c_numeric_code` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数字码',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_alpha_2_code` (`c_alpha_2_code`) USING BTREE COMMENT '二字码唯一索引',
  UNIQUE KEY `u_alpha_3_code` (`c_alpha_3_code`) USING BTREE COMMENT '三字码唯一索引',
  UNIQUE KEY `u_numeric_code` (`c_numeric_code`) USING BTREE COMMENT '数字码唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='国家或地区'
```

#### 1.3 性能优化
字典表无需性能优化，单表可以满足性能要求。


### 2.用户
#### 2.1 概念与规则
系统内的一个用户，可以是真实人注册的用户，也可以是机器人注册的用户，是唯一可以登录到系统、使用系统的实体。

#### 2.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_user
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |用户

##### 2.2.1 Columns
Column              |Type       |Default Value    |Nullable|Comments
--------------------|-----------|-----------------|--------|--------
c_id                |varchar(20)|                 |NO      |主键编号
c_create_datetime   |datetime   |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime   |CURRENT_TIMESTAMP|NO      |修改时间
c_number            |varchar(50)|                 |NO      |用户号
c_status            |tinyint    |                 |NO      |用户状态[-1:未激活 0:正常]
c_freeze_end_time   |datetime   |NULL             |YES     |冻结结束时间

##### 2.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
u_number            |BTREE|YES   |c_number       |用户号唯一索引

##### 2.2.3 DDL
```sql
CREATE TABLE `t_user` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户号',
  `c_status` tinyint NOT NULL COMMENT '用户状态[-1:未激活 0:正常]',
  `c_freeze_end_time` datetime DEFAULT NULL COMMENT '冻结结束时间',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_number` (`c_number`) USING BTREE COMMENT '用户号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户'
```

#### 2.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_id`进行数据切分。但这样会导致`u_number`的唯一性失效，解决方案可以是如下这样：

- 一、新增`t_user_number`表，仅一个字段用户号，并对此字段建立唯一索引。该表中记录的用户号都是正在使用或者已经使用过的用户号。
- 二、新增的`t_user_number`表分库分表时按用户号字段进行数据切分。
- 三、`t_user`表插入新的用户号时，需要检查`t_user_number`表中是否存在此用户号，不存在才可以新增`t_user`表记录并插入用户号到`t_user_number`表。

通过以上方案可以解决大数据量下的性能问题，同时保持唯一索引的有效性。


### 3.用户证件
#### 3.1 概念与规则
每个系统设计人员都很想定义一个概念和现实中的具体的人一一对应。但现实是系统很难做到识别和区分世界范围内的每个独立的人，因此系统只能依赖各个国家和地区发行的有效身份证件来进行一定程度的识别。这里我们定义出一个用户证件的概念来表达，这里的一个用户证件是指某一个国家或地区下的某一种具有公信力的身份证件。证件与用户关系具有以下一些规则与限制：

- 一、一个用户可以绑定多种类型证件，但每种类型证件只能绑定一个。当期望绑定的证件被其它用户绑定时，需要其他用户与该证件的绑定关系解除后才可继续绑定。
- 二、一个证件只能被一个用户进行绑定使用，如果某证件被其他用户绑定了，需要先解除原有绑定关系后再与当前用户进行绑定。

可能会出现如下一些特殊情况，值得考虑：

- 一、某个人具有两个国家的国籍，他同时拥有这两个国家的身份证。那么这个人可以创建两个用户，然后将这两个国家的身份证分别与这两个用户进行关联。
- 二、某个人只有一个国籍，他在本系统创建了两个用户，然后他使用这个国家的两个不同类型的身份证件分别与这两个用户进行绑定。

针对上述特殊情况一，具有不同国家或地区的国籍的同一个人被我们系统识别为两个人其实也并无不妥，因为我们主要关注的是身份是否真实有效。针对上述特殊情况二，我们会尽量减少一个国家和地区内可使用的身份证件类型的范围，尽可能减少一人多证件的注册情况出现，一般一个国家内都有一种统一的有效身份证件。

一些业务如果需要保证一个人只能办理一次某业务，那么可以要求每个国家或地区仅允许某一种证件实名的用户才可以办理此项业务。

#### 3.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_user_certificate
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |用户证件   

##### 3.2.1 Columns
Column              |Type       |Default Value    |Nullable|Comments
--------------------|-----------|-----------------|--------|--------
c_id                |varchar(20)|                 |NO      |主键编号
c_create_datetime   |datetime   |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime   |CURRENT_TIMESTAMP|NO      |修改时间
c_user_id           |varchar(20)|                 |NO      |用户编号
c_name              |varchar(50)|                 |NO      |姓名
c_cor_id            |varchar(20)|                 |NO      |国家或地区编号
c_certificate_type  |smallint   |                 |NO      |证件类型
c_certificate_number|varchar(50)|                 |NO      |证件编号

##### 3.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
u_certificate_number|BTREE|YES   |c_certificate_number,c_cor_id,c_certificate_type|证件编号唯一索引
u_user_id           |BTREE|YES   |c_user_id,c_cor_id,c_certificate_type|用户编号唯一索引

##### 3.2.3 DDL
```sql
CREATE TABLE `t_user_certificate` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
  `c_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `c_cor_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '国家或地区编号',
  `c_certificate_type` smallint NOT NULL COMMENT '证件类型',
  `c_certificate_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '证件编号',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_certificate_number` (`c_certificate_number`,`c_cor_id`,`c_certificate_type`) USING BTREE COMMENT '证件编号唯一索引',
  UNIQUE KEY `u_user_id` (`c_user_id`,`c_cor_id`,`c_certificate_type`) USING BTREE COMMENT '用户编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户证件'
```

#### 3.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_user_id`进行数据切分。但这样就会导致使用`c_certificate_number`字段进行查询本表时无法定位出具体的库和表，且`u_certificate_number`索引唯一性失效，怎么解决这个问题呢？方案如下：

- 一、新增`t_user_certificate_reverse`表，该表结构与`t_user_certificate`结构一模一样。
- 二、新增的`t_user_certificate_reverse`表分库分表时按`c_certificate_number`字段进行数据切分。
- 三、`t_user_certificate`表与`t_user_certificate_reverse`表数据同步维护更新，保持一致性。

通过以上方案可以解决大数据量下的性能问题，同时保持唯一索引的有效性。


### 4 用户设备
#### 4.1 概念与规则
记录用户的可信设备。一个用户可以有多个可信设备，可信设备和不可信设备在登录、具体业务等操作方面应该有一定的区别。用户可以管理自己的可信设备，对于不再使用的可信设备予以删除操作。

#### 4.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_user_device
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |用户设备

##### 4.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_user_id           |varchar(20) |                 |NO      |用户编号
c_device_name       |varchar(100)|                 |NO      |设备名称
c_device_number     |varchar(100)|                 |NO      |设备编号
c_device_os         |varchar(20) |                 |NO      |设备操作系统

##### 4.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
i_user_id           |BTREE|NO    |c_user_id      |用户编号索引

##### 4.2.3 DDL
```sql
CREATE TABLE `t_user_device` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
  `c_device_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备名称',
  `c_device_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备编号',
  `c_device_os` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备操作系统',
  PRIMARY KEY (`c_id`),
  KEY `i_user_id` (`c_user_id`) USING BTREE COMMENT '用户编号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户设备'
```

#### 4.3 性能优化
数据量大时，如若需要分库分表可以使用字`c_user_id`进行数据切分。


### 5 用户认证
#### 5.1 概念与规则
记录用户设置或者授权的认证账号，这些账号可以用来进行用户登录认证等操作。这里的账号包括手机号、邮箱、账密、微信、支付宝、手势、指纹、人脸等。这些信息是用户认证的最有效有价值的信息，可以用于具体的业务服务，但需要得到用户的确认授权。

认证账号有如下规则特点：

- 一个用户可以有多种类型的认证账号，并且每种类型认证账号还可以有多个。
- 本表中每种类型的账号不能重复。
- 每个账号只能属于一个用户。

#### 5.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_user_auth
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |用户认证

##### 5.2.1 Columns
Column           |Type        |Default Value    |Nullable|Comments
-----------------|------------|-----------------|--------|--------
c_id             |varchar(20) |                 |NO      |主键编号
c_create_datetime|datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime|datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_user_id        |varchar(20) |                 |NO      |用户编号
c_auth_type      |tinyint     |                 |NO      |认证类型[1:手机号(account:手机号，password:空) 2:普通账号(account:账号名，password:转加密密码密文)  3:邮箱(account:邮箱地址，password:转加密密码密文或者空)  4:微信(account:微信OPENID，password:空)  5:支付宝(account:支付宝OPENID，password:空)   81:快捷登录-手势(account:设备编号，password:转加密密码密文)  82:快捷登录-指纹(account:设备编号，password:转加密密码密文)  83:快捷登录-人脸(account:设备编号，password:转加密密码密文)]
c_account        |varchar(100)|                 |NO      |账号
c_password       |varchar(128)|NULL             |YES     |密码
c_status         |tinyint     |                 |NO      |账号状态[-2:预录入 -1:未激活 0:正常]
c_freeze_end_time|datetime    |NULL             |YES     |冻结结束时间

##### 5.2.2 Indexes
Key                 |Type |Unique|Columns               |Comments
--------------------|-----|------|----------------------|--------
PRIMARY             |BTREE|YES   |c_id                  |主键索引
i_user_id           |BTREE|NO    |c_user_id             |用户编号索引
u_account           |BTREE|YES   |c_account,c_auth_type |账号唯一索引

##### 5.2.3 DDL
```sql
CREATE TABLE `t_user_auth` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
  `c_auth_type` tinyint NOT NULL COMMENT '认证类型[1:手机号(account:手机号，password:空) 2:普通账号(account:账号名，password:转加密密码密文)  3:邮箱(account:邮箱地址，password:转加密密码密文或者空)  4:微信(account:微信OPENID，password:空)  5:支付宝(account:支付宝OPENID，password:空)   81:快捷登录-手势(account:设备编号，password:转加密密码密文)  82:快捷登录-指纹(account:设备编号，password:转加密密码密文)  83:快捷登录-人脸(account:设备编号，password:转加密密码密文)]',
  `c_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `c_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `c_status` tinyint NOT NULL COMMENT '账号状态[-2:预录入 -1:未激活 0:正常]',
  `c_freeze_end_time` datetime DEFAULT NULL COMMENT '冻结结束时间',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_account` (`c_account`,`c_auth_type`) USING BTREE COMMENT '账号唯一索引',
  KEY `i_user_id` (`c_user_id`) USING BTREE COMMENT '用户编号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户认证'
```

#### 5.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_user_id`进行数据切分。但这样就会导致使用`c_account`字段进行查询本表时无法定位出具体的库和表，且`u_account`索引唯一性失效。和前面一样，方案如下：

- 一、新增`t_user_auth_reverse`表，该表结构与`t_user_auth`结构一模一样。
- 二、新增的`t_user_auth_reverse`表分库分表时按`c_account`字段进行数据切分。
- 三、`t_user_auth_reverse`表与`t_user_auth`表数据同步维护更新，保持一致性。

通过以上方案可以解决大数据量下的性能问题，同时保持唯一索引的有效性。


### 6 组织机构
#### 6.1 概念与规则
代表系统中的一个组织机构，与现实中组织机构不一定一一对应。可以是虚拟的，也可以是实际的。组织机构和用户一样可以关联企业证件，从而成为一个实名的组织机构。这里有个特殊组织机构需要说明下：

- 主键编号为‘--------------------’的组织机构为代表本系统的一个组织机构，每个User默认都加入本系统，成为本系统的一个会员，用户在此组织中的会员昵称就是用户在本系统中的昵称，用户在此组织中的其它会员信息亦然，均代表在本系统中的信息，这样做主要是为了设计上的统一和简洁，使个人版和企业版应用共用的基础数据架构。

#### 6.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_organization
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |组织机构

##### 6.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号[--------------------:代表本系统的组织机构编号，每个用户默认都加入本系统组织机构，成为本系统组织机构的一个会员]
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_name              |varchar(100)|                 |NO      |组织名称

##### 6.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引

##### 6.2.3 DDL
```sql
CREATE TABLE `t_organization` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号[--------------------:代表本系统的组织机构编号，每个用户默认都加入本系统组织机构，成为本系统组织机构的一个会员]',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织名称',
  PRIMARY KEY (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组织机构'
```
#### 6.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_id`进行数据切分。通过组织名称搜索组织这样的功能则需要结合使用ES等聚合搜索工具来完成。


### 7.组织机构证件
#### 7.1 概念与规则
和前面提到的用户证件一样，每个系统设计人员都很想定义一个概念和现实中的具体的组织机构一一对应。但现实是系统很难做到识别和区分世界范围内的每个独立的组织机构，因此系统只能依赖各个国家和地区发行的有效组织机构证件来进行一定程度的识别。这里我们定义出一个组织机构证件的概念来表达，这里的一个组织机构证件是指某一个国家或地区下的某一种具有公信力的证件。证件与组织机构关系具有以下一些限制：

- 一、一个组织机构可以绑定多种类型证件，但每种类型的证件一个组织机构只能绑定一个。当期望绑定的证件被其它组织机构绑定时，需要其他组织机构与该证件解除绑定关系后才可继续绑定当前组织机构。
- 二、一个证件只能被一个组织机构进行绑定使用，如果某证件被其他组织机构绑定了，需要先解除绑定关系后再与当前组织机构进行绑定。

可能会出现如下一些特殊情况，值得考虑：

- 一、某个组织机构具有两个国家的证件。那么这个它可以创建两个组织机构，然后将这两个国家的证件分别与这两个组织机构进行关联。
- 二、某组织机构在本系统创建了两个组织机构，然后他使用这个国家的两个不同类型的证件分别与这两个本系统组织机构进行绑定。

针对上述特殊情况一，具有不同国家或地区的证件的同一个组织机构被我们系统识别为两个组织机构其实也并无不妥，因为我们主要关注的是身份是否真实有效。针对上述特殊情况二，我们会尽量减少一个国家和地区内可使用的组织机构证件类型的范围，尽可能减少一组织机构多证件的注册情况出现，一般一个国家内都有一种统一的有效组织机构证件。

一些业务如果需要保证一个组织机构只能办理一次某业务，那么可以要求每个国家或地区仅允许某一种证件实名的组织机构才可以办理此项业务。

#### 7.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_organization_certificate
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |组织机构证件   

##### 7.2.1 Columns
Column              |Type       |Default Value    |Nullable|Comments
--------------------|-----------|-----------------|--------|--------
c_id                |varchar(20)|                 |NO      |主键编号
c_create_datetime   |datetime   |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime   |CURRENT_TIMESTAMP|NO      |修改时间
c_organization_id   |varchar(20)|                 |NO      |组织机构编号
c_name              |varchar(50)|                 |NO      |组织机构证件名称
c_cor_id            |varchar(20)|                 |NO      |国家或地区编号
c_certificate_type  |smallint   |                 |NO      |证件类型
c_certificate_number|varchar(50)|                 |NO      |证件编号
c_registration_date |date       |                 |NO      |注册日期
c_cancellation_date |date       |NULL             |YES     |注销日期[为null代表未注销]

##### 7.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引
u_certificate_number|BTREE|YES   |c_certificate_number,c_cor_id,c_certificate_type|证件编号唯一索引
u_organization_id   |BTREE|YES   |c_organization_id,c_cor_id,c_certificate_type|组织机构编号唯一索引

##### 7.2.3 DDL
```sql
CREATE TABLE `t_organization_certificate` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构编号',
  `c_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构证件名称',
  `c_cor_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '国家或地区编号',
  `c_certificate_type` smallint NOT NULL COMMENT '证件类型',
  `c_certificate_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '证件编号',
  `c_registration_date` date NOT NULL COMMENT '注册日期',
  `c_cancellation_date` date DEFAULT NULL COMMENT '注销日期[为null代表未注销]',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_certificate_number` (`c_certificate_number`,`c_cor_id`,`c_certificate_type`) USING BTREE COMMENT '证件编号唯一索引',
  UNIQUE KEY `u_organization_id` (`c_organization_id`,`c_cor_id`,`c_certificate_type`) USING BTREE COMMENT '组织机构编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组织机构证件'
```

#### 7.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_organization_id`进行数据切分。但这样就会导致使用`c_certificate_number`字段进行查询本表时无法定位出具体的库和表，且`u_certificate_number`索引唯一性失效。和前面一样，方案如下：

- 一、新增`t_organization_certificate_reverse`表，该表结构与`t_organization_certificate`结构一模一样。
- 二、新增的`t_organization_certificate_reverse`表分库分表时按`c_certificate_number`字段进行数据切分。
- 三、`t_organization_certificate_reverse`表与`t_organization_certificate`表数据同步维护更新，保持一致性。

通过以上方案可以解决大数据量下的性能问题，同时保持唯一索引的有效性。


### 8 部门
#### 8.1 概念与规则
组织机构下的部门，数据结构为树结构。每个组织机构都有一个默认根部门，这个组织机构创建的其它部门都需要挂载在默认根部门为根节点的树结构下。加入一个组织机构的用户（即“会员”，下文有介绍）不属于该组织机构的任何其他部门的话，就需要属于这个默认根部门。

- 一个会员可以同时属于对应组织机构下的多个部门。
- 一个会员至少需要属于对应组织机构下的一个部门。

#### 8.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_department
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |部门

##### 8.2.1 Columns
Column            |Type        |Default Value    |Nullable|Comments
------------------|------------|-----------------|--------|--------
c_id              |varchar(20) |                 |NO      |主键编号
c_create_datetime |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_organization_id |varchar(20) |                 |NO      |所属组织机构编号
c_parent_id       |varchar(20) |                 |YES     |父部门编号（null表示一级部门，即默认根部门）
c_department_route|varchar(200)|                 |NO      |部门路径[根节点到本节点的编号使用|线作为分割拼接起来]
c_department_name |varchar(50) |                 |NO      |部门名称

##### 8.2.2 Indexes
Key               |Type |Unique|Columns           |Comments
------------------|-----|------|------------------|--------
PRIMARY           |BTREE|YES   |c_id              |主键索引
i_organization_id |BTREE|NO    |c_organization_id |所属组织机构编号索引
i_parent_id       |BTREE|NO    |c_parent_id       |父部门编号索引
i_department_route|BTREE|NO    |c_department_route|部门路径索引


##### 8.2.3 DDL
```sql
CREATE TABLE `t_department` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属组织机构编号',
  `c_parent_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '父部门编号（null表示一级部门，即默认根部门）',
  `c_department_route` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门路径[根节点到本节点的编号使用|线作为分割拼接起来]',
  `c_department_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门名称',
  PRIMARY KEY (`c_id`),
  KEY `i_organization_id` (`c_organization_id`) USING BTREE COMMENT '所属组织机构编号索引',
  KEY `i_parent_id` (`c_parent_id`) USING BTREE COMMENT '父部门编号索引',
  KEY `i_department_route` (`c_department_route`) USING BTREE COMMENT '部门路径索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门'
```

#### 8.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_organization_id`进行数据切分。


### 9 群组
#### 9.1 概念与规则
会员（下文有介绍）发起建立的一个群组。用于多人业务场景，如群聊。

#### 9.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_group
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |群组

##### 9.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_group_name        |varchar(100)|                 |NO      |群组名称
c_group_remark      |varchar(200)|                 |NO      |群组备注

##### 9.2.2 Indexes
Key                 |Type |Unique|Columns        |Comments
--------------------|-----|------|---------------|--------
PRIMARY             |BTREE|YES   |c_id           |主键索引

##### 9.2.3 DDL
```sql
CREATE TABLE `t_group` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_group_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '群组名称',
  `c_group_remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '群组备注',
  PRIMARY KEY (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='群组'
```

#### 9.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_id`进行数据切分。


### 10 会员
#### 10.1 概念与规则
代表组织机构与用户间的关系，我们将此定义为会员。会员信息记录的就是这个用户在这个组织机构中的信息。一个用户在一个组织机构中最多只能有一条会员记录。会员表示的是用户在组织机构中的一个关系。需要代表某组织机构或者需要以某组织机构身份进行的业务操作场景应该将操作信息对应记录在会员这个概念上。用户在本系统中的信息如果不是通用于所有组织机构上，则应该记录在本系统组织机构对应的那条会员记录上。

- 一个用户可以加入一个或者多个组织机构，从而成为一个或者多个组织机构中的会员（用户至少是本系统组织机构的会员）。
- 一个组织机构可以包含一个或者多个会员，每个会员就是用户在这个组织机构的关系表示（每个组织机构至少有一个会员）。
- 同一组织机构中的任意两个会员不能对应于同一用户。

#### 10.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_member
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |会员

##### 10.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_organization_id   |varchar(20) |                 |NO      |关联组织机构编号[--------------------:代表本系统组织机构编号，每个User默认都加入本系统，成为本系统组织机构的一个用户]
c_user_id           |varchar(20) |                 |NO      |关联用户编号
c_nickname          |varchar(50) |                 |NO      |组织机构内昵称[用户在本系统组织机构内的昵称即为本系统内昵称]
c_personal_signature|varchar(200)|NULL             |YES     |组织机构内个性签名[用户在本系统组织机构内的个性签名即为本系统内个性签名]
c_administrator_tag |tinyint     |                 |NO      |组织机构管理员标记[1:普通人员 3:管理员 7:拥有者]

##### 10.2.2 Indexes
Key                      |Type |Unique|Columns        |Comments
-------------------------|-----|------|---------------|--------
PRIMARY                  |BTREE|YES   |c_id           |主键索引
u_organization_id_user_id|BTREE|YES   |c_organization_id,c_user_id          |组织机构编号用户编号唯一索引
i_user_id                |BTREE|NO    |c_user_id      |用户编号索引

##### 10.2.3 DDL
```sql
CREATE TABLE `t_member` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联组织机构编号[--------------------:代表本系统组织机构编号，每个User默认都加入本系统，成为本系统组织机构的一个用户]',
  `c_user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联用户编号',
  `c_nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织机构内昵称[用户在本系统组织机构内的昵称即为本系统内昵称]',
  `c_personal_signature` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组织机构内个性签名[用户在本系统组织机构内的个性签名即为本系统内个性签名]',
  `c_administrator_tag` tinyint NOT NULL COMMENT '组织机构管理员标记[1:普通人员 3:管理员 7:拥有者]',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_organization_id_user_id` (`c_organization_id`,`c_user_id`) USING BTREE COMMENT '组织机构编号用户编号唯一索引',
  KEY `i_user_id` (`c_user_id`) USING BTREE COMMENT '用户编号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员'
```

#### 10.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_organization_id`进行数据切分。但这样就会导致使用`c_user_id`字段进行查询本表时无法定位出具体的库和表。和前面一样，方案如下：

- 一、新增`t_member_reverse`表，该表结构与`t_member`结构一模一样。
- 二、新增的`t_member_reverse`表分库分表时按`c_user_id`字段进行数据切分。
- 三、`t_member_reverse`表与`t_member`表数据同步维护更新，保持一致性。

通过以上方案可以解决大数据量下的性能问题，同时保持唯一索引的有效性。


### 11 会员部门
#### 11.1 概念与规则
记录会员所属的部门信息。一个会员可以属于一个或多个部门。每个组织机构必须有一个默认根部门，会员最初加入这个组织机构时，就默认位于这个根部门下。一个会员不属于该组织机构的任何其他部门的话，他就需要属于这个默认根部门。

- 一个会员可以同时属于对应组织机构下的多个部门。
- 一个会员至少需要属于对应组织机构下的一个部门。

#### 11.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_member_department
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |会员部门

##### 11.2.1 Columns
Column             |Type        |Default Value    |Nullable|Comments
-------------------|------------|-----------------|--------|--------
c_id               |varchar(20) |                 |NO      |主键编号
c_create_datetime  |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime  |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_organization_id  |varchar(20) |                 |NO      |所属组织机构编号[为冗余字段，主要用于数据分库分表使用]
c_member_id        |varchar(20) |                 |NO      |会员编号
c_department_id    |varchar(20) |                 |NO      |所属部门编号

##### 11.2.2 Indexes
Key                      |Type |Unique|Columns        |Comments
-------------------------|-----|------|---------------|--------
PRIMARY                  |BTREE|YES   |c_id           |主键索引
u_member_id_department_id|BTREE|YES   |c_member_id,c_department_id          |会员编号所属部门编号唯一索引
i_department_id          |BTREE|NO    |c_department_id|所属部门编号索引

##### 11.2.3 DDL
```sql
CREATE TABLE `t_member_department` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属组织机构编号[为冗余字段，主要用于数据分库分表使用]',
  `c_member_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会员编号',
  `c_department_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属部门编号',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_member_id_department_id` (`c_member_id`,`c_department_id`) USING BTREE COMMENT '会员编号所属部门编号唯一索引',
  KEY `i_department_id` (`c_department_id`) USING BTREE COMMENT '所属部门编号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员部门'
```

#### 11.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_organization_id`进行数据切分。


### 12 会员联系人
#### 12.1 概念与规则
记录会员的联系人。如果两个会员相互是联系人，这里会有两条数据，也就是说这里记录的数据是单向的。联系人可以是自己所属组织机构以内的也可以是以外的。

#### 12.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_member_contact
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |会员联系人

##### 12.2.1 Columns
Column             |Type       |Default Value    |Nullable|Comments
-------------------|-----------|-----------------|--------|--------
c_id               |varchar(20)|                 |NO      |主键编号
c_create_datetime  |datetime   |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime  |datetime   |CURRENT_TIMESTAMP|NO      |修改时间
c_organization_id  |varchar(20)|                 |NO      |会员所属组织机构编号[为冗余字段，主要用于数据分库分表使用]
c_member_id        |varchar(20)|                 |NO      |会员编号
c_contact_organization_id|varchar(20)|           |NO      |联系人所属组织机构编号[为冗余字段，用于快速确认联系人是否位于本组织机构内部]
c_contact_member_id|varchar(20)|                 |NO      |联系人编号

##### 12.2.2 Indexes
Key                          |Type |Unique|Columns        |Comments
-----------------------------|-----|------|---------------|--------
PRIMARY                      |BTREE|YES   |c_id           |主键索引
u_member_id_contact_member_id|BTREE|YES   |c_member_id,c_contact_organization_id,c_contact_member_id|组织机构编号用户编号唯一索引


##### 12.2.3 DDL
```sql
CREATE TABLE `t_member_contact` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会员所属组织机构编号[为冗余字段，主要用于数据分库分表使用]',
  `c_member_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会员编号',
  `c_contact_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人所属组织机构编号[为冗余字段，用于快速确认联系人是否位于本组织机构内部]',
  `c_contact_member_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人编号',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_member_id_contact_member_id` (`c_member_id`,`c_contact_organization_id`,`c_contact_member_id`) USING BTREE COMMENT '会员编号联系人编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员联系人'
```

#### 12.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_organization_id`进行数据切分。


### 13 群组会员
#### 13.1 概念与规则
记录群组包含的会员，本表为主表用于记录群组与会员关联关系相关的各项业务信息。

#### 13.2 TableInfo
Attribute          |Value   
-------------------|-----------
Table name         |t_group_member
Engine             |InnoDB
Charset            |utf8mb4
Collation          |utf8mb4_general_ci   
Comment            |群组会员

##### 13.2.1 Columns
Column              |Type        |Default Value    |Nullable|Comments
--------------------|------------|-----------------|--------|--------
c_id                |varchar(20) |                 |NO      |主键编号
c_create_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |创建时间
c_update_datetime   |datetime    |CURRENT_TIMESTAMP|NO      |修改时间
c_group_id          |varchar(20) |                 |NO      |群组编号
c_member_id         |varchar(20) |                 |NO      |会员编号
c_organization_id   |varchar(20) |                 |NO      |会员所属组织机构编号[为冗余字段，主要用于数据分库分表使用]
c_administrator_tag |tinyint     |                 |NO      |群组管理员标记[1:普通人员 3:管理员 7:群主]

##### 13.2.2 Indexes
Key                 |Type |Unique|Columns               |Comments
--------------------|-----|------|----------------------|--------
PRIMARY             |BTREE|YES   |c_id                  |主键索引
u_group_id_member_id|BTREE|YES   |c_group_id,c_member_id|群组编号会员编号唯一索引

##### 13.2.3 DDL
```sql
CREATE TABLE `t_group_member` (
  `c_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键编号',
  `c_create_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `c_update_datetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `c_group_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '群组编号',
  `c_member_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会员编号',
  `c_organization_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会员所属组织机构编号[为冗余字段，主要用于数据分库分表使用]',
  `c_administrator_tag` tinyint NOT NULL COMMENT '群组管理员标记[1:普通人员 3:管理员 7:群主]',
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `u_group_id_member_id` (`c_group_id`,`c_member_id`) USING BTREE COMMENT '群组编号会员编号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='群组会员'
```

#### 13.3 性能优化
数据量大时，如若需要分库分表可以使用字段`c_group_id`进行数据切分。但这样就会导致使用`c_member_id`字段进行查询本表时无法定位出具体的库和表。和前面一样，方案如下：

- 一、新增`t_group_member_reverse`表，该表结构与`t_group_member`结构一模一样。
- 二、新增的`t_group_member_reverse`表分库分表时按`c_organization_id`字段进行数据切分。
- 三、`t_group_member_reverse`表与`t_group_member`表数据同步维护更新，保持一致性。

通过以上方案可以解决大数据量下的性能问题，同时保持唯一索引的有效性。
