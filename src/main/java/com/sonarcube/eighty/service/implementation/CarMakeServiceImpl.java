package com.sonarcube.eighty.service.implementation;

import com.sonarcube.eighty.dto.CarMakeRequest;
import com.sonarcube.eighty.dto.CarMakeResponse;
import com.sonarcube.eighty.exception.ResourceAlreadyExistsException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import com.sonarcube.eighty.model.CarMake;
import com.sonarcube.eighty.repository.CarMakeRepository;
import com.sonarcube.eighty.service.CarMakeService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class CarMakeServiceImpl implements CarMakeService {

    private final CarMakeRepository carMakeRepository;

    CarMakeServiceImpl(CarMakeRepository carMakeRepository){
        this.carMakeRepository = carMakeRepository;
    }

    private static final String CAR_MAKE = "Car Make";

    @Override
    public List<CarMakeResponse> getAllCarMakes() {
        List<CarMake> carMakes = carMakeRepository.findAll();
        List<CarMakeResponse> carMakeResponse = new ArrayList<>();
        for (CarMake carMake : carMakes) {
            CarMakeResponse convertToCarMakeResponse = convertCarMakeToCarMakeResponse(carMake);
            carMakeResponse.add(convertToCarMakeResponse);
        }
        return carMakeResponse;
    }

    @Override
    public CarMakeResponse getCarMakes(Long id) {
        CarMake carMake = carMakeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR_MAKE, "id", id)
        );
        return convertCarMakeToCarMakeResponse(carMake);
    }

    @Override
    public CarMakeResponse saveCarMake(CarMakeRequest request) {
        Optional<CarMake> optionalCarMake = carMakeRepository.findByName(request.getName());
        if (optionalCarMake.isPresent()){
            throw new ResourceAlreadyExistsException(CAR_MAKE, "name", request.getName());
        }
        CarMake carMake = CarMake.builder()
                .name(request.getName())
                .isActive(true)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond())
                .updatedAt(null)
                .deletedAt(null)
                .build();
        CarMake saved = carMakeRepository.save(carMake);
        return convertCarMakeToCarMakeResponse(saved);
    }

    @Override
    public CarMakeResponse updateCarMake(Long id, CarMakeRequest request) {
        CarMake carMakeById = carMakeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR_MAKE, "id", id)
        );
        Optional<CarMake> existingCarMake = carMakeRepository.findByName(request.getName());
        if (existingCarMake.isPresent() && !existingCarMake.get().getId().equals(id)) {
            throw new ResourceAlreadyExistsException(CAR_MAKE, "name", request.getName());
        }
        CarMake carMake = CarMake.builder()
                .id(carMakeById.getId())
                .name(request.getName())
                .isActive(carMakeById.isActive())
                .createdAt(carMakeById.getCreatedAt())
                .updatedAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond())
                .deletedAt(carMakeById.getDeletedAt())
                .build();
        CarMake saved = carMakeRepository.save(carMake);
        return convertCarMakeToCarMakeResponse(saved);
    }

    @Override
    public String deleteCarMake(Long id) {
        CarMake existingCarMake = carMakeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR_MAKE, "id", id)
        );
        CarMake carMake = CarMake.builder()
                .id(existingCarMake.getId())
                .name(existingCarMake.getName())
                .isActive(false)
                .createdAt(existingCarMake.getCreatedAt())
                .updatedAt(existingCarMake.getUpdatedAt())
                .deletedAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond())
                .build();
        carMakeRepository.save(carMake);
        return "Car Make successfully deleted!";
    }

    private CarMakeResponse convertCarMakeToCarMakeResponse(CarMake carMake) {
        Instant createdAt = Instant.ofEpochSecond(carMake.getCreatedAt());
        Instant updatedAt = carMake.getUpdatedAt() != null ? Instant.ofEpochSecond(carMake.getUpdatedAt()) : null;
        Instant deletedAt = carMake.getDeletedAt() != null ? Instant.ofEpochSecond(carMake.getDeletedAt()) : null;
        return CarMakeResponse.builder()
                .id(carMake.getId())
                .name(carMake.getName())
                .isActive(carMake.isActive())
                .createdAt(createdAt.atZone(ZoneId.of("UTC")))
                .updatedAt(updatedAt != null ? updatedAt.atZone(ZoneId.of("UTC")) : null)
                .deletedAt(deletedAt != null ? deletedAt.atZone(ZoneId.of("UTC")) : null)
                .build();
    }
}
