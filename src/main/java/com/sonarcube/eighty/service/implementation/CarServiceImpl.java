package com.sonarcube.eighty.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonarcube.eighty.dto.CarDto;
import com.sonarcube.eighty.dto.Dimensions;
import com.sonarcube.eighty.dto.Engine;
import com.sonarcube.eighty.dto.Warranty;
import com.sonarcube.eighty.exception.ResourceConversionException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import com.sonarcube.eighty.model.Car;
import com.sonarcube.eighty.repository.CarRepository;
import com.sonarcube.eighty.service.CarService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<CarDto> getAllCars() {
        // Car to CarDto conversion
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(car -> {
                    try {
                        return convertToDto(car);
                    } catch (JsonProcessingException e) {
                        throw new ResourceConversionException("Car", "CarDto");
                    }
                })
                .toList();
    }


    @Override
    public CarDto getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Car", "id", id)
        );

        try {
            return convertToDto(car);
        } catch (JsonProcessingException e) {
            throw new ResourceConversionException("Car", "CarDto");
        }
    }

    @Override
    public CarDto saveCar(CarDto carDto) {
//        if (carRepository.existsById(carDto.getId())) {
//            throw new ResourceAlreadyExistsException("Car", "id", carDto.getId());
//        }

        try {
            Car car = convertToCar(carDto);
            carRepository.save(car);
            return convertToDto(car);
        } catch (JsonProcessingException e) {
            throw new ResourceConversionException("Car", "CarDto");
        }
    }

    @Override
    public Car updateCar(Long id, Car car) {
        return carRepository.findById(id).map(existingCar -> {
            Car updatedCar = updateCarDetails(existingCar, car);
            return carRepository.save(updatedCar);
        }).orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
    }

    @Override
    public boolean deleteCar(Long id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
            return true;
        } else {
            throw new ResourceNotFoundException("Car", "id", id);
        }
    }

    private Car updateCarDetails(Car existingCar, Car car) {
        existingCar.setMake(car.getMake());
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
                .make(car.getMake())
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

    private Car convertToCar(CarDto carDto) throws JsonProcessingException {
        String engine = objectMapper.writeValueAsString(carDto.getEngine());
        String warranty = objectMapper.writeValueAsString(carDto.getWarranty());
        String dimensions = objectMapper.writeValueAsString(carDto.getDimensions());
        String features = objectMapper.writeValueAsString(carDto.getFeatures());
        String maintenanceDates = objectMapper.writeValueAsString(carDto.getMaintenanceDates());
        return Car.builder()
                .id(carDto.getId())
                .make(carDto.getMake())
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
}
