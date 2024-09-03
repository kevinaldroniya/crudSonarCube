package com.sonarcube.eighty.service;

import com.sonarcube.eighty.dto.CarDtoRequest;
import com.sonarcube.eighty.dto.CarDtoResponse;

import java.util.List;

public interface CarService {
    List<CarDtoResponse> getAllCars();
    CarDtoResponse getCarById(Long id);
    CarDtoResponse saveCar(CarDtoRequest carDtoRequest);
    CarDtoResponse updateCar(Long id, CarDtoRequest carDtoRequest);
    String deleteCar(Long id);
}
