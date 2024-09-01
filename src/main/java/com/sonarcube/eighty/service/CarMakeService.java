package com.sonarcube.eighty.service;

import com.sonarcube.eighty.dto.CarMakeRequest;
import com.sonarcube.eighty.dto.CarMakeResponse;

import java.util.List;

public interface CarMakeService {
    List<CarMakeResponse> getAllCarModels();
    CarMakeResponse getCarModel(Long id);
    CarMakeResponse saveCarModel(CarMakeRequest request);
    CarMakeResponse updateCarModel(Long id, CarMakeRequest request);
    String deleteCarModel(Long id);
}
