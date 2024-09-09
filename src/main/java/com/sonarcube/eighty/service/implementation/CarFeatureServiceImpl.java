package com.sonarcube.eighty.service.implementation;

import com.sonarcube.eighty.dto.CarFeatureResponse;
import com.sonarcube.eighty.exception.ResourceAlreadyExistsException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import com.sonarcube.eighty.model.CarFeature;
import com.sonarcube.eighty.repository.CarFeatureRepository;
import com.sonarcube.eighty.service.CarFeatureService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarFeatureServiceImpl implements CarFeatureService {

    private final CarFeatureRepository carFeatureRepository;

    public CarFeatureServiceImpl(CarFeatureRepository carFeatureRepository) {
        this.carFeatureRepository = carFeatureRepository;
    }

    private static final String CAR_FEATURE = "Car Feature";

    @Override
    public List<CarFeatureResponse> getAllCarFeatures() {
        List<CarFeature> carFeatures = carFeatureRepository.findAll();
        List<CarFeatureResponse> carFeatureResponseList = new ArrayList<>();
        for (CarFeature carFeature : carFeatures) {
            CarFeatureResponse carFeatureResponse = mapToCarFeatureResponse(carFeature);
            carFeatureResponseList.add(carFeatureResponse);
        }
        return carFeatureResponseList;
    }

    @Override
    public CarFeatureResponse getCarFeatureById(Long id) {
        CarFeature carFeature = carFeatureRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR_FEATURE, "id", id)
        );
        return mapToCarFeatureResponse(carFeature);
    }

    @Override
    public CarFeatureResponse createCarFeature(String feature) {
        CarFeature carFeature = carFeatureRepository.findByFeature(feature).orElse(null);
        if (carFeature == null) {
            CarFeature newCarFeature = CarFeature.builder()
                    .feature(feature)
                    .createdAt(ZonedDateTime.now().toEpochSecond())
                    .build();
            CarFeature saved = carFeatureRepository.save(newCarFeature);
            return mapToCarFeatureResponse(saved);
        }else{
            throw new ResourceAlreadyExistsException(CAR_FEATURE, "feature", feature);
        }
    }

    @Override
    public CarFeatureResponse updateCarFeature(Long id, String feature) {
        CarFeature existingCarFeature = carFeatureRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR_FEATURE, "id", id)
        );
        CarFeature carFeature = carFeatureRepository.findByFeature(feature).orElse(null);
        if(carFeature != null && !carFeature.getId().equals(existingCarFeature.getId())){
            throw new ResourceAlreadyExistsException(CAR_FEATURE, "feature", feature);
        }
        existingCarFeature.setFeature(feature);
        existingCarFeature.setUpdatedAt(ZonedDateTime.now().toEpochSecond());
        CarFeature saved = carFeatureRepository.save(existingCarFeature);
        return mapToCarFeatureResponse(saved);
    }

    @Override
    public CarFeatureResponse deleteCarFeature(Long id) {
        CarFeature carFeature = carFeatureRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR_FEATURE, "id", id)
        );
        carFeature.setDeletedAt(ZonedDateTime.now().toEpochSecond());
        carFeature.setActive(false);
        CarFeature saved = carFeatureRepository.save(carFeature);
        return mapToCarFeatureResponse(saved);
    }

    private CarFeatureResponse mapToCarFeatureResponse(CarFeature carFeature) {
        Instant createdAt = Instant.ofEpochSecond(carFeature.getCreatedAt());
        Instant updatedAt = carFeature.getUpdatedAt() != null ? Instant.ofEpochSecond(carFeature.getUpdatedAt()) : null;
        Instant deletedAt = carFeature.getDeletedAt() != null ? Instant.ofEpochSecond(carFeature.getDeletedAt()) : null;
        return CarFeatureResponse.builder()
                .id(carFeature.getId())
                .feature(carFeature.getFeature())
                .isActive(carFeature.isActive())
                .createdAt(createdAt.atZone(ZoneId.of("UTC")))
                .updatedAt(updatedAt != null ? updatedAt.atZone(ZoneId.of("UTC")) : null)
                .deletedAt(deletedAt != null ? deletedAt.atZone(ZoneId.of("UTC")) : null)
                .build();
    }
}
