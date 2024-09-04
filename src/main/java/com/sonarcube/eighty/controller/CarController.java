package com.sonarcube.eighty.controller;

import com.sonarcube.eighty.dto.CarDtoRequest;
import com.sonarcube.eighty.dto.CarDtoResponse;
import com.sonarcube.eighty.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @GetMapping(
            path = "/findBySomeFields",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<CarDtoResponse>> findBySomeFields(
            @RequestParam(defaultValue = "") String make,
            @RequestParam(defaultValue = "") String model,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "1") boolean isElectric,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        Page<CarDtoResponse> responses = carService.findBySomeFields(make, model, year, isElectric, page, size, sortBy, sortDirection);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping(
            path = "/findByCustomQuery",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<CarDtoResponse>> findByCustomQuery(
            @RequestParam(defaultValue = "") String make,
            @RequestParam(defaultValue = "") String model,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "1") boolean isElectric,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        Page<CarDtoResponse> responses = carService.findCarByCustomQuery(make, model, year, isElectric, page, size, sortBy, sortDirection);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping(
            path = "/findByCustomQueryV2",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<CarDtoResponse>> findByCustomQueryV2(
            @RequestParam(defaultValue = "") String make,
            @RequestParam(defaultValue = "") String model,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "1") boolean isElectric,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        Page<CarDtoResponse> responses = carService.findCarByCustomQueryV2(make, model, year, isElectric, page, size, sortBy, sortDirection);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
