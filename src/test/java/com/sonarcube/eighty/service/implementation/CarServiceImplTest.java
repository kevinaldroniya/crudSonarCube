package com.sonarcube.eighty.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonarcube.eighty.dto.CarDto;
import com.sonarcube.eighty.exception.ResourceAlreadyExistsException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import com.sonarcube.eighty.model.Car;
import com.sonarcube.eighty.dto.Dimensions;
import com.sonarcube.eighty.dto.Engine;
import com.sonarcube.eighty.dto.Warranty;
import com.sonarcube.eighty.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CarServiceImplTest {

    @InjectMocks
    private CarServiceImpl carServiceImpl;

    @Mock
    private CarRepository carRepository;

    @Mock
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    void testGetAllCars_shouldReturnAllCars() throws JsonProcessingException {
        //Arrange
        List<Car> mockCars = getAllCars();
        LocalDate[] carDtoMaintenanceDates = getCarDtoMaintenanceDates();
        String maintenanceDates = getMaintenanceDates();
        String features = getFeatures();
        String engine = getEngine();
        when(carRepository.findAll()).thenReturn(mockCars);
        when(objectMapper.readValue(getNewEngine().toString(), Engine.class)).thenReturn(getCarDtoEngine());
        when(objectMapper.readValue(getMaintenanceDates(), LocalDate[].class)).thenReturn(getCarDtoMaintenanceDates());
        when(objectMapper.readValue(getFeatures(), String[].class)).thenReturn(getCarDtoFeatures());
        when(objectMapper.readValue(getWarranty(), Warranty.class)).thenReturn(getCarDtoWarranty());
        when(objectMapper.readValue(getDimensions(), Dimensions.class)).thenReturn(getCarDtoDimensions());
        //Act
        List<CarDto> actualCars = carServiceImpl.getAllCars();
        //Assert
        assertNotNull(actualCars);
    }

    @Test
    void testGetAllCars_shouldReturnEmptyList(){
        //Arrange
        List<Car> mockCars = new ArrayList<>();
        when(carRepository.findAll()).thenReturn(mockCars);
        //Act
        List<CarDto> actualCars = carServiceImpl.getAllCars();
        //Assert
        assertNotNull(actualCars);
    }

    @Test
    void testGetAllCars_shouldReturnNull(){
        //Arrange
        when(carRepository.findAll()).thenReturn(null);
        //Act
        List<CarDto> actualCars = carServiceImpl.getAllCars();
        //Assert
        assertNull(actualCars);
    }

    @Test
    void testGetCarById_shouldReturnCar() throws JsonProcessingException {
        //Arrange
        Car mockCar = getOneCar();
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCar));
        when(objectMapper.readValue(getFeatures(), String[].class)).thenReturn(getCarDtoFeatures());
        when(objectMapper.readValue(getEngine(), Engine.class)).thenReturn(getCarDtoEngine());
        when(objectMapper.readValue(getWarranty(), Warranty.class)).thenReturn(getCarDtoWarranty());
        when(objectMapper.readValue(getMaintenanceDates(), LocalDate[].class)).thenReturn(getCarDtoMaintenanceDates());
        when(objectMapper.readValue(getDimensions(), Dimensions.class)).thenReturn(getCarDtoDimensions());
        //Act
        CarDto actualCar = carServiceImpl.getCarById(1L);
        System.out.printf("actualCar : %s", actualCar);
        //Assert
        assertNotNull(actualCar);
        assertEquals(mockCar.getId(), actualCar.getId());
    }

    @Test
    void testGetCarById_ShouldThrowNotFoundException(){
        //Arrange
        when(carRepository.findById(1L)).thenReturn(Optional.empty());
        //Act
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> carServiceImpl.getCarById(1L));
        //Assert
        assertEquals("Car not found with id : '1'", e.getMessage());
    }

    @Test
    void testSaveCar_shouldReturnCar() throws JsonProcessingException {
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Car mockCar = getOneCar();
        //when(carRepository.existsById(oneCarDto.getId())).thenReturn(false);
        when(objectMapper.writeValueAsString(oneCarDto.getEngine())).thenReturn(mockCar.getEngine());
        when(objectMapper.writeValueAsString(oneCarDto.getWarranty())).thenReturn(mockCar.getWarranty());
        when(objectMapper.writeValueAsString(oneCarDto.getDimensions())).thenReturn(mockCar.getDimensions());
        when(objectMapper.writeValueAsString(oneCarDto.getFeatures())).thenReturn(mockCar.getFeatures());
        when(objectMapper.writeValueAsString(oneCarDto.getMaintenanceDates())).thenReturn(mockCar.getMaintenanceDates());
        when(objectMapper.readValue(getFeatures(), String[].class)).thenReturn(getCarDtoFeatures());
        when(objectMapper.readValue(getEngine(), Engine.class)).thenReturn(getCarDtoEngine());
        when(objectMapper.readValue(getWarranty(), Warranty.class)).thenReturn(getCarDtoWarranty());
        when(objectMapper.readValue(getMaintenanceDates(), LocalDate[].class)).thenReturn(getCarDtoMaintenanceDates());
        when(objectMapper.readValue(getDimensions(), Dimensions.class)).thenReturn(getCarDtoDimensions());
        //Act
        CarDto response = carServiceImpl.saveCar(oneCarDto);
        //Assert
        assertNotNull(response);
        assertEquals(oneCarDto.getId(), response.getId());
    }


//    @Test
//    void testSaveCar_shouldThrowAlreadyExistException(){
//        //Arrange
//        CarDto oneCarDto = getOneCarDto();
//        when(carRepository.existsById(1L)).thenReturn(true);
//        //Act
//        //CarDto carDto = carServiceImpl.saveCar(oneCarDto);
//        ResourceAlreadyExistsException response = assertThrows(ResourceAlreadyExistsException.class, () -> carServiceImpl.saveCar(oneCarDto));
//        //Assert
//        assertNotNull(response);
//        assertEquals("Car already exists with id : '1'", response.getMessage());
//    }

//    @Test
//    void testUpdateCar_shouldReturnCar(){
//        //Arrange
//        Car existsCar = getOneCar();
//        Car updateCar = updateCarData();
//        when(carRepository.findById(1L)).thenReturn(Optional.of(existsCar));
//        when(carRepository.save(updateCar)).thenReturn(updateCar);
//        //Act
//        Car car = carServiceImpl.updateCar(1L, updateCar);
//        //Assert
//        assertNotNull(car);
//    }

//    @Test
//    void testUpdateCar_shouldThrownNotFoundException(){
//        //Arrange
//        Car updateCar = updateCarData();
//        when(carRepository.findById(1L)).thenReturn(Optional.empty());
//        //Act
//        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> carServiceImpl.updateCar(1L, updateCar));
//        //Assert
//        assertEquals("Car not found with id : '1'", e.getMessage());
//    }
//
//    @Test
//    void testDeleteCar_shouldDeleteCar(){
//        //Arrange
//        when(carRepository.existsById(1L)).thenReturn(true);
//        //Act
//        boolean deleted = carServiceImpl.deleteCar(1L);
//        //Assert
//        assertTrue(deleted);
//    }
//
//    @Test
//    void testDeleteCar_shouldThrowNotFoundException(){
//        //Arrange
//        when(carRepository.existsById(1L)).thenReturn(false);
//        //Act
//        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> carServiceImpl.deleteCar(1L));
//        //Assert
//        assertEquals("Car not found with id : '1'", e.getMessage());
//    }

    private List<Car> getAllCars()  throws JsonProcessingException{
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            boolean isElectric = false;
            if (i%2==0){
                isElectric = true;
            }
            Car car = Car.builder()
                    .id((long) i)
                    .make("Make-" + i)
                    .model("Model-" + i)
                    .year(2021)
                    .price(10000)
                    .isElectric(isElectric)
                    .features(getFeatures())
                    .engine(getNewEngine().toString())
                    .previousOwner(1)
                    .warranty(getWarranty())
                    .maintenanceDates(getMaintenanceDates())
                    .dimensions(getDimensions())
                    .build();
            cars.add(car);
        }
        return cars;
    }

    private Map<String, Object> getNewEngine(){
        Map<String, Object> engine = new HashMap<>();
        engine.put("type","type");
        engine.put("horsepower",100);
        engine.put("torque",100);
        return engine;
    }

    private Car getOneCar(){
        return Car.builder()
                .id(1L)
                .make("Make")
                .model("Model")
                .year(2021)
                .price(10000)
                .isElectric(true)
                .features(getFeatures())
                .engine(getEngine())
                .previousOwner(1)
                .warranty(getWarranty())
                .maintenanceDates(getMaintenanceDates())
                .dimensions(getDimensions())
                .build();
    }

    private Car updateCarData() throws JsonProcessingException {
        return Car.builder()
                .id(1L)
                .make("Make-Updated")
                .model("Model-Updated")
                .year(2021)
                .price(10000)
                .isElectric(true)
                .features(objectMapper.writeValueAsString(getFeatures()))
                .engine(objectMapper.writeValueAsString(getEngine()))
                .previousOwner(1)
                .warranty(objectMapper.writeValueAsString(getWarranty()))
                .maintenanceDates(getMaintenanceDates())
                .dimensions(getDimensions())
                .build();
    }

    private String getFeatures(){
        List<String> features = new ArrayList<>();
        features.add("Feature1");
        features.add("Feature2");
        features.add("Feature3");

        return "[" + features.stream()
                .map(feature -> "\"" + feature + "\"")
                .collect(Collectors.joining(", "))+ "]";
    }

    private String getEngine(){
        return Engine.builder()
                .type("EngineType")
                .horsepower(200)
                .torque(300)
                .build().toString();
    }

    private String getWarranty(){
        return Warranty.builder()
                .basic("Basic")
                .powertrain("Powertrain")
                .build().toString();
    }

    private String getMaintenanceDates(){
        List<LocalDate> maintenanceDates = new ArrayList<>();
        maintenanceDates.add(LocalDate.now());
        maintenanceDates.add(LocalDate.now());
        maintenanceDates.add(LocalDate.now());

        List<String> maintenanceDatesAsString = maintenanceDates.stream()
                .map(LocalDate::toString)
                .toList();

        return "["+ maintenanceDatesAsString.stream()
                .map(date -> "\"" + date + "\"")
                .collect(Collectors.joining(", ")) + "]";
    }

    private String getDimensions(){
        return Dimensions.builder()
                .height(100)
                .length(200)
                .weight(300)
                .width(400)
                .build().toString();
    }

    private CarDto getOneCarDto(){
        return CarDto.builder()
                .id(1L)
                .make("Make")
                .model("Model")
                .year(2021)
                .price(10000)
                .isElectric(true)
                .features(List.of("Feature1", "Feature2", "Feature3"))
                .engine(Engine.builder()
                        .type("EngineType")
                        .horsepower(200)
                        .torque(300)
                        .build())
                .previousOwner(1)
                .warranty(Warranty.builder()
                        .basic("Basic")
                        .powertrain("Powertrain")
                        .build())
                .maintenanceDates(List.of(
                        LocalDate.now(),
                        LocalDate.now(),
                        LocalDate.now()
                ))
                .dimensions(Dimensions.builder()
                        .height(100)
                        .length(200)
                        .weight(300)
                        .width(400)
                        .build())
                .build();
    }

    private List<CarDto> getAllCarDto(){
        List<CarDto> carDtos = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            boolean isElectric = false;
            if (i%2==0){
                isElectric = true;
            }
            CarDto carDto = CarDto.builder()
                    .id((long) i)
                    .make("Make-" + i)
                    .model("Model-" + i)
                    .year(2021)
                    .price(10000)
                    .isElectric(isElectric)
                    .features(List.of("Feature1", "Feature2", "Feature3"))
                    .engine(Engine.builder()
                            .type("EngineType")
                            .horsepower(200)
                            .torque(300)
                            .build())
                    .previousOwner(1)
                    .warranty(Warranty.builder()
                            .basic("Basic")
                            .powertrain("Powertrain")
                            .build())
                    .maintenanceDates(List.of(
                            LocalDate.now(),
                            LocalDate.now(),
                            LocalDate.now()
                    ))
                    .dimensions(Dimensions.builder()
                            .height(100)
                            .length(200)
                            .weight(300)
                            .width(400)
                            .build())
                    .build();
            carDtos.add(carDto);
        }
        return carDtos;
    }

    private Engine getCarDtoEngine(){
        return Engine.builder()
                .horsepower(100)
                .torque(100)
                .type("type")
                .build();
    }

    private LocalDate[] getCarDtoMaintenanceDates(){
        List<LocalDate> maintenanceDates = new ArrayList<>();
        maintenanceDates.add(LocalDate.now());
        maintenanceDates.add(LocalDate.of(2022,7,31));
        maintenanceDates.add(LocalDate.EPOCH);
        return maintenanceDates.toArray(LocalDate[]::new);
    }

    private String[] getCarDtoFeatures(){
        List<String> features = new ArrayList<>();
        features.add("Features 1");
        features.add("Features 2");
        features.add("Features 3");
        return features.toArray(String[]::new);
    }

    private static Warranty getCarDtoWarranty(){
        return Warranty.builder()
                .powertrain("pwt")
                .basic("basic")
                .build();
    }

    private static Dimensions getCarDtoDimensions(){
        return Dimensions.builder()
                .height(100)
                .length(100)
                .weight(100)
                .width(100)
                .build();
    }
}