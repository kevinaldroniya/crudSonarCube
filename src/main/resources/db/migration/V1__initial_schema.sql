-- items.car definition

CREATE TABLE `car` (
  `is_electric` bit(1) NOT NULL,
  `previous_owner` int NOT NULL,
  `price` double NOT NULL,
  `year` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dimensions` varchar(255) NOT NULL,
  `engine_specs` varchar(255) NOT NULL,
  `features` varchar(255) NOT NULL,
  `maintenance_dates` varchar(255) NOT NULL,
  `make_id` bigint NOT NULL,
  `model` varchar(255) NOT NULL,
  `warranty` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`make_id`) REFERENCES `car_make`(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- crudSonar.car_make definition

CREATE TABLE `car_make` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `created_at` bigint NOT NULL DEFAULT (unix_timestamp(now())),
  `updated_at` bigint DEFAULT NULL,
  `deleted_at` bigint DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;