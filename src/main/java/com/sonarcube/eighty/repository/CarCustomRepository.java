package com.sonarcube.eighty.repository;

import com.sonarcube.eighty.dto.CarFilterParams;
import com.sonarcube.eighty.model.Car;
import com.sonarcube.eighty.model.CarMake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarCustomRepository {
    Page<Car> findCarWithCustomQueryV2(
            CarMake carMake,
            CarFilterParams carFilterParams,
            Pageable pageable
    );
}
