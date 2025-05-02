/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3308
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3308
 Source Schema         : shebeiwenjianchuanshu

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 13/04/2025 14:39:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for record
-- ----------------------------
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `fromid` int NOT NULL,
  `toid` int NOT NULL,
  `fromName` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `toName` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `sendTime` datetime NOT NULL,
  `readTime` datetime NULL DEFAULT NULL,
  `isRead` varchar(5) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of record
-- ----------------------------
INSERT INTO `record` VALUES (104, 93200, 327290, '', '', '2025-04-13 11:34:47', '2025-04-13 11:34:47', '1', '1111111111111');
INSERT INTO `record` VALUES (105, 327290, 93200, '', '', '2025-04-13 11:34:56', '2025-04-13 11:34:56', '1', '#093#');
INSERT INTO `record` VALUES (106, 327290, 93200, '', '', '2025-04-13 11:35:03', '2025-04-13 11:35:03', '1', '发文件');
INSERT INTO `record` VALUES (107, 327290, 93200, '', '', '2025-04-13 11:35:09', '2025-04-13 11:35:09', '1', '#i327290932001744515309349.jpg#');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `qqnum` int NOT NULL,
  `realname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `sex` int NOT NULL,
  `age` int NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `signature` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `photo` int NULL DEFAULT NULL,
  `state` int NULL DEFAULT NULL,
  `registerTime` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `onhours` bigint NULL DEFAULT 0,
  `rsapublic` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `rsaprivate` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `qqnum`(`qqnum`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (9, 93200, '王贝贝', '王贝贝', 0, 11, '1', '', '2111@22.com', 1, 4, '2025-03-27 15:34:34', 1816605, 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAcXzaJRwpIqbQutGWAJKQzY6zcTjF9e/LOPxMufD2KB0dkVd7LO4tccl1Inr0hOlnIXBUH7dcYx1zhNHctwMYfmpJMSqMlg5dSqgEbEiavfwlnS0XvPQ+at9GXEldOZJ69l9KRANO0N1pY6VoFcuOit26Shhn37pqqCpikXdKyQIDAQAB', 'MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIBxfNolHCkiptC60ZYAkpDNjrNxOMX178s4/Ey58PYoHR2RV3ss7i1xyXUievSE6WchcFQft1xjHXOE0dy3Axh+akkxKoyWDl1KqARsSJq9/CWdLRe89D5q30ZcSV05knr2X0pEA07Q3WljpWgVy46K3bpKGGffumqoKmKRd0rJAgMBAAECgYBbKDAj4hKKXz1lBjGY8+8RNdIBKGnSlSkKYfctvQl2qiF/ypb7pHelaQxBRaddwrajque9zISl3PIZ+jm3G2vmAwS2QbvKCNtX7aLZKIgV6BJKkH7PK3uhq0MPNdFF+cmGq0sOlyE+o4hE0ijtRmiqvmDv0fN5MHd1MnwsuXugAQJBAMzTq7ich5blAuGNx946+QZVTsGw0NrO8WbHm0OHYKhjJkvVrdITNJbh9vHJieJnnZnogN47Am8iNpuykpJxF8ECQQCgiHlUWXgR47DW26yYykWaMYZDnNy14OQuxvHZyjMbAbYgLjwuCsD9+RyGkO3hMdOqWtv4ZU3+SOpI9b9SMrUJAkBdd5H68mLlNqfz6VD1Vg/hFfiUZm1IC/sYnal6TUKMVIJbRKzHGkB7Qa4Ac6laCVJfsztrLZ5UvFYTAo/kB3uBAkA2qcIGwuBr1whkYTEIZfgFZU++vi85j3ajpwK7kmqMi6ykkJnVY7wLv0PDXp5hzEGzBHVaw8QnDlUYuvWG29VhAkBuPX053Y3wPfmbzMVbuRwwCtXbqhup9wWlvlX9II4fddXjBpvxBaLaWtW+Uu0EUbTYza/WfPOnybDur4gr+P7d');
INSERT INTO `user` VALUES (10, 327290, '11', '李珊珊', 0, 11, '1', '', '11@22.com', 1, 4, '2025-03-27 17:15:14', 336656, 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCS2U839XnxxrAdDgHswsPuXmakYJuPylQMc88uvYifE9lcY8QCV3UF4hl7/TOC/cJVW8vIZd9IrFd+dhflmzL0GGgG2cSKvmHo4f2B0UYUxvo6iWoZUV4L3qm3k2Tp9rc4ZSZtzhy/WnBeA2e4a9nC0aWccas/mw+vbRt5NeFJ0wIDAQAB', 'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJLZTzf1efHGsB0OAezCw+5eZqRgm4/KVAxzzy69iJ8T2VxjxAJXdQXiGXv9M4L9wlVby8hl30isV352F+WbMvQYaAbZxIq+Yejh/YHRRhTG+jqJahlRXgveqbeTZOn2tzhlJm3OHL9acF4DZ7hr2cLRpZxxqz+bD69tG3k14UnTAgMBAAECgYAn2dM2s2YxZ6L5b4i0MLKtKG8gYps0rUqbyn3KCGNgECnneaeCnytBuncB/9ePz7+CHJQzwPmUJjjkN7YKeFCeJZCRwSypxdg/xlXoK+vu8UlxFOiqR0KYRgq8CYqpxONWH7nQXXcUWLkafJzu3R70ed82+yaGhjQ1LXp5VQakoQJBANZvhOo5EKxFq4TkjWmUM/eY5ybCgdsvcxiqHuJl1d8sFTZt8+i2WxpdFGrj7TKr4bQXTjDHvAxkjUfG+njf2dECQQCvUBO1gGiTFOPHyqD6oPs8DO1w4TVwRJGnT1pydUde0mNoh0aQAeii2x5Uvbkze1Ng9CkrO42faanNAzbKBK5jAkEApl0c1I7zVYQz3sVmxPubGcQBybtr/b4hYIhTr7fsJtnPl0vUyL35jkmalpdApmhdklfmbMf5J/A2//Hqzy50kQJASlrYQR7ZO6icg1x5slMcu3dwLXQXSiqK7D9HyUvcnWb2KptVXjaxCIGO97oQFrmzv2dPHrr/ciFKl11Jm50oqQJACj3cYpw3MIgbJxxn+9a9vZTzxPJA41MQahgNiC4W9tMaJCGN2Ne8Q1hpb0WvVacaVuGDGIdAI+Jjg9wacEzhBQ==');

-- ----------------------------
-- Table structure for user_friends
-- ----------------------------
DROP TABLE IF EXISTS `user_friends`;
CREATE TABLE `user_friends`  (
  `selfid` int NOT NULL DEFAULT 0,
  `friendid` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`selfid`, `friendid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of user_friends
-- ----------------------------
INSERT INTO `user_friends` VALUES (93200, 327290);
INSERT INTO `user_friends` VALUES (327290, 93200);

SET FOREIGN_KEY_CHECKS = 1;
