CREATE TABLE `sys_resource` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` int(10) DEFAULT NULL COMMENT '父id',
  `type` tinyint(3) DEFAULT NULL COMMENT '1-菜单 2-资源 3-其他',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `path` varchar(255) DEFAULT NULL COMMENT '路径',
  `authentication` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否需要认证 0-否 1-是',
  `authorization` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否需要权限 0-否 1-是',
  `sort` int(10) DEFAULT '0' COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='系统资源表';

CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `code` varchar(255) DEFAULT NULL COMMENT '角色编码',
  `admin` tinyint(2) DEFAULT '0' COMMENT '是否是管理员 0-否 1-是',
  `state` tinyint(3) DEFAULT NULL COMMENT '状态 1-正常 2-冻结',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) COMMENT='角色表';

CREATE TABLE `sys_role_resource` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int(10) NOT NULL COMMENT '角色id',
  `resource_id` int(10) NOT NULL COMMENT '资源id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_res` (`role_id`,`resource_id`)
) COMMENT='角色资源表';

CREATE TABLE `sys_user` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `login_name` varchar(255) NOT NULL COMMENT '登录名',
  `name` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `state` tinyint(3) NOT NULL COMMENT '状态 1-正常 2-冻结',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_login_name` (`login_name`)
) COMMENT='用户表';

CREATE TABLE `sys_user_role`
(
  `id`          int(10)      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_uuid`   varchar(255) NOT NULL COMMENT '用户编号',
  `role_id`     int(10)      NOT NULL COMMENT '角色id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT ='用户角色表';
