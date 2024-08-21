package com.sonarcube.eighty.controller;

import com.sonarcube.eighty.dto.CarDto;
import com.sonarcube.eighty.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

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

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarDto> saveCar(@Valid @RequestBody CarDto carDto){
        CarDto savedCar = carService.saveCar(carDto);
        return new ResponseEntity<>(savedCar, HttpStatus.CREATED);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/{id}"
    )
    public ResponseEntity<CarDto> updateCar(@PathVariable("id") Long id, @Valid @RequestBody CarDto carDto){
        CarDto updateCar = carService.updateCar(id, carDto);
        return new ResponseEntity<>(updateCar, HttpStatus.OK);
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteCar(@PathVariable("id") Long id){
        String deleteCar = carService.deleteCar(id);
        return new ResponseEntity<>(deleteCar, HttpStatus.OK);
    }
}
