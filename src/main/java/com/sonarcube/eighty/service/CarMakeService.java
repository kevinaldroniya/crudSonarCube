package com.sonarcube.eighty.service;

import com.sonarcube.eighty.dto.CarMakeRequest;
import com.sonarcube.eighty.dto.CarMakeResponse;

import java.util.List;

public interface CarMakeService {
    List<CarMakeResponse> getAllCarMakes();
    CarMakeResponse getCarMakes(Long id);
    CarMakeResponse saveCarMake(CarMakeRequest request);
    CarMakeResponse updateCarMake(Long id, CarMakeRequest request);
    String deleteCarMake(Long id);
}
