CREATE DATABASE  IF NOT EXISTS `fisioterapia` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `fisioterapia`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: fisioterapia
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `aluno_horarios`
--

DROP TABLE IF EXISTS `aluno_horarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aluno_horarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_aluno` int NOT NULL,
  `id_horario` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_aluno` (`id_aluno`),
  KEY `id_horario` (`id_horario`),
  CONSTRAINT `aluno_horarios_ibfk_1` FOREIGN KEY (`id_aluno`) REFERENCES `alunos` (`id`),
  CONSTRAINT `aluno_horarios_ibfk_2` FOREIGN KEY (`id_horario`) REFERENCES `horarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `aluno_turno`
--

DROP TABLE IF EXISTS `aluno_turno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aluno_turno` (
  `id` int NOT NULL AUTO_INCREMENT,
  `aluno_id` int NOT NULL,
  `turno_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `aluno_id` (`aluno_id`),
  KEY `turno_id` (`turno_id`),
  CONSTRAINT `aluno_turno_ibfk_1` FOREIGN KEY (`aluno_id`) REFERENCES `alunos` (`id`),
  CONSTRAINT `aluno_turno_ibfk_2` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alunos`
--

DROP TABLE IF EXISTS `alunos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alunos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) DEFAULT NULL,
  `senha` varchar(300) DEFAULT NULL,
  `cpf` varchar(30) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `tipo` enum('estagio','curso') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alunos_especialidades`
--

DROP TABLE IF EXISTS `alunos_especialidades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alunos_especialidades` (
  `id` int NOT NULL AUTO_INCREMENT,
  `idAluno` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idAluno` (`idAluno`),
  CONSTRAINT `alunos_especialidades_ibfk_1` FOREIGN KEY (`idAluno`) REFERENCES `alunos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alunos_professor`
--

DROP TABLE IF EXISTS `alunos_professor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alunos_professor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_aluno` int NOT NULL,
  `id_professor` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_aluno` (`id_aluno`),
  KEY `id_professor` (`id_professor`),
  CONSTRAINT `alunos_professor_ibfk_1` FOREIGN KEY (`id_aluno`) REFERENCES `alunos` (`id`),
  CONSTRAINT `alunos_professor_ibfk_2` FOREIGN KEY (`id_professor`) REFERENCES `professores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consultas`
--

DROP TABLE IF EXISTS `consultas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `aluno_id` int NOT NULL,
  `paciente` int NOT NULL,
  `motivo` varchar(500) NOT NULL,
  `data_agendada` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_consulta` datetime NOT NULL,
  `status` enum('cancelada','agendada','concluida','a validar','em aberto') DEFAULT 'agendada',
  PRIMARY KEY (`id`),
  KEY `fk_agendamento_paciente` (`paciente`),
  KEY `consultas_ibfk_1` (`aluno_id`),
  CONSTRAINT `consultas_ibfk_1` FOREIGN KEY (`aluno_id`) REFERENCES `alunos` (`id`),
  CONSTRAINT `fk_agendamento_paciente` FOREIGN KEY (`paciente`) REFERENCES `pacientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `especialidades`
--

DROP TABLE IF EXISTS `especialidades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `especialidades` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nome` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `horarios`
--

DROP TABLE IF EXISTS `horarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `horario` time NOT NULL,
  `turno_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `turno_id` (`turno_id`),
  CONSTRAINT `horarios_ibfk_1` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pacientes`
--

DROP TABLE IF EXISTS `pacientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pacientes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(350) DEFAULT NULL,
  `cpf` varchar(20) DEFAULT NULL,
  `telefone` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `professores`
--

DROP TABLE IF EXISTS `professores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) DEFAULT NULL,
  `senha` varchar(300) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `tipo` enum('comum','coordenador') DEFAULT 'comum',
  `ativo` tinyint(1) DEFAULT '1',
  `data_cadastro` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `professores_especializacao`
--

DROP TABLE IF EXISTS `professores_especializacao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professores_especializacao` (
  `professor_id` int NOT NULL,
  `especialidade_id` int NOT NULL,
  KEY `professor_id` (`professor_id`),
  KEY `especialidade_id` (`especialidade_id`),
  CONSTRAINT `professores_especializacao_ibfk_1` FOREIGN KEY (`professor_id`) REFERENCES `professores` (`id`) ON DELETE CASCADE,
  CONSTRAINT `professores_especializacao_ibfk_2` FOREIGN KEY (`especialidade_id`) REFERENCES `especialidades` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `relatorios`
--

DROP TABLE IF EXISTS `relatorios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `relatorios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `consulta_id` int NOT NULL,
  `descricao` text NOT NULL,
  `data_criacao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `consulta_id` (`consulta_id`),
  CONSTRAINT `relatorios_ibfk_1` FOREIGN KEY (`consulta_id`) REFERENCES `consultas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `turnos`
--

DROP TABLE IF EXISTS `turnos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turnos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-04 19:22:53
