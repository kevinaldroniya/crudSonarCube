package com.sonarcube.eighty.repository;

import com.sonarcube.eighty.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarRepository extends JpaRepository<Car, Long>, CarCustomRepository {
    Page<Car> findBySomeOfFields(String make, String model, int year, boolean isElectric, Pageable pageable);

    @Query(
            value = "SELECT * FROM car WHERE" +
                    "(:make IS NULL OR make LIKE %:make%) AND" +
                    "(:model IS NULL OR model LIKE %:model%) AND" +
                    "(:year IS NULL OR year = :year) AND" +
                    "(:isElectric IS NULL OR is_electric = :isElectric)",
            countQuery = "SELECT COUNT(*) FROM car WHERE" +
                    "(:make IS NULL OR make LIKE %:make%) AND" +
                    "(:model IS NULL OR model LIKE %:model%) AND" +
                    "(:year IS NULL OR year = :year) AND" +
                    "(:isElectric IS NULL OR is_electric = :isElectric)",
            nativeQuery = true
    )
    Page<Car> findCarByCustomQuery(@Param("make") String make,
                                   @Param("model") String model,
                                   @Param("year") int year,
                                   @Param("isElectric") Boolean isElectric,
                                   Pageable pageable);
}
