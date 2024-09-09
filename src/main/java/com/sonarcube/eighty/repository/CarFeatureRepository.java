package com.sonarcube.eighty.repository;

import com.sonarcube.eighty.model.CarFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarFeatureRepository extends JpaRepository<CarFeature, Long> {
    Optional<CarFeature> findByFeature(String feature);
}
