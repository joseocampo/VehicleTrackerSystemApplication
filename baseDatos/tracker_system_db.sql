-- phpMyAdmin SQL Dump
-- version 4.6.6
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 06-11-2018 a las 07:54:27
-- Versión del servidor: 5.7.17-log
-- Versión de PHP: 5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `tracker_system_db`
--

DELIMITER $$
--
-- Procedimientos
--
CREATE  PROCEDURE `administrator` (IN `usuario` VARCHAR(30))  NO SQL
update t_users set t_users.role = 0 where t_users.user = usuario$$

CREATE  PROCEDURE `drop_user` (IN `user` VARCHAR(40))  BEGIN

	SET @query = CONCAT('DROP USER ', user);
	PREPARE stmt FROM @query;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;
	FLUSH PRIVILEGES;
END$$

CREATE  PROCEDURE `vehicle_user` (IN `usuario` VARCHAR(30))  NO SQL
update t_users set t_users.role = 1 where t_users.user = usuario$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `t_binnacle`
--

CREATE TABLE `t_binnacle` (
  `User` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `t_failures`
--

CREATE TABLE `t_failures` (
  `PK_Date` datetime NOT NULL,
  `Cost` float NOT NULL,
  `Details` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `FK_User` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `FK_Vehicle` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `t_incidents`
--

CREATE TABLE `t_incidents` (
  `PK_Date` datetime NOT NULL,
  `Details` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `Cost` float NOT NULL,
  `FK_User` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `FK_Vehicle` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `t_municipality_details`
--

CREATE TABLE `t_municipality_details` (
  `Name` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Logo` mediumblob NOT NULL,
  `Address` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Mission` text CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Vission` text CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Phone_number` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `t_routes`
--

CREATE TABLE `t_routes` (
  `PK_Date` date NOT NULL,
  `Travel` blob NOT NULL,
  `Details` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `FK_User` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `FK_Vehicle` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `t_users`
--

CREATE TABLE `t_users` (
  `user` varchar(20) NOT NULL,
  `password` varchar(30) NOT NULL,
  `role` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `t_users`
--

INSERT INTO `t_users` (`user`, `password`, `role`) VALUES
('504260844', '504260844', 1),
('55555', '55555', 1),
('root', 'slapshock10', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `t_user_information`
--

CREATE TABLE `t_user_information` (
  `PK_Id` varchar(12) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Name` varchar(20) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Surname` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `Second_surname` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `Area` varchar(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Phone_number` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `Email` varchar(50) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL,
  `Address` varchar(45) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `vehicle` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `t_user_information`
--

INSERT INTO `t_user_information` (`PK_Id`, `Name`, `Surname`, `Second_surname`, `Area`, `Phone_number`, `Email`, `Address`, `vehicle`) VALUES
('207330432', 'Didier', 'Ocampos', 'Martinez', 'Policía Municipal', '555555', 'Didiere@gmail.com', 'San Pablo, Heredia', NULL),
('207780771', 'Anthony', 'Ramírez', 'López', 'Informática', '85425053', 'arleyy1004@gmail.com', 'San Pablo, Heredia', NULL),
('504260844', 'Lizzette', 'Badilla', 'Cortéz', 'Centro de Cultura', '71475303', 'Lizeete@gmail.com', 'San Pablo, Heredia', NULL),
('55555', 'Juan', 'Pere', 'Mora', 'Policía Municipal', '11111', 'juan@gmail.com', 'San Pablo, Heredia', NULL),
('anthony', 'anthony', 'Ramírez', 'López', 'Informática', '85425053', 'arleyy1004@gmail.com', 'San Pablo, Heredia', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `t_vehicle`
--

CREATE TABLE `t_vehicle` (
  `PK_License_plate` varchar(8) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Model` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Brand` varchar(15) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL,
  `Year_purchase` varchar(4) CHARACTER SET latin1 COLLATE latin1_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Volcado de datos para la tabla `t_vehicle`
--

INSERT INTO `t_vehicle` (`PK_License_plate`, `Model`, `Brand`, `Year_purchase`) VALUES
('22', 'Rio', 'Nissan', '2003'),
('5555', 'Rop', 'Toyota', '1997'),
('55555', 'Rop', 'Chevrolet', '1987'),
('555556', 'Rop', 'Toyota', '1990'),
('asda', 'sss', 'Toyota', '1997'),
('k2187', 'Rop', 'Nissan', '1997'),
('krm', 'Rio', 'Nissan', '1999');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `t_failures`
--
ALTER TABLE `t_failures`
  ADD PRIMARY KEY (`PK_Date`),
  ADD KEY `User` (`FK_User`),
  ADD KEY `Vehicle` (`FK_Vehicle`);

--
-- Indices de la tabla `t_incidents`
--
ALTER TABLE `t_incidents`
  ADD PRIMARY KEY (`PK_Date`),
  ADD KEY `User` (`FK_User`),
  ADD KEY `Vehicle` (`FK_Vehicle`);

--
-- Indices de la tabla `t_routes`
--
ALTER TABLE `t_routes`
  ADD PRIMARY KEY (`PK_Date`),
  ADD KEY `User` (`FK_User`),
  ADD KEY `Vehicle` (`FK_Vehicle`);

--
-- Indices de la tabla `t_users`
--
ALTER TABLE `t_users`
  ADD PRIMARY KEY (`user`);

--
-- Indices de la tabla `t_user_information`
--
ALTER TABLE `t_user_information`
  ADD PRIMARY KEY (`PK_Id`),
  ADD KEY `FKVehicleU` (`vehicle`);

--
-- Indices de la tabla `t_vehicle`
--
ALTER TABLE `t_vehicle`
  ADD PRIMARY KEY (`PK_License_plate`);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `t_failures`
--
ALTER TABLE `t_failures`
  ADD CONSTRAINT `fk_failures_user` FOREIGN KEY (`FK_User`) REFERENCES `t_user_information` (`PK_Id`),
  ADD CONSTRAINT `fk_failures_vehicle` FOREIGN KEY (`FK_Vehicle`) REFERENCES `t_vehicle` (`PK_License_plate`);

--
-- Filtros para la tabla `t_incidents`
--
ALTER TABLE `t_incidents`
  ADD CONSTRAINT `fk_incidents_user` FOREIGN KEY (`FK_User`) REFERENCES `t_user_information` (`PK_Id`),
  ADD CONSTRAINT `fk_incidents_vehicle` FOREIGN KEY (`FK_Vehicle`) REFERENCES `t_vehicle` (`PK_License_plate`);

--
-- Filtros para la tabla `t_routes`
--
ALTER TABLE `t_routes`
  ADD CONSTRAINT `fk_routes_user` FOREIGN KEY (`FK_User`) REFERENCES `t_user_information` (`PK_Id`),
  ADD CONSTRAINT `fk_routes_vehicle` FOREIGN KEY (`FK_Vehicle`) REFERENCES `t_vehicle` (`PK_License_plate`);

--
-- Filtros para la tabla `t_user_information`
--
ALTER TABLE `t_user_information`
  ADD CONSTRAINT `fk_user_vehicle` FOREIGN KEY (`vehicle`) REFERENCES `t_vehicle` (`PK_License_plate`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
