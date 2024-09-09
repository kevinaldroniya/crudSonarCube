package com.sonarcube.eighty.controller;

import com.sonarcube.eighty.dto.CarFeatureResponse;
import com.sonarcube.eighty.model.CarFeature;
import com.sonarcube.eighty.service.CarFeatureService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("car-feature")
public class CarFeatureController {

    private final CarFeatureService carFeatureService;

    public CarFeatureController(CarFeatureService carFeatureService) {
        this.carFeatureService = carFeatureService;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<CarFeatureResponse>> getAllCarFeatures() {
        return ResponseEntity.ok(carFeatureService.getAllCarFeatures());
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/{id}"
    )
    public ResponseEntity<CarFeatureResponse> getCarFeatureById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(carFeatureService.getCarFeatureById(id));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CarFeatureResponse> createCarFeature(@RequestBody CarFeature carFeature) {
        return ResponseEntity.ok(carFeatureService.createCarFeature(carFeature.getFeature()));
    }

    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/{id}"
    )
    public ResponseEntity<CarFeatureResponse> updateCarFeature(@PathVariable("id") Long id, @RequestBody CarFeature carFeature) {
        return ResponseEntity.ok(carFeatureService.updateCarFeature(id, carFeature.getFeature()));
    }

    @DeleteMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/{id}"
    )
    public ResponseEntity<CarFeatureResponse> deleteCarFeature(@PathVariable("id") Long id) {
        return ResponseEntity.ok(carFeatureService.deleteCarFeature(id));
    }
}
