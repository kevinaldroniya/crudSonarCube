package com.sonarcube.eighty.service;

import com.sonarcube.eighty.dto.CarDtoRequest;
import com.sonarcube.eighty.dto.CarDtoResponse;
import com.sonarcube.eighty.dto.CarStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CarService {
    List<CarDtoResponse> getAllCars();
    CarDtoResponse getCarById(Long id);
    CarDtoResponse saveCar(CarDtoRequest carDtoRequest);
    CarDtoResponse updateCar(Long id, CarDtoRequest carDtoRequest);
    CarDtoResponse updateCarStatus(Long id, CarStatusRequest carStatusRequest);
    String deleteCar(Long id);
    Page<CarDtoResponse> findBySomeFields(String make, String model, int year, boolean isElectric, int page, int size, String sortBy, String sortDirection);
    Page<CarDtoResponse> findCarByCustomQuery(String make, String model, int year, boolean isElectric, int page, int size, String sortBy, String sortDirection);
    Page<CarDtoResponse> findCarByCustomQueryV2(String make, String model, int year, boolean isElectric, int page, int size, String sortBy, String sortDirection);

}
