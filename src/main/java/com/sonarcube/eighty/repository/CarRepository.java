package com.sonarcube.eighty.repository;

import com.sonarcube.eighty.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
