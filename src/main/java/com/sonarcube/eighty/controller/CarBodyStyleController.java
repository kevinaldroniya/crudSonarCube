package com.sonarcube.eighty.controller;

import com.sonarcube.eighty.dto.CarBodyResponse;
import com.sonarcube.eighty.service.CarBodyStyleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car-body-style")
public class CarBodyStyleController {

    private final CarBodyStyleService carBodyStyleService;

    public CarBodyStyleController(CarBodyStyleService carBodyStyleService){
        this.carBodyStyleService = carBodyStyleService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<CarBodyResponse>> getAllCarBodyStyles(){
        return ResponseEntity.ok(carBodyStyleService.getAllCarBodyStyles());
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarBodyResponse> getCarBodyStyleById(@PathVariable("id") Long id){
        return ResponseEntity.ok(carBodyStyleService.getCarBodyStyleById(id));
    }

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarBodyResponse> createCarBodyStyle(@RequestBody String name){
        return ResponseEntity.ok(carBodyStyleService.createCarBodyStyle(name));
    }

    @PutMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarBodyResponse> updateCarBodyStyle(@PathVariable("id") Long id, @RequestBody String name){
        return ResponseEntity.ok(carBodyStyleService.updateCarBodyStyle(id, name));
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarBodyResponse> deleteCarBodyStyle(@PathVariable("id") Long id){
        return ResponseEntity.ok(carBodyStyleService.deleteCarBodyStyle(id));
    }

}
