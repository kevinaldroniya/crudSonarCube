package com.sonarcube.eighty.service;

import com.sonarcube.eighty.dto.CarDto;
import com.sonarcube.eighty.model.Car;

import java.util.List;

public interface CarService {
    List<CarDto> getAllCars();
    CarDto getCarById(Long id);
    CarDto saveCar(CarDto carDto);
    Car updateCar(Long id, Car car);
    boolean deleteCar(Long id);
}
