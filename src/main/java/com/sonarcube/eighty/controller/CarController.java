package com.sonarcube.eighty.controller;

import com.sonarcube.eighty.dto.CarDtoRequest;
import com.sonarcube.eighty.dto.CarDtoResponse;
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
    public ResponseEntity<List<CarDtoResponse>> getAllCars() {
        List<CarDtoResponse> allCars = carService.getAllCars();
        return ResponseEntity.ok(allCars);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarDtoResponse> getCarById(@PathVariable("id") Long id){
        CarDtoResponse carById = carService.getCarById(id);
        return ResponseEntity.ok(carById);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarDtoResponse> saveCar(@Valid @RequestBody CarDtoRequest carDtoRequest){
        CarDtoResponse savedCar = carService.saveCar(carDtoRequest);
        return new ResponseEntity<>(savedCar, HttpStatus.CREATED);
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/{id}"
    )
    public ResponseEntity<CarDtoResponse> updateCar(@PathVariable("id") Long id, @Valid @RequestBody CarDtoRequest carDtoRequest){
        CarDtoResponse updateCar = carService.updateCar(id, carDtoRequest);
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
