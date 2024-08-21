package com.sonarcube.eighty.service;

import com.sonarcube.eighty.dto.CarDto;
import com.sonarcube.eighty.model.Car;

import java.util.List;

public interface CarService {
    List<CarDto> getAllCars();
    CarDto getCarById(Long id);
    CarDto saveCar(CarDto carDto);
    CarDto updateCar(Long id, CarDto carDto);
    String deleteCar(Long id);
}
