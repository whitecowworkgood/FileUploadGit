-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.3.38-MariaDB-0ubuntu0.20.04.1 - Ubuntu 20.04
-- 서버 OS:                        debian-linux-gnu
-- HeidiSQL 버전:                  12.5.0.6677
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- file_store 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `file_store` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `file_store`;

-- 테이블 file_store.authority 구조 내보내기
CREATE TABLE IF NOT EXISTS `authority` (
  `authority_name` varchar(50) NOT NULL,
  PRIMARY KEY (`authority_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 file_store.file_entity 구조 내보내기
CREATE TABLE IF NOT EXISTS `file_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuidfile_name` varchar(1020) NOT NULL,
  `file_ole_path` varchar(1020) NOT NULL,
  `file_save_path` varchar(1020) NOT NULL,
  `file_size` bigint(20) NOT NULL,
  `file_type` varchar(1020) NOT NULL,
  `origin_file_name` varchar(1020) NOT NULL,
  `count_num` bigint(10) NOT NULL DEFAULT 5,
  `user_name` varchar(50) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 0,
  `comment` mediumtext NOT NULL DEFAULT 'TEST',
  `time_stamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_encrypt` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 file_store.ole_entry 구조 내보내기
CREATE TABLE IF NOT EXISTS `ole_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuidfile_name` varchar(1020) NOT NULL,
  `original_file_name` varchar(1020) NOT NULL,
  `super_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 file_store.refresh_token 구조 내보내기
CREATE TABLE IF NOT EXISTS `refresh_token` (
  `rt_key` varchar(255) NOT NULL,
  `rt_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rt_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 file_store.rsa_keys 구조 내보내기
CREATE TABLE IF NOT EXISTS `rsa_keys` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `public_key` text NOT NULL,
  `private_key` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 file_store.users 구조 내보내기
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activated` bit(1) DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 file_store.user_authority 구조 내보내기
CREATE TABLE IF NOT EXISTS `user_authority` (
  `user_id` bigint(20) NOT NULL,
  `authority_name` varchar(50) NOT NULL,
  PRIMARY KEY (`user_id`,`authority_name`),
  KEY `FK6ktglpl5mjosa283rvken2py5` (`authority_name`),
  CONSTRAINT `FK6ktglpl5mjosa283rvken2py5` FOREIGN KEY (`authority_name`) REFERENCES `authority` (`authority_name`),
  CONSTRAINT `FKhi46vu7680y1hwvmnnuh4cybx` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
