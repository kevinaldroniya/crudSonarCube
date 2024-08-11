package com.sonarcube.eighty.controller;

import com.sonarcube.eighty.dto.CarDto;
import com.sonarcube.eighty.model.Car;
import com.sonarcube.eighty.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<CarDto> allCars = carService.getAllCars();
        return ResponseEntity.ok(allCars);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarDto> getCarById(@PathVariable("id") Long id){
        CarDto carById = carService.getCarById(id);
        return ResponseEntity.ok(carById);
    }
}
