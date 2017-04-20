/*
Navicat MySQL Data Transfer

Source Server         : 203
Source Server Version : 50173
Source Host           : 172.16.1.203:3306
Source Database       : erpdb

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2017-04-13 20:10:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `conf_deviceType`
-- ----------------------------
DROP TABLE IF EXISTS `conf_deviceType`;
CREATE TABLE `conf_deviceType` (
  `id` int(5) NOT NULL,
  `number` varchar(10) NOT NULL,
  `deviceName` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of conf_deviceType
-- ----------------------------

-- ----------------------------
-- Table structure for `conf_dpt`
-- ----------------------------
DROP TABLE IF EXISTS `conf_dpt`;
CREATE TABLE `conf_dpt` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `dptdesc` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of conf_dpt
-- ----------------------------

-- ----------------------------
-- Table structure for `conf_processLevel`
-- ----------------------------
DROP TABLE IF EXISTS `conf_processLevel`;
CREATE TABLE `conf_processLevel` (
  `id` int(5) NOT NULL,
  `level` varchar(2) DEFAULT NULL COMMENT '级别编号',
  `hourwage` float(8,0) DEFAULT NULL COMMENT '工资标准/每小时',
  `facility` float(8,0) DEFAULT NULL COMMENT '难易度',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of conf_processLevel
-- ----------------------------

-- ----------------------------
-- Table structure for `conf_stylePart`
-- ----------------------------
DROP TABLE IF EXISTS `conf_stylePart`;
CREATE TABLE `conf_stylePart` (
  `id` int(5) NOT NULL,
  `number` varchar(5) NOT NULL COMMENT '部件编号',
  `name` varchar(20) NOT NULL COMMENT '部件名称',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of conf_stylePart
-- ----------------------------

-- ----------------------------
-- Table structure for `conf_workCenter`
-- ----------------------------
DROP TABLE IF EXISTS `conf_workCenter`;
CREATE TABLE `conf_workCenter` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `number` varchar(5) NOT NULL COMMENT '工作中心编号',
  `name` varchar(20) NOT NULL COMMENT '工作中心名称',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of conf_workCenter
-- ----------------------------

-- ----------------------------
-- Table structure for `conf_workTypeSet`
-- ----------------------------
DROP TABLE IF EXISTS `conf_workTypeSet`;
CREATE TABLE `conf_workTypeSet` (
  `id` int(11) NOT NULL,
  `number` varchar(5) NOT NULL COMMENT '编号',
  `workTypeName` varchar(20) NOT NULL COMMENT '工种名称',
  `hourWage` float(8,0) NOT NULL COMMENT '计时工资单价/小时',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of conf_workTypeSet
-- ----------------------------

-- ----------------------------
-- Table structure for `user_user`
-- ----------------------------
DROP TABLE IF EXISTS `user_user`;
CREATE TABLE `user_user` (
  `id` int(5) NOT NULL,
  `number` varchar(10) NOT NULL,
  `username` varchar(30) NOT NULL,
  `password` varchar(50) NOT NULL,
  `enusername` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_user
-- ----------------------------
