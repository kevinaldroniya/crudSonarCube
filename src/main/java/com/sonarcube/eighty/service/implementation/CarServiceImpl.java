package com.sonarcube.eighty.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonarcube.eighty.dto.CarDto;
import com.sonarcube.eighty.dto.Dimensions;
import com.sonarcube.eighty.dto.Engine;
import com.sonarcube.eighty.dto.Warranty;
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

import java.time.LocalDate;
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
    public List<CarDto> getAllCars() {
        // Car to CarDto conversion
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(car -> {
                    try {
                        return convertToDto(car);
                    } catch (JsonProcessingException e) {
                        throw new ResourceConversionException(CAR, CAR_DTO);
                    }
                })
                .toList();
    }


    @Override
    public CarDto getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR, "id", id)
        );
        try {
            return convertToDto(car);
        } catch (JsonProcessingException e) {
            throw new ResourceConversionException(CAR, CAR_DTO);
        }
    }

    @Override
    public CarDto saveCar(@Valid CarDto carDto) {
        CarMake carMake = carMakeRepository.findByName(carDto.getMake()).orElseThrow(
                () -> new ResourceNotFoundException("Car Make", "make", carDto.getMake())
        );
        validateRequest(carDto);

        try {
            Car car = convertToCar(carDto, carMake);
            Car saved = carRepository.save(car);
            return convertToDto(saved);
        } catch (JsonProcessingException e) {
            throw new ResourceConversionException(CAR_DTO, CAR);
        }
    }

    @Override
    public CarDto updateCar(Long id, CarDto carDto) {
        Car carById = carRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(CAR, "id", id)
        );
        CarMake carMake = carMakeRepository.findByName(carDto.getMake()).orElseThrow(
                () -> new ResourceNotFoundException("Car Make", "make", carDto.getMake())
        );
        validateRequest(carDto);
        try {
            Car convertedToCar = convertToCar(carDto, carMake);
            Car updateCar = updateCarDetails(carById, convertedToCar);
            Car save = carRepository.save(updateCar);
            return convertToDto(save);
        }catch (JsonProcessingException e){
            throw new ResourceConversionException(CAR_DTO, CAR);
        }
    }

    @Override
    public String deleteCar(Long id) {
//        if (carRepository.existsById(id)) {
//            carRepository.deleteById(id);
//            return "Car with id: " + id + " deleted successfully";
//        } else {
//            throw new ResourceNotFoundException(CAR, "id", id);
//        }
        return null;
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

    private CarDto convertToDto(Car car) throws JsonProcessingException {
        return CarDto.builder()
                .id(car.getId())
                .make(car.getCarMake().getName())
                .model(car.getModel())
                .year(car.getYear())
                .price(car.getPrice())
                .isElectric(car.isElectric())
                .features(Arrays.asList(objectMapper.readValue(car.getFeatures(), String[].class)))
                .engine(objectMapper.readValue(car.getEngine(), Engine.class))
                .previousOwner(car.getPreviousOwner())
                .warranty(objectMapper.readValue(car.getWarranty(), Warranty.class))
                .maintenanceDates(Arrays.asList(objectMapper.readValue(car.getMaintenanceDates(), LocalDate[].class)))
                .dimensions(objectMapper.readValue(car.getDimensions(), Dimensions.class))
                .build();
    }

    private Car convertToCar(CarDto carDto, CarMake carMake) throws JsonProcessingException {
        String engine = objectMapper.writeValueAsString(carDto.getEngine());
        String warranty = objectMapper.writeValueAsString(carDto.getWarranty());
        String dimensions = objectMapper.writeValueAsString(carDto.getDimensions());
        String features = objectMapper.writeValueAsString(carDto.getFeatures());
        String maintenanceDates = objectMapper.writeValueAsString(carDto.getMaintenanceDates());
        return Car.builder()
                .id(carDto.getId())
                .carMake(carMake)
                .model(carDto.getModel())
                .year(carDto.getYear())
                .price(carDto.getPrice())
                .isElectric(carDto.isElectric())
                .features(features)
                .engine(engine)
                .previousOwner(carDto.getPreviousOwner())
                .warranty(warranty)
                .maintenanceDates(maintenanceDates)
                .dimensions(dimensions)
                .build();
    }

    private void validateRequest(CarDto carDto){
        validateMake(carDto.getMake());
        validateModel(carDto.getModel());
        validateYear(carDto.getYear());
        validatePrice(carDto.getPrice());
        validateFeatures(carDto.getFeatures());
        validateEngine(carDto.getEngine());
        validatePreviousOwner(carDto.getPreviousOwner());
        validateWarranty(carDto.getWarranty());
        validateMaintenanceDates(carDto.getMaintenanceDates());
        validateDimensions(carDto.getDimensions());
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
