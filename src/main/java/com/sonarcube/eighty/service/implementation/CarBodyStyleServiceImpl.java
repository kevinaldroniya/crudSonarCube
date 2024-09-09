package com.sonarcube.eighty.service.implementation;

import com.sonarcube.eighty.dto.CarBodyResponse;
import com.sonarcube.eighty.exception.ResourceAlreadyExistsException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import com.sonarcube.eighty.model.CarBodyStyle;
import com.sonarcube.eighty.repository.CarBodyStyleRepository;
import com.sonarcube.eighty.service.CarBodyStyleService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarBodyStyleServiceImpl implements CarBodyStyleService {

    private final CarBodyStyleRepository carBodyStyleRepository;

    public CarBodyStyleServiceImpl(CarBodyStyleRepository carBodyStyleRepository){
        this.carBodyStyleRepository = carBodyStyleRepository;
    }

    private static final String CAR_BODY_STYLE = "Car Body Style";

    @Override
    public List<CarBodyResponse> getAllCarBodyStyles() {
        List<CarBodyStyle> all = carBodyStyleRepository.findAll();
        List<CarBodyResponse> carBodyResponses = new ArrayList<>();
        for (CarBodyStyle carBodyStyle : all) {
            CarBodyResponse carBodyResponse = convertToCarBodyResponse(carBodyStyle);
            carBodyResponses.add(carBodyResponse);
        }
        return carBodyResponses;
    }

    @Override
    public CarBodyResponse getCarBodyStyleById(Long id) {
        CarBodyStyle carBodyStyle = carBodyStyleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(CAR_BODY_STYLE, "id", id));
        return convertToCarBodyResponse(carBodyStyle);
    }

    @Override
    public CarBodyResponse createCarBodyStyle(String name) {
        Optional<CarBodyStyle> optionalCarBodyStyle = carBodyStyleRepository.findByName(name);
        if (optionalCarBodyStyle.isEmpty()){
            CarBodyStyle carBodyStyle = CarBodyStyle.builder()
                    .name(name)
                    .createdAt(ZonedDateTime.now().toEpochSecond())
                    .isActive(true)
                    .build();
            carBodyStyleRepository.save(carBodyStyle);
            return convertToCarBodyResponse(carBodyStyle);
        }else{
            throw new ResourceNotFoundException(CAR_BODY_STYLE, "name", name);
        }
    }

    @Override
    public CarBodyResponse updateCarBodyStyle(Long id, String name) {
        CarBodyStyle carBodyStyle = carBodyStyleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR_BODY_STYLE, "id", id)
        );
        Optional<CarBodyStyle> optionalCarBodyStyle = carBodyStyleRepository.findByName(name);
        if (optionalCarBodyStyle.isPresent() && carBodyStyle.getId().equals(optionalCarBodyStyle.get().getId())){
            throw new ResourceAlreadyExistsException(CAR_BODY_STYLE, "name", name);
        }
        carBodyStyle.setName(name);
        carBodyStyle.setUpdatedAt(ZonedDateTime.now().toEpochSecond());
        CarBodyStyle saved = carBodyStyleRepository.save(carBodyStyle);
        return convertToCarBodyResponse(saved);
    }

    @Override
    public CarBodyResponse deleteCarBodyStyle(Long id) {
        CarBodyStyle carBodyStyle = carBodyStyleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR_BODY_STYLE, "id", id)
        );
        carBodyStyle.setActive(false);
        CarBodyStyle saved = carBodyStyleRepository.save(carBodyStyle);
        return convertToCarBodyResponse(saved);
    }

    private CarBodyResponse convertToCarBodyResponse(CarBodyStyle carBodyStyle) {
        return CarBodyResponse.builder()
                .id(carBodyStyle.getId())
                .name(carBodyStyle.getName())
                .isActive(carBodyStyle.isActive())
                .createdAt(carBodyStyle.getCreatedAt())
                .updatedAt(carBodyStyle.getUpdatedAt())
                .deletedAt(carBodyStyle.getDeletedAt())
                .build();
    }
}
