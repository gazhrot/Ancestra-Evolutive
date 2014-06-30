/*
Navicat MySQL Data Transfer

Source Server         : batadase
Source Server Version : 50612
Source Host           : localhost:3306
Source Database       : database

Target Server Type    : MYSQL
Target Server Version : 50612
File Encoding         : 65001

Date: 2014-06-05 13:42:57
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `mountpark_data`
-- ----------------------------
DROP TABLE IF EXISTS `mountpark_data`;
CREATE TABLE `mountpark_data` (
  `mapid` int(11) NOT NULL,
  `cellid` int(11) NOT NULL,
  `size` int(11) NOT NULL,
  `owner` int(11) NOT NULL,
  `guild` int(11) NOT NULL DEFAULT '-1',
  `price` int(11) NOT NULL,
  `data` text NOT NULL,
  PRIMARY KEY (`mapid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of mountpark_data
-- ----------------------------
INSERT INTO `mountpark_data` VALUES ('8760', '753', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('2221', '242', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4308', '507', '8', '0', '-1', '1973000', '');
INSERT INTO `mountpark_data` VALUES ('8848', '549', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('8744', '579', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('8743', '605', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('9748', '356', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9747', '78', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9746', '169', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8747', '615', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('9745', '168', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9744', '164', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9743', '208', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9736', '194', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9737', '243', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9738', '109', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9739', '388', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9740', '178', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9741', '298', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9728', '359', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9742', '214', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9732', '112', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8746', '208', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('9733', '345', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9734', '279', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8745', '235', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('9735', '342', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9356', '399', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8752', '602', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('9357', '294', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9354', '264', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9353', '324', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9355', '308', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9358', '252', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9352', '112', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9729', '355', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9730', '282', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9731', '279', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9726', '343', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10249', '164', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9349', '354', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9350', '125', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9346', '226', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9345', '226', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9725', '199', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9342', '327', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10561', '380', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10559', '380', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10557', '287', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10554', '374', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10602', '365', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10601', '366', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10600', '373', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10599', '293', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10606', '308', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10607', '325', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10609', '331', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10611', '381', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10622', '323', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10630', '412', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('10618', '380', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8598', '374', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8604', '337', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8564', '168', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8567', '323', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8570', '299', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8610', '236', '5', '1', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8607', '354', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4966', '169', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4967', '119', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4634', '268', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4757', '324', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4806', '342', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4805', '381', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4809', '357', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4810', '358', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4705', '367', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4711', '323', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4729', '338', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4723', '134', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8750', '468', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('8851', '578', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8749', '614', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('8748', '550', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('8751', '356', '5', '-1', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('9450', '128', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9449', '268', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9451', '442', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9455', '416', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9453', '381', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9456', '181', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9457', '284', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9458', '401', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9459', '454', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9462', '426', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9461', '151', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9460', '308', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9463', '268', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9464', '381', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9465', '315', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9466', '307', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8757', '604', '5', '0', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('8758', '181', '5', '0', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('8759', '655', '5', '0', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('9268', '598', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9270', '252', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9273', '271', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9274', '655', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9278', '753', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9277', '361', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4246', '180', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4245', '587', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4242', '727', '8', '0', '-1', '1973000', '');
INSERT INTO `mountpark_data` VALUES ('4207', '636', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4206', '579', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4079', '418', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4211', '253', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4210', '603', '3', '0', '-1', '660000', '');
INSERT INTO `mountpark_data` VALUES ('4209', '651', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4225', '485', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4309', '541', '16', '0', '-1', '3630000', '');
INSERT INTO `mountpark_data` VALUES ('4248', '393', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4342', '458', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4241', '595', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4240', '326', '11', '0', '-1', '2420000', '');
INSERT INTO `mountpark_data` VALUES ('4238', '255', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4233', '523', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4243', '559', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4273', '247', '9', '0', '-1', '2090000', '');
INSERT INTO `mountpark_data` VALUES ('4269', '560', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4264', '617', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4278', '427', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4272', '397', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4271', '116', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4265', '620', '11', '0', '-1', '2420000', '');
INSERT INTO `mountpark_data` VALUES ('4262', '587', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4261', '325', '8', '0', '-1', '1973000', '');
INSERT INTO `mountpark_data` VALUES ('4217', '669', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4219', '431', '5', '15', '-1', '0', '');
INSERT INTO `mountpark_data` VALUES ('4218', '448', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4213', '597', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4216', '302', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4215', '155', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4270', '714', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4096', '652', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4104', '162', '3', '0', '-1', '660000', '');
INSERT INTO `mountpark_data` VALUES ('4284', '283', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4291', '532', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4275', '451', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4280', '196', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4287', '257', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4282', '172', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4169', '490', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4172', '615', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4300', '193', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4289', '506', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4181', '678', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4178', '541', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4212', '232', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4170', '467', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4204', '455', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4182', '308', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4208', '208', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4299', '472', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4304', '414', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4301', '620', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4290', '325', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4336', '437', '3', '0', '-1', '660000', '');
INSERT INTO `mountpark_data` VALUES ('2216', '675', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('2215', '567', '11', '0', '-1', '2420000', '');
INSERT INTO `mountpark_data` VALUES ('2209', '674', '3', '0', '-1', '660000', '');
INSERT INTO `mountpark_data` VALUES ('2210', '472', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4303', '679', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4305', '438', '3', '0', '-1', '660000', '');
INSERT INTO `mountpark_data` VALUES ('4077', '403', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4082', '414', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4302', '617', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4072', '287', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4090', '697', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4097', '707', '8', '0', '-1', '1973000', '');
INSERT INTO `mountpark_data` VALUES ('4180', '621', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4094', '529', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4236', '578', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4177', '556', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4232', '717', '8', '0', '-1', '1973000', '');
INSERT INTO `mountpark_data` VALUES ('4173', '660', '3', '0', '-1', '660000', '');
INSERT INTO `mountpark_data` VALUES ('8479', '267', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8480', '342', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4231', '708', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4229', '543', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4093', '730', '8', '0', '-1', '1973000', '');
INSERT INTO `mountpark_data` VALUES ('4070', '468', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('2220', '373', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('2218', '410', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('9216', '210', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9204', '369', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9158', '585', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9156', '605', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9157', '523', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9159', '676', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9162', '558', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9209', '220', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9218', '226', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9163', '599', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9160', '375', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9164', '691', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9207', '313', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9219', '161', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9220', '205', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9208', '429', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9165', '433', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9152', '503', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9166', '560', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9222', '222', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9210', '226', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9167', '449', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9153', '603', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('6154', '598', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9168', '468', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9211', '429', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9223', '197', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9169', '414', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9213', '206', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('9225', '219', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8773', '270', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8778', '263', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8783', '326', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8788', '384', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8793', '370', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8798', '323', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8803', '160', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8804', '125', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8799', '383', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8794', '398', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8789', '180', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8779', '157', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8774', '298', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8770', '236', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8780', '341', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8805', '314', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8806', '324', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8832', '295', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8791', '295', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8786', '294', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8781', '314', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8776', '196', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8813', '298', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8817', '315', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8821', '278', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8825', '326', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8829', '298', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8833', '283', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8837', '215', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8838', '313', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8834', '146', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8830', '366', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8826', '295', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8822', '190', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8818', '299', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8814', '396', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4590', '622', '6', '0', '-1', '1457000', '');
INSERT INTO `mountpark_data` VALUES ('4586', '511', '4', '0', '-1', '907500', '');
INSERT INTO `mountpark_data` VALUES ('4605', '395', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4600', '494', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4596', '674', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4606', '454', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4584', '561', '8', '0', '-1', '1973000', '');
INSERT INTO `mountpark_data` VALUES ('4595', '190', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4624', '181', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4625', '694', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4627', '177', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4626', '697', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8753', '226', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8754', '598', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8755', '560', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('8756', '606', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4591', '525', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4593', '225', '8', '0', '-1', '1973000', '');
INSERT INTO `mountpark_data` VALUES ('4628', '675', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4620', '263', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4616', '618', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4622', '712', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4621', '378', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4614', '355', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4615', '204', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4592', '542', '3', '0', '-1', '660000', '');
INSERT INTO `mountpark_data` VALUES ('4589', '269', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4583', '752', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4598', '598', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5139', '641', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5136', '563', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4930', '421', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4607', '578', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4582', '359', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4599', '376', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4601', '363', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4629', '599', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4644', '583', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4646', '578', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4603', '580', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4597', '452', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4609', '245', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4931', '618', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5127', '617', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5133', '578', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5151', '152', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5278', '322', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5334', '251', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4932', '637', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4562', '524', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4549', '418', '7', '0', '-1', '1673000', '');
INSERT INTO `mountpark_data` VALUES ('4649', '154', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4647', '731', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4631', '579', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4630', '523', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4633', '544', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4640', '616', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4666', '231', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4588', '620', '10', '0', '-1', '2255000', '');
INSERT INTO `mountpark_data` VALUES ('4934', '709', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5333', '488', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4617', '303', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4618', '434', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5280', '619', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5279', '734', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5112', '152', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5111', '266', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5108', '433', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4941', '396', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4937', '579', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4639', '245', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4637', '542', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4690', '716', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4935', '566', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4936', '642', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5277', '570', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5324', '456', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5113', '560', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5304', '596', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5311', '598', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5326', '531', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('5331', '658', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4611', '632', '5', '0', '-1', '1100000', '');
INSERT INTO `mountpark_data` VALUES ('4613', '329', '5', '0', '-1', '1100000', '');