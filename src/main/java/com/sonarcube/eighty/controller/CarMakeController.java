package com.sonarcube.eighty.controller;

import com.sonarcube.eighty.dto.CarMakeRequest;
import com.sonarcube.eighty.dto.CarMakeResponse;
import com.sonarcube.eighty.service.CarMakeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/makes")
public class CarMakeController {

    private final CarMakeService carMakeService;

    CarMakeController(CarMakeService carMakeService){
        this.carMakeService = carMakeService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<CarMakeResponse>> getAllCarModels(){
        List<CarMakeResponse> responses = carMakeService.getAllCarMakes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarMakeResponse> getCarModel(@PathVariable("id") Long id){
        CarMakeResponse response = carMakeService.getCarMakes(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarMakeResponse> saveCarModel(@RequestBody CarMakeRequest request){
        CarMakeResponse response = carMakeService.saveCarMake(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarMakeResponse> updateCarModel(@PathVariable("id") Long id, @RequestBody CarMakeRequest request){
        CarMakeResponse response = carMakeService.updateCarMake(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteCarModel(@PathVariable("id") Long id){
        String response = carMakeService.deleteCarMake(id);
        return ResponseEntity.ok(response);
    }
}
