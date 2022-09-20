SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `api_alarm`;
CREATE TABLE `api_alarm`  (
  `api_id` varchar(20) NOT NULL,
  `alarm_plugin` varchar(255) NULL DEFAULT NULL,
  `alarm_plugin_param` varchar(1024) NULL DEFAULT NULL
);

DROP TABLE IF EXISTS `api_auth`;
CREATE TABLE `api_auth`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NULL DEFAULT NULL,
  `group_id` varchar(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
);

DROP TABLE IF EXISTS `api_config`;
CREATE TABLE `api_config`  (
  `id` varchar(255) NOT NULL,
  `path` varchar(255) NULL DEFAULT NULL,
  `name` varchar(255) NULL DEFAULT NULL,
  `note` varchar(255) NULL DEFAULT NULL,
  `params` text NULL,
  `status` int(0) NULL DEFAULT NULL,
  `datasource_id` varchar(255) NULL DEFAULT NULL,
  `previlege` int(0) NULL DEFAULT NULL,
  `group_id` varchar(255) NULL DEFAULT NULL,
  `cache_plugin` varchar(255) NULL DEFAULT NULL,
  `cache_plugin_params` varchar(255) NULL DEFAULT NULL,
  `create_time` varchar(20) NULL DEFAULT NULL,
  `update_time` varchar(20) NULL DEFAULT NULL,
  `content_type` varchar(50) NULL DEFAULT NULL,
  `open_trans` int(0) NULL DEFAULT NULL,
  `json_param` text NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `path`(`path`) USING BTREE
);

DROP TABLE IF EXISTS `api_group`;
CREATE TABLE `api_group`  (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
);

DROP TABLE IF EXISTS `api_sql`;
CREATE TABLE `api_sql`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `api_id` varchar(11) NOT NULL,
  `sql_text` text NOT NULL,
  `transform_plugin` varchar(255) NULL DEFAULT NULL,
  `transform_plugin_params` varchar(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
);

DROP TABLE IF EXISTS `datasource`;
CREATE TABLE `datasource`  (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NULL DEFAULT NULL,
  `note` varchar(255) NULL DEFAULT NULL,
  `type` varchar(255) NULL DEFAULT NULL,
  `url` varchar(255) NULL DEFAULT NULL,
  `username` varchar(255) NULL DEFAULT NULL,
  `password` varchar(255) NULL DEFAULT NULL,
  `driver` varchar(100) NULL DEFAULT NULL,
  `table_sql` varchar(255) NULL DEFAULT NULL,
  `create_time` varchar(20) NULL DEFAULT NULL,
  `update_time` varchar(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
);

DROP TABLE IF EXISTS `firewall`;
CREATE TABLE `firewall`  (
  `status` varchar(255) NULL DEFAULT NULL,
  `mode` varchar(255) NULL DEFAULT NULL
);

DROP TABLE IF EXISTS `ip_rules`;
CREATE TABLE `ip_rules`  (
  `type` varchar(255) NULL DEFAULT NULL,
  `ip` varchar(10240) NULL DEFAULT NULL
);

DROP TABLE IF EXISTS `token`;
CREATE TABLE `token`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NULL DEFAULT NULL,
  `note` varchar(255) NULL DEFAULT NULL,
  `expire` bigint(0) NULL DEFAULT NULL,
  `create_time` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
);

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NULL DEFAULT NULL,
  `password` varchar(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
);

INSERT INTO `ip_rules` VALUES ('white', NULL);
INSERT INTO `ip_rules` VALUES ('black', NULL);

INSERT INTO `user` VALUES (1, 'admin', 'admin');

SET FOREIGN_KEY_CHECKS = 1;
