package com.sonarcube.eighty.repository;

import com.sonarcube.eighty.model.CarBodyStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarBodyStyleRepository extends JpaRepository<CarBodyStyle, Long> {
    Optional<CarBodyStyle> findByName(String name);
}
