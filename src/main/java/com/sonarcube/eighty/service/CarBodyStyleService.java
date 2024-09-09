package com.sonarcube.eighty.service;

import com.sonarcube.eighty.dto.CarBodyResponse;

import java.util.List;

public interface CarBodyStyleService {
    List<CarBodyResponse> getAllCarBodyStyles();
    CarBodyResponse getCarBodyStyleById(Long id);
    CarBodyResponse createCarBodyStyle(String name);
    CarBodyResponse updateCarBodyStyle(Long id, String name);
    CarBodyResponse deleteCarBodyStyle(Long id);
}
