package com.sonarcube.eighty.repository;

import com.sonarcube.eighty.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarCustomRepository {
    Page<Car> findCarWithCustomQueryV2(
            String make,
            String model,
            Integer year,
            Boolean isElectric,
            Pageable pageable
    );
}
