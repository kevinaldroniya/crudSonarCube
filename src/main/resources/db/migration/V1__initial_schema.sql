-- items.car definition

CREATE TABLE `car` (
  `is_electric` bit(1) DEFAULT NULL,
  `previous_owner` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  `year` int DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dimensions` varchar(255) DEFAULT NULL,
  `engine_specs` varchar(255) DEFAULT NULL,
  `features` varchar(255) DEFAULT NULL,
  `maintenance_dates` varchar(255) DEFAULT NULL,
  `make` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `warranty` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;