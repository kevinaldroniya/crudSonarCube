package com.sonarcube.eighty.service;

import com.sonarcube.eighty.dto.CarFeatureResponse;

import java.util.List;

public interface CarFeatureService {
    List<CarFeatureResponse> getAllCarFeatures();
    CarFeatureResponse getCarFeatureById(Long id);
    CarFeatureResponse createCarFeature(String feature);
    CarFeatureResponse updateCarFeature(Long id, String feature);
    CarFeatureResponse deleteCarFeature(Long id);
}
