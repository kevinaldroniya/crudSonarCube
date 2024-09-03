package com.sonarcube.eighty.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonarcube.eighty.dto.*;
import com.sonarcube.eighty.exception.InvalidRequestException;
import com.sonarcube.eighty.exception.ResourceConversionException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import com.sonarcube.eighty.model.Car;
import com.sonarcube.eighty.model.CarMake;
import com.sonarcube.eighty.repository.CarMakeRepository;
import com.sonarcube.eighty.repository.CarRepository;
import com.sonarcube.eighty.service.CarService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarMakeRepository carMakeRepository;
    private final ObjectMapper objectMapper;
    private static final String CAR_DTO = "CarDto";
    private static final String CAR = "Car";

    @Override
    public List<CarDtoResponse> getAllCars() {
        // Car to CarDto conversion
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(car -> {
                    try {
                        return convertToDtoResponse(car);
                    } catch (JsonProcessingException e) {
                        throw new ResourceConversionException(CAR, CAR_DTO);
                    }
                })
                .toList();
    }


    @Override
    public CarDtoResponse getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR, "id", id)
        );
        try {
            return convertToDtoResponse(car);
        } catch (JsonProcessingException e) {
            throw new ResourceConversionException(CAR, CAR_DTO);
        }
    }

    @Override
    public CarDtoResponse saveCar(@Valid CarDtoRequest carDtoRequest) {
        validateRequest(carDtoRequest);
        CarMake carMake = carMakeRepository.findByName(carDtoRequest.getMake()).orElseThrow(
                () -> new ResourceNotFoundException("Car Make", "make", carDtoRequest.getMake())
        );
        try {
            Car car = convertToCar(carDtoRequest, carMake);
            car.setCreatedAt(ZonedDateTime.now().toEpochSecond());
            car.setStatus(CarStatus.ACTIVE.getValue());
            Car saved = carRepository.save(car);
            return convertToDtoResponse(saved);
        } catch (JsonProcessingException e) {
            throw new ResourceConversionException(CAR_DTO, CAR);
        }
    }

    @Override
    public CarDtoResponse updateCar(Long id, CarDtoRequest carDtoRequest) {
        validateRequest(carDtoRequest);
        Car carById = carRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR, "id", id)
        );
        CarMake carMake = carMakeRepository.findByName(carDtoRequest.getMake()).orElseThrow(
                () -> new ResourceNotFoundException("Car Make", "make", carDtoRequest.getMake())
        );
        try {
            Car convertedToCar = convertToCar(carDtoRequest, carMake);
            Car updateCar = updateCarDetails(carById, convertedToCar);
            updateCar.setUpdatedAt(ZonedDateTime.now().toEpochSecond());
            Car save = carRepository.save(updateCar);
            return convertToDtoResponse(save);
        }catch (JsonProcessingException e){
            throw new ResourceConversionException(CAR_DTO, CAR);
        }
    }

    @Override
    public String deleteCar(Long id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
            return "Car with id: " + id + " deleted successfully";
        } else {
            throw new ResourceNotFoundException(CAR, "id", id);
        }
    }

    private Car updateCarDetails(Car existingCar, Car car) {
        existingCar.setCarMake(car.getCarMake());
        existingCar.setModel(car.getModel());
        existingCar.setYear(car.getYear());
        existingCar.setPrice(car.getPrice());
        existingCar.setElectric(car.isElectric());
        existingCar.setFeatures(car.getFeatures());
        existingCar.setEngine(car.getEngine());
        existingCar.setPreviousOwner(car.getPreviousOwner());
        existingCar.setWarranty(car.getWarranty());
        existingCar.setMaintenanceDates(car.getMaintenanceDates());
        existingCar.setDimensions(car.getDimensions());
        return existingCar;
    }

    private CarDtoResponse convertToDtoResponse(Car car) throws JsonProcessingException {
        Instant createdAt = Instant.ofEpochSecond(car.getCreatedAt());
        Instant updatedAt = car.getUpdatedAt() != null ? Instant.ofEpochSecond(car.getUpdatedAt()) : null;
        return CarDtoResponse.builder()
                .id(car.getId())
                .make(car.getCarMake().getName())
                .model(car.getModel())
                .year(car.getYear())
                .price(car.getPrice())
                .isElectric(car.isElectric())
                .status(CarStatus.fromValue(car.getStatus()))
                .features(Arrays.asList(objectMapper.readValue(car.getFeatures(), String[].class)))
                .engine(objectMapper.readValue(car.getEngine(), Engine.class))
                .previousOwner(car.getPreviousOwner())
                .warranty(objectMapper.readValue(car.getWarranty(), Warranty.class))
                .maintenanceDates(Arrays.asList(objectMapper.readValue(car.getMaintenanceDates(), LocalDate[].class)))
                .dimensions(objectMapper.readValue(car.getDimensions(), Dimensions.class))
                .createdAt(createdAt.atZone(ZoneId.of("UTC")))
                .updatedAt(updatedAt != null ? updatedAt.atZone(ZoneId.of("UTC")) : null)
                .build();
    }

    private Car convertToCar(CarDtoRequest carDtoRequest, CarMake carMake) throws JsonProcessingException {
        String engine = objectMapper.writeValueAsString(carDtoRequest.getEngine());
        String warranty = objectMapper.writeValueAsString(carDtoRequest.getWarranty());
        String dimensions = objectMapper.writeValueAsString(carDtoRequest.getDimensions());
        String features = objectMapper.writeValueAsString(carDtoRequest.getFeatures());
        String maintenanceDates = objectMapper.writeValueAsString(carDtoRequest.getMaintenanceDates());
        return Car.builder()
                .id(carDtoRequest.getId())
                .carMake(carMake)
                .model(carDtoRequest.getModel())
                .year(carDtoRequest.getYear())
                .price(carDtoRequest.getPrice())
                .isElectric(carDtoRequest.isElectric())
                .features(features)
                .engine(engine)
                .previousOwner(carDtoRequest.getPreviousOwner())
                .warranty(warranty)
                .maintenanceDates(maintenanceDates)
                .dimensions(dimensions)
                .build();
    }

    private void validateRequest(CarDtoRequest carDtoRequest){
        validateMake(carDtoRequest.getMake());
        validateModel(carDtoRequest.getModel());
        validateYear(carDtoRequest.getYear());
        validatePrice(carDtoRequest.getPrice());
        validateFeatures(carDtoRequest.getFeatures());
        validateEngine(carDtoRequest.getEngine());
        validatePreviousOwner(carDtoRequest.getPreviousOwner());
        validateWarranty(carDtoRequest.getWarranty());
        validateMaintenanceDates(carDtoRequest.getMaintenanceDates());
        validateDimensions(carDtoRequest.getDimensions());
    }

    private void validateMaintenanceDates(List<LocalDate> maintenanceDates) {
        if (Objects.isNull(maintenanceDates)){
            throw new InvalidRequestException("'maintenanceDates' must not be null");
        } else if (maintenanceDates.size() < 2 || maintenanceDates.size() > 10) {
            throw new InvalidRequestException("'maintenanceDates' must be between 2 and 10");
        }
    }

    private void validateMake(String make){
        if (Objects.isNull(make) || make.isEmpty() || make.isBlank()){
            throw new InvalidRequestException("'make' must not be empty");
        }
    }

    private void validateModel(String model){
        if (Objects.isNull(model) || model.isEmpty()||model.isBlank()){
            throw new InvalidRequestException("'model' must not be empty");
        }
    }

    private void validateYear(int year){
        if (year < 1950){
            throw new InvalidRequestException("'year' must be greater than or equal to 1950");
        }else if (year > 2024){
            throw new InvalidRequestException("'year' must be less than or equal to 2024");
        }
    }

    private void validatePrice(double price){
        if (price <= 0 ){
            throw new InvalidRequestException("'price' must be greater than 0");
        }
    }

    private void validateFeatures(List<String> features){
        if (Objects.isNull(features) || features.isEmpty() || features.size() >10){
            throw new InvalidRequestException("'features' size must be between 2 and 10");
        }
    }

    private static void validateEngine(Engine engine){
        if (Objects.isNull(engine)){
            throw new InvalidRequestException("'engine' must not be null");
        } else if (Objects.isNull(engine.getType())|| engine.getType().isEmpty() || engine.getType().isBlank()) {
            throw new InvalidRequestException("'engine.type' must not be empty");
        } else if (engine.getTorque() < 0) {
            throw new InvalidRequestException("'engine.torque' must be greater than or equal to 0");
        } else if (engine.getHorsepower() < 0) {
            throw new InvalidRequestException("'engine.horsepower' must be greater than or equal to 0");
        }
    }

    private static void validatePreviousOwner(int previousOwner){
        if (previousOwner < 0 ){
            throw new InvalidRequestException("'previousOwner' must be greater than or equal to 0");
        }
    }

    private static void validateWarranty(Warranty warranty){
        if (Objects.isNull(warranty)) {
            throw new InvalidRequestException("'warranty' must not be null");
        } else if (warranty.getBasic() == null || warranty.getBasic().isEmpty() || warranty.getBasic().isBlank()) {
            throw new InvalidRequestException("'warranty.basic' must not be empty");
        } else if (warranty.getPowertrain() == null || warranty.getPowertrain().isEmpty() || warranty.getPowertrain().isBlank()) {
            throw new InvalidRequestException("'warranty.powertrain' must not be empty");
        }
    }

    private static void validateDimensions(Dimensions dimensions){
        if (Objects.isNull(dimensions)){
            throw new InvalidRequestException("'dimensions' must not be empty");
        } else if (dimensions.getLength() < 0) {
            throw new InvalidRequestException("'dimensions.length' must be greater than or equal to 0");
        } else if (dimensions.getWidth() < 0) {
            throw new InvalidRequestException("'dimensions.width' must be greater than or equal to 0");
        } else if (dimensions.getHeight() < 0) {
            throw new InvalidRequestException("'dimensions.height' must be greater than or equal to 0");
        } else if (dimensions.getWeight() < 0) {
            throw new InvalidRequestException("'dimensions.weight' must be greater than or equal to 0");
        }
    }
}
