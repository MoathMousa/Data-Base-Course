-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: project
-- ------------------------------------------------------
-- Server version	5.7.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `forum`
--

DROP TABLE IF EXISTS `forum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forum` (
  `id` int(11) unsigned NOT NULL DEFAULT '0',
  `name` char(35) NOT NULL DEFAULT 'forum',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum`
--

LOCK TABLES `forum` WRITE;
/*!40000 ALTER TABLE `forum` DISABLE KEYS */;
INSERT INTO `forum` VALUES (0,'Announcements'),(1,'Feedback and Criticism'),(2,'Game'),(3,'General Discussion');
/*!40000 ALTER TABLE `forum` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_account`
--

DROP TABLE IF EXISTS `game_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_account` (
  `id` int(10) unsigned NOT NULL DEFAULT '1',
  `username` char(15) NOT NULL DEFAULT 'username',
  `password` char(15) NOT NULL DEFAULT 'password',
  `email` char(25) NOT NULL DEFAULT 'email@email.com',
  `level` int(10) unsigned NOT NULL DEFAULT '1',
  `day` int(10) unsigned NOT NULL DEFAULT '1',
  `month` int(10) unsigned NOT NULL DEFAULT '1',
  `year` int(10) unsigned NOT NULL DEFAULT '1970',
  `sel_car_no` int(10) unsigned NOT NULL DEFAULT '0',
  `sel_color_no` int(10) unsigned NOT NULL DEFAULT '0',
  `server_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `server_id_idx` (`server_id`),
  CONSTRAINT `server_id` FOREIGN KEY (`server_id`) REFERENCES `server` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_account`
--

LOCK TABLES `game_account` WRITE;
/*!40000 ALTER TABLE `game_account` DISABLE KEYS */;
INSERT INTO `game_account` VALUES (0,'Drifter','youthinkyouwon','mustafa@gmail.com',5,4,10,1998,1,2,NULL),(1,'I_King_Yes','123456789','you96@gmail.com',3,18,7,1996,1,0,NULL),(2,'HebronKing','iamfromhebron','picollo@hotmail.com',7,30,9,1996,6,4,NULL),(3,'L33T','dontcomeme','heyoheyo@yahoo.com',2,4,10,1998,1,2,NULL),(4,'I_HATE_YOU','ilovesinging','peter-park@gmail.com',8,4,10,1998,7,4,NULL),(5,'LOL_TROLL','BLABLA','master_97@gmail.com',7,4,10,1997,1,2,NULL),(6,'please','12345','please@gmail.com',1,1,1,1870,0,0,NULL),(7,'Hope','hopel','hope@school.com',1,8,9,2009,0,0,NULL);
/*!40000 ALTER TABLE `game_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership`
--

DROP TABLE IF EXISTS `membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `membership` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `time` char(5) NOT NULL DEFAULT 'week',
  `since` date DEFAULT NULL,
  PRIMARY KEY (`id`,`time`),
  CONSTRAINT `id` FOREIGN KEY (`id`) REFERENCES `player` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership`
--

LOCK TABLES `membership` WRITE;
/*!40000 ALTER TABLE `membership` DISABLE KEYS */;
INSERT INTO `membership` VALUES (0,'week',NULL),(4,'week',NULL);
/*!40000 ALTER TABLE `membership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `moderator`
--

DROP TABLE IF EXISTS `moderator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `moderator` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `mod_number` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `mod_number_UNIQUE` (`mod_number`),
  CONSTRAINT `gmid` FOREIGN KEY (`id`) REFERENCES `game_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `moderator`
--

LOCK TABLES `moderator` WRITE;
/*!40000 ALTER TABLE `moderator` DISABLE KEYS */;
INSERT INTO `moderator` VALUES (1,0),(2,1);
/*!40000 ALTER TABLE `moderator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `player_number` int(10) unsigned NOT NULL DEFAULT '0',
  `banned_by` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `player_number_UNIQUE` (`player_number`),
  KEY `banned_by_idx` (`banned_by`),
  CONSTRAINT `banned_by` FOREIGN KEY (`banned_by`) REFERENCES `moderator` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `pid` FOREIGN KEY (`id`) REFERENCES `game_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
INSERT INTO `player` VALUES (0,0,NULL),(3,1,NULL),(4,2,NULL),(5,3,2),(6,4,1),(7,5,NULL);
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `contents` char(200) DEFAULT NULL,
  `game_acc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `thread_id` int(10) unsigned NOT NULL DEFAULT '0',
  `head_post` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  KEY `game_acc_id_idx` (`game_acc_id`),
  KEY `thread_id_idx` (`thread_id`),
  CONSTRAINT `game_acc_id` FOREIGN KEY (`game_acc_id`) REFERENCES `game_account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `thread_id` FOREIGN KEY (`thread_id`) REFERENCES `thread` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (1,'lashoof',2,13,'Y');
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `server`
--

DROP TABLE IF EXISTS `server`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` char(45) NOT NULL DEFAULT 'server',
  `capacity` int(10) unsigned NOT NULL DEFAULT '50',
  `server_man_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `server_man_id_idx` (`server_man_id`),
  CONSTRAINT `server_man_id` FOREIGN KEY (`server_man_id`) REFERENCES `server_manager` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `server`
--

LOCK TABLES `server` WRITE;
/*!40000 ALTER TABLE `server` DISABLE KEYS */;
INSERT INTO `server` VALUES (0,'Shabab Birzeit',30,1),(1,'Computer Science Power',27,1),(2,'Engineering is cool',50,2),(3,'Holiday Projects',77,0),(4,'What is up?',11,3);
/*!40000 ALTER TABLE `server` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `server_manager`
--

DROP TABLE IF EXISTS `server_manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server_manager` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `username` char(15) NOT NULL DEFAULT 'username',
  `password` char(15) NOT NULL DEFAULT 'password',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `server_manager`
--

LOCK TABLES `server_manager` WRITE;
/*!40000 ALTER TABLE `server_manager` DISABLE KEYS */;
INSERT INTO `server_manager` VALUES (0,'Luai_Iwidat','luaiiwidat'),(1,'Moath_Mou','moathmou'),(2,'Yousef_Ani','yousefani'),(3,'Mohammad','mohammad');
/*!40000 ALTER TABLE `server_manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `thread`
--

DROP TABLE IF EXISTS `thread`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thread` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` char(35) NOT NULL,
  `forum_id` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `forum_id_idx` (`forum_id`),
  CONSTRAINT `forum_id` FOREIGN KEY (`forum_id`) REFERENCES `forum` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thread`
--

LOCK TABLES `thread` WRITE;
/*!40000 ALTER TABLE `thread` DISABLE KEYS */;
INSERT INTO `thread` VALUES (0,'New Cars Added!',0),(1,'Database Project Finished',0),(2,'COMP333 Has merged the two courses',0),(3,'Bug in Track Barcelona',1),(5,'How do I apply the brakes?',2),(6,'I want a Gallardo!!!',1),(7,'What\'s your favorite car?',2),(8,'I WON MY FIRST RACE',2),(9,'Damaged My Car',2),(10,'Anybody studying Computer Science',3),(11,'Which is better, CS or CE?',3),(12,'I wish I could travel to Italy',3),(13,'Hat',1);
/*!40000 ALTER TABLE `thread` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-01-07 12:11:34
