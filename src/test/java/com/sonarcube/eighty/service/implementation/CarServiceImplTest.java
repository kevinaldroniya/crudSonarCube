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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarServiceImplTest {

    @InjectMocks
    private CarServiceImpl carServiceImpl;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMakeRepository carMakeRepository;

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
        when(carRepository.findAll()).thenReturn(mockCars);
        when(objectMapper.readValue(getNewEngine().toString(), Engine.class)).thenReturn(getCarDtoEngine());
        when(objectMapper.readValue(getMaintenanceDates(), LocalDate[].class)).thenReturn(getCarDtoMaintenanceDates());
        when(objectMapper.readValue(getFeatures(), String[].class)).thenReturn(getCarDtoFeatures());
        when(objectMapper.readValue(getWarranty(), Warranty.class)).thenReturn(getCarDtoWarranty());
        when(objectMapper.readValue(getDimensions(), Dimensions.class)).thenReturn(getCarDtoDimensions());
        //Act
        List<CarDtoResponse> actualCars = carServiceImpl.getAllCars();
        //Assert
        assertNotNull(actualCars);
    }

    @Test
    void testGetAllCars_shouldReturnEmptyList(){
        //Arrange
        List<Car> mockCars = new ArrayList<>();
        when(carRepository.findAll()).thenReturn(mockCars);
        //Act
        List<CarDtoResponse> actualCars = carServiceImpl.getAllCars();
        //Assert
        assertNotNull(actualCars);
    }

    @Test
    void testGetAllCars_shouldResourceConversionException_whenJsonProcessingException() throws JsonProcessingException {
        List<Car> allCars = getAllCars();
        when(carRepository.findAll()).thenReturn(allCars);
        doThrow(JsonProcessingException.class).when(objectMapper).readValue(any(String.class), any(Class.class));
        ResourceConversionException response = assertThrows(ResourceConversionException.class, () -> carServiceImpl.getAllCars());
        assertNotNull(response.getMessage());
        assertEquals("Error while serializing Car to CarDto", response.getMessage());
    }

    @Test
    void testGetCarById_shouldResourceConversionException_whenJsonProcessingException() throws JsonProcessingException {
        //Arrange
        Car mockCar = getOneCar();
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCar));
        doThrow(JsonProcessingException.class).when(objectMapper).readValue(any(String.class), any(Class.class));
        //Act
        ResourceConversionException e = assertThrows(ResourceConversionException.class, () -> carServiceImpl.getCarById(1L));
        //Assert
        assertEquals("Error while serializing Car to CarDto", e.getMessage());
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
        CarDtoResponse actualCar = carServiceImpl.getCarById(1L);
        //Assert
        assertNotNull(actualCar);
        assertEquals(mockCar.getId(), actualCar.getId());
    }

    @Test
    void testGetCarById_shouldThrow() throws JsonProcessingException {
        //Arrange
        Car mockCar = getOneCar();
        mockCar.setStatus("notValid");
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCar));
        when(objectMapper.readValue(getFeatures(), String[].class)).thenReturn(getCarDtoFeatures());
        when(objectMapper.readValue(getEngine(), Engine.class)).thenReturn(getCarDtoEngine());
        when(objectMapper.readValue(getWarranty(), Warranty.class)).thenReturn(getCarDtoWarranty());
        when(objectMapper.readValue(getMaintenanceDates(), LocalDate[].class)).thenReturn(getCarDtoMaintenanceDates());
        when(objectMapper.readValue(getDimensions(), Dimensions.class)).thenReturn(getCarDtoDimensions());
        //Act
        ResourceConversionException response = assertThrows(ResourceConversionException.class, () -> carServiceImpl.getCarById(1L));
        //Assert
        assertNotNull(response);
        assertEquals("Error while serializing Car to CarDtoResponse", response.getMessage());
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
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Car oneCar = getOneCar();
        System.out.println(oneCar.toString());
        when(carMakeRepository.findByName("Make")).thenReturn(Optional.of(getCarMake()));
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getEngine())).thenReturn(oneCar.getEngine());
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getWarranty())).thenReturn(oneCar.getWarranty());
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getDimensions())).thenReturn(oneCar.getDimensions());
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getFeatures())).thenReturn(oneCar.getFeatures());
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getMaintenanceDates())).thenReturn(oneCar.getMaintenanceDates());
        when(objectMapper.readValue(getFeatures(), String[].class)).thenReturn(getCarDtoFeatures());
        when(objectMapper.readValue(getEngine(), Engine.class)).thenReturn(getCarDtoEngine());
        when(objectMapper.readValue(getWarranty(), Warranty.class)).thenReturn(getCarDtoWarranty());
        when(objectMapper.readValue(getMaintenanceDates(), LocalDate[].class)).thenReturn(getCarDtoMaintenanceDates());
        when(objectMapper.readValue(getDimensions(), Dimensions.class)).thenReturn(getCarDtoDimensions());
        when(carRepository.save(any(Car.class))).thenReturn(oneCar);

        //Act
        CarDtoResponse response = carServiceImpl.saveCar(oneCarDtoRequest);
        //Assert
        assertNotNull(response);
        assertEquals(oneCarDtoRequest.getMake(), response.getMake());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_makeEmpty(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setMake("");
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'make' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_makeBlank(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setMake("   ");
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'make' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_modelEmpty(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setModel("");
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'model' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_modelBlank(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setModel("  ");
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'model' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_yearLessThan(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setYear(1949);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'year' must be greater than or equal to 1950", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_yearGreaterThan(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setYear(2025);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'year' must be less than or equal to 2024", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_priceNegative(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setPrice(-1);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'price' must be greater than 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_featuresEmptyArray(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setFeatures(new ArrayList<>());
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'features' size must be between 2 and 10", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_featuresMaxArray(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        List<String> features = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            features.add(String.valueOf(i));
        }
        oneCarDtoRequest.setFeatures(features);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'features' size must be between 2 and 10", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineNull(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setEngine(null);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'engine' must not be null",response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineTypeIsEmpty(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Engine engine = Engine.builder()
                .torque(100)
                .horsepower(100)
                .type("")
                .build();
        oneCarDtoRequest.setEngine(engine);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'engine.type' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineTypeIsBlank(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Engine engine = Engine.builder()
                .torque(100)
                .horsepower(100)
                .type("  ")
                .build();
        oneCarDtoRequest.setEngine(engine);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'engine.type' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineTorqueIsLessThanZero(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Engine engine = Engine.builder()
                .torque(-100)
                .horsepower(100)
                .type("type")
                .build();
        oneCarDtoRequest.setEngine(engine);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'engine.torque' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineHorsepowerIsLessThanZero(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Engine engine = Engine.builder()
                .torque(100)
                .horsepower(-100)
                .type("type")
                .build();
        oneCarDtoRequest.setEngine(engine);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'engine.horsepower' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_previousOwnerLessThanZero(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setPreviousOwner(-100);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'previousOwner' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_warrantyNull(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setWarranty(null);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals("'warranty' must not be null", response.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "null, 'warranty.basic', 'warranty.basic' must not be empty",
            "'', 'warranty.basic', 'warranty.basic' must not be empty",
            "'  ', 'warranty.basic', 'warranty.basic' must not be empty",
            "null, 'warranty.powertrain', 'warranty.powertrain' must not be empty",
            "'', 'warranty.powertrain', 'warranty.powertrain' must not be empty",
            "'  ', 'warranty.powertrain', 'warranty.powertrain' must not be empty"
    })
    void testSaveCar_shouldThrowInvalidRequestException_warrantyInvalid(String value, String field, String expectedMessage){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        switch (field){
            case "warranty.basic":
                if (value.equalsIgnoreCase("null")){
                    oneCarDtoRequest.setWarranty(Warranty.builder().basic(null).powertrain("pwt").build());
                } else {
                    oneCarDtoRequest.setWarranty(Warranty.builder().basic(value).powertrain("pwt").build());
                }
                break;
            case "warranty.powertrain":
                if (value.equalsIgnoreCase("null")){
                    oneCarDtoRequest.setWarranty(Warranty.builder().basic("basic").powertrain(null).build());
                } else {
                    oneCarDtoRequest.setWarranty(Warranty.builder().basic("basic").powertrain(value).build());
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + field);
        }
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        //Assert
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsNull(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setDimensions(null);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        assertEquals("'dimensions' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsLengthLessThanZero(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Dimensions dimensions = Dimensions.builder()
                .length(-100)
                .height(100)
                .weight(100)
                .width(100)
                .build();
        oneCarDtoRequest.setDimensions(dimensions);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        assertEquals("'dimensions.length' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsWidthLessThanZero(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Dimensions dimensions = Dimensions.builder()
                .width(-100)
                .build();
        oneCarDtoRequest.setDimensions(dimensions);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        assertEquals("'dimensions.width' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsHeightLessThanZero(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Dimensions dimensions = Dimensions.builder()
                .height(-100)
                .build();
        oneCarDtoRequest.setDimensions(dimensions);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        assertEquals("'dimensions.height' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsWeightLessThanZero(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Dimensions dimensions = Dimensions.builder()
                .weight(-100)
                .build();
        oneCarDtoRequest.setDimensions(dimensions);
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        assertEquals("'dimensions.weight' must be greater than or equal to 0", response.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "'', 'make', 'make' must not be empty",
            "'   ', 'make', 'make' must not be empty",
            "'', 'model', 'model' must not be empty",
            "'  ', 'model', 'model' must not be empty",
            "1949, 'year', 'year' must be greater than or equal to 1950",
            "2025, 'year', 'year' must be less than or equal to 2024",
            "-1, 'price', 'price' must be greater than 0",
            "'[]', 'features', 'features' size must be between 2 and 10",
            "null, 'engine', 'engine' must not be null",
            "'', 'engine.type', 'engine.type' must not be empty",
            "'  ', 'engine.type', 'engine.type' must not be empty",
            "-100, 'engine.torque', 'engine.torque' must be greater than or equal to 0",
            "-100, 'engine.horsepower', 'engine.horsepower' must be greater than or equal to 0",
            "-100, 'previousOwner', 'previousOwner' must be greater than or equal to 0",
            "null, 'warranty', 'warranty' must not be null",
            "null, 'warranty.basic', 'warranty.basic' must not be empty",
            "'', 'warranty.basic', 'warranty.basic' must not be empty",
            "' ', 'warranty.basic', 'warranty.basic' must not be empty",
            "null, 'warranty.powertrain', 'warranty.powertrain' must not be empty",
            "'', 'warranty.powertrain', 'warranty.powertrain' must not be empty",
            "'  ', 'warranty.powertrain', 'warranty.powertrain' must not be empty",
            "null, 'dimensions', 'dimensions' must not be empty",
            "-100, 'dimensions.length', 'dimensions.length' must be greater than or equal to 0",
            "-100, 'dimensions.width', 'dimensions.width' must be greater than or equal to 0",
            "-100, 'dimensions.height', 'dimensions.height' must be greater than or equal to 0",
            "-100, 'dimensions.weight', 'dimensions.weight' must be greater than or equal to 0"
    })
    void testSaveCar_shouldThrowInvalidRequestException(String value, String field, String expectedMessage) {
        // Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        switch (field) {
            case "make":
                oneCarDtoRequest.setMake(value);
                break;
            case "model":
                oneCarDtoRequest.setModel(value);
                break;
            case "year":
                oneCarDtoRequest.setYear(Integer.parseInt(value));
                break;
            case "price":
                oneCarDtoRequest.setPrice(Double.parseDouble(value));
                break;
            case "features":
                oneCarDtoRequest.setFeatures(Collections.emptyList());
                break;
            case "engine":
                oneCarDtoRequest.setEngine(null);
                break;
            case "engine.type":
                if (oneCarDtoRequest.getEngine() == null) {
                    oneCarDtoRequest.setEngine(Engine.builder().type(value).build());
                } else {
                    oneCarDtoRequest.getEngine().setType(value);
                }
                break;
            case "engine.torque":
                if (oneCarDtoRequest.getEngine() == null) {
                    oneCarDtoRequest.setEngine(Engine.builder().torque(Integer.parseInt(value)).build());
                } else {
                    oneCarDtoRequest.getEngine().setTorque(Integer.parseInt(value));
                }
                break;
            case "engine.horsepower":
                if (oneCarDtoRequest.getEngine() == null) {
                    oneCarDtoRequest.setEngine(Engine.builder().horsepower(Integer.parseInt(value)).build());
                } else {
                    oneCarDtoRequest.getEngine().setHorsepower(Integer.parseInt(value));
                }
                break;
            case "previousOwner":
                oneCarDtoRequest.setPreviousOwner(Integer.parseInt(value));
                break;
            case "warranty":
                oneCarDtoRequest.setWarranty(null);
                break;
            case "warranty.basic":
                if (oneCarDtoRequest.getWarranty() == null) {
                    oneCarDtoRequest.setWarranty(Warranty.builder().basic(value).build());
                } else {
                    if (value.equals("null")){
                        oneCarDtoRequest.getWarranty().setBasic(null);
                    } else {
                        oneCarDtoRequest.getWarranty().setBasic(value);
                    }
                }
                break;
            case "warranty.powertrain":
                if (oneCarDtoRequest.getWarranty() == null) {
                    oneCarDtoRequest.setWarranty(Warranty.builder().powertrain(value).build());
                } else {
                    if (value.equalsIgnoreCase("null")){
                        oneCarDtoRequest.getWarranty().setPowertrain(null);
                    }else {
                        oneCarDtoRequest.getWarranty().setPowertrain(value);
                    }
                }
                break;
            case "dimensions":
                oneCarDtoRequest.setDimensions(null);
                break;
            case "dimensions.length":
                if (oneCarDtoRequest.getDimensions() == null) {
                    oneCarDtoRequest.setDimensions(Dimensions.builder().length(Integer.parseInt(value)).build());
                } else {
                    oneCarDtoRequest.getDimensions().setLength(Integer.parseInt(value));
                }
                break;
            case "dimensions.width":
                if (oneCarDtoRequest.getDimensions() == null) {
                    oneCarDtoRequest.setDimensions(Dimensions.builder().width(Integer.parseInt(value)).build());
                } else {
                    oneCarDtoRequest.getDimensions().setWidth(Integer.parseInt(value));
                }
                break;
            case "dimensions.height":
                if (oneCarDtoRequest.getDimensions() == null) {
                    oneCarDtoRequest.setDimensions(Dimensions.builder().height(Integer.parseInt(value)).build());
                } else {
                    oneCarDtoRequest.getDimensions().setHeight(Integer.parseInt(value));
                }
                break;
            case "dimensions.weight":
                if (oneCarDtoRequest.getDimensions() == null) {
                    oneCarDtoRequest.setDimensions(Dimensions.builder().weight(Integer.parseInt(value)).build());
                } else {
                    oneCarDtoRequest.getDimensions().setWeight(Integer.parseInt(value));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + field);
        }
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        // Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));

        // Assert
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowResourceConversionException_whenJsonProcessingException() throws JsonProcessingException {
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any());
        ResourceConversionException response = assertThrows(ResourceConversionException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        assertNotNull(response.getMessage());
        assertEquals("Error while serializing CarDto to Car", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowResourceNotFoundException_makeNotFound(){
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.empty());
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> carServiceImpl.saveCar(oneCarDtoRequest));
        assertNotNull(response.getMessage());
        assertEquals("Car Make not found with make : 'Make'", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowResourceConversionException_whenJsonProcessingException() throws JsonProcessingException {
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Car oneCar = getOneCar();
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any());
        ResourceConversionException response = assertThrows(ResourceConversionException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        assertNotNull(response.getMessage());
        assertEquals("Error while serializing CarDto to Car", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldReturnUpdatedCar() throws IOException {
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setMake("Make Update");
        Car oneCar = getOneCar();
        CarMake carMake = getCarMake();
        carMake.setName("Make Update");
        oneCar.setCarMake(carMake);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName("Make Update")).thenReturn(Optional.of(carMake));
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getEngine())).thenReturn(oneCar.getEngine());
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getWarranty())).thenReturn(oneCar.getWarranty());
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getDimensions())).thenReturn(oneCar.getDimensions());
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getFeatures())).thenReturn(oneCar.getFeatures());
        when(objectMapper.writeValueAsString(oneCarDtoRequest.getMaintenanceDates())).thenReturn(oneCar.getMaintenanceDates());
        when(objectMapper.readValue(getFeatures(), String[].class)).thenReturn(getCarDtoFeatures());
        when(objectMapper.readValue(getEngine(), Engine.class)).thenReturn(getCarDtoEngine());
        when(objectMapper.readValue(getWarranty(), Warranty.class)).thenReturn(getCarDtoWarranty());
        when(objectMapper.readValue(getMaintenanceDates(), LocalDate[].class)).thenReturn(getCarDtoMaintenanceDates());
        when(objectMapper.readValue(getDimensions(), Dimensions.class)).thenReturn(getCarDtoDimensions());
        when(carRepository.save(any(Car.class))).thenReturn(oneCar);
        //Act
        CarDtoResponse response = carServiceImpl.updateCar(1L, oneCarDtoRequest);
        //Assert
        assertNotNull(response.getId());
    }

    @Test
    void testUpdateCar_shouldThrowNotFoundException(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setMake("make update");
        //Act
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response);
        assertEquals("Car not found with id : '1'", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_makeNull(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setMake(null);
        Car oneCar = getOneCar();
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response);
        assertEquals("'make' must not be empty", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_makeEmpty(){
        //Arrange
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setMake("");
        Car oneCar = getOneCar();
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'make' must not be empty", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_makeBlank(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setMake("  ");
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'make' must not be empty", response.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "null, 'model' must not be empty",
            "'', 'model' must not be empty",
            "'  ', 'model' must not be empty",
    })
    void testUpdateCar_shouldThrowInvalidRequestException_modelInvalid(String value, String expectedMessage){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        if (value.equalsIgnoreCase("null")){
            oneCarDtoRequest.setModel(null);
        } else {
            oneCarDtoRequest.setModel(value);
        }
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_yearLessThan(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setYear(1949);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'year' must be greater than or equal to 1950", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_yearGreaterThan(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setYear(2045);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'year' must be less than or equal to 2024", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_priceLessThanZero(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setPrice(-1);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'price' must be greater than 0", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_featuresIsNull(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setFeatures(null);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'features' size must be between 2 and 10", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_featuresIsEmpty(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setFeatures(new ArrayList<>());
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'features' size must be between 2 and 10", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_featuresIsGreaterThan(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        List<String> features = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            features.add(String.valueOf(i));
        }
        oneCarDtoRequest.setFeatures(features);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'features' size must be between 2 and 10", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_EngineIsNull(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setEngine(null);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'engine' must not be null", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_engineTypeIsNull(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Engine engine = Engine.builder()
                .type(null)
                .horsepower(100)
                .torque(100)
                .build();
        oneCarDtoRequest.setEngine(engine);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'engine.type' must not be empty", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_engineTypeIsEmpty(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Engine carDtoEngine = getCarDtoEngine();
        carDtoEngine.setType("");
        oneCarDtoRequest.setEngine(carDtoEngine);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'engine.type' must not be empty", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_engineTypeIsBlank(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Engine engine = getCarDtoEngine();
        engine.setType(" ");
        oneCarDtoRequest.setEngine(engine);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'engine.type' must not be empty", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_engineTorqueIsLessThan(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Engine carDtoEngine = getCarDtoEngine();
        carDtoEngine.setTorque(-1);
        oneCarDtoRequest.setEngine(carDtoEngine);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'engine.torque' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_engineHorsepowerIsLessThan(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Engine carDtoEngine = getCarDtoEngine();
        carDtoEngine.setHorsepower(-1);
        oneCarDtoRequest.setEngine(carDtoEngine);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'engine.horsepower' must be greater than or equal to 0", response.getMessage());

    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_previousOwnerIsLessThan(){
        //Arrange
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setPreviousOwner(-1);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'previousOwner' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequestException_warrantyIsNull(){
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setWarranty(null);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals("'warranty' must not be null", response.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "null, 'warranty.basic', 'warranty.basic' must not be empty",
            "'', 'warranty.basic', 'warranty.basic' must not be empty",
            "'  ', 'warranty.basic', 'warranty.basic' must not be empty",
            "null, 'warranty.powertrain', 'warranty.powertrain' must not be empty",
            "'', 'warranty.powertrain', 'warranty.powertrain' must not be empty",
            "'  ', 'warranty.powertrain', 'warranty.powertrain' must not be empty"
    })
    void testUpdateCar_shouldThrowInvalidRequest_warrantyFields(String value, String field, String expectedMessage){
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        Warranty carDtoWarranty = getCarDtoWarranty();
        switch (field) {
            case "warranty.basic":
                if (value.equalsIgnoreCase("null")){
                    carDtoWarranty.setBasic(null);
                } else {
                    carDtoWarranty.setBasic(value);
                }
                oneCarDtoRequest.setWarranty(carDtoWarranty);
                break;
            case "warranty.powertrain":
                if (value.equalsIgnoreCase("null")){
                    carDtoWarranty.setPowertrain(null);
                } else {
                    carDtoWarranty.setPowertrain(value);
                }
                oneCarDtoRequest.setWarranty(carDtoWarranty);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + field);
        }
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        //Assert
        assertNotNull(response.getMessage());
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequest_maintenanceDatesNull(){
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setMaintenanceDates(null);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        assertNotNull(response.getMessage());
        assertEquals("'maintenanceDates' must not be null", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequest_maintenanceDatesIsEmpty(){
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setMaintenanceDates(new ArrayList<>());
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        assertNotNull(response.getMessage());
        assertEquals("'maintenanceDates' must be between 2 and 10", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequest_maintenanceDatesMax(){
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        List<LocalDate> maintenanceDates = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            maintenanceDates.add(LocalDate.now());
        }
        oneCarDtoRequest.setMaintenanceDates(maintenanceDates);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        assertNotNull(response.getMessage());
        assertEquals("'maintenanceDates' must be between 2 and 10", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowInvalidRequest_dimensionsIsNull(){
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        oneCarDtoRequest.setDimensions(null);
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        assertNotNull(response.getMessage());
        assertEquals("'dimensions' must not be empty", response.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "-100, 'dimensions.length', 'dimensions.length' must be greater than or equal to 0",
            "-100, 'dimensions.width', 'dimensions.width' must be greater than or equal to 0",
            "-100, 'dimensions.height', 'dimensions.height' must be greater than or equal to 0",
            "-100, 'dimensions.weight', 'dimensions.weight' must be greater than or equal to 0"
    })
    void testUpdateCar_shouldThrowInvalidRequest_dimensions(String value, String field, String expectedMessage){
        Car oneCar = getOneCar();
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Dimensions carDtoDimensions = getCarDtoDimensions();
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        switch (field){
            case "dimensions.length":
                carDtoDimensions.setLength(Integer.parseInt(value));
                break;
            case "dimensions.width":
                carDtoDimensions.setWidth(Integer.parseInt(value));
                break;
            case "dimensions.height":
                carDtoDimensions.setHeight(Integer.parseInt(value));
                break;
            case "dimensions.weight":
                carDtoDimensions.setWeight(Integer.parseInt(value));
                break;
            default:
                throw new IllegalArgumentException("Unexpected Value : "+field);
        }
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.of(getCarMake()));
        oneCarDtoRequest.setDimensions(carDtoDimensions);
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        assertNotNull(response.getMessage());
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowResourceNotFoundException_carMakeNotFound(){
        CarDtoRequest oneCarDtoRequest = getOneCarDto();
        Car oneCar = getOneCar();
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carMakeRepository.findByName(oneCarDtoRequest.getMake())).thenReturn(Optional.empty());
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> carServiceImpl.updateCar(1L, oneCarDtoRequest));
        assertNotNull(response.getMessage());
        assertEquals("Car Make not found with make : 'Make'", response.getMessage());
    }

    @Test
    void testDeleteCar_shouldDeleteCar(){
        //Arrange
        Car oneCar = getOneCar();
        when(carRepository.existsById(1L)).thenReturn(true);
        //Act
        String response = carServiceImpl.deleteCar(1L);
        //Assert
        verify(carRepository, times(1)).deleteById(oneCar.getId());
        assertEquals("Car with id: 1 deleted successfully", response);
    }

    @Test
    void testDeleteCar_shouldThrowResourceNotFoundException(){
        //Arrange
        when(carRepository.existsById(1L)).thenReturn(false);

        //Act
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> carServiceImpl.deleteCar(1L));
        //Assert
        assertNotNull(response);
        assertEquals("Car not found with id : '1'", response.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "''",
            "active",
            "sold",
            "archive",
            "deleted"
    })
    void testUpdateStatusCar_shouldUpdateStatusAndReturnResponse(String value) throws Exception{
        CarStatusRequest request = getCarStatusRequest();
        request.setCarStatus(CarStatus.fromValue(value));
        Car oneCar = getOneCar();
        Car savedCar = getOneCar();
        savedCar.setUpdatedAt(ZonedDateTime.now().toEpochSecond());
        savedCar.setStatus(CarStatus.fromValue(value).toString());
        when(carRepository.findById(1L)).thenReturn(Optional.of(oneCar));
        when(carRepository.save(any(Car.class))).thenReturn(savedCar);
        when(objectMapper.readValue(getFeatures(), String[].class)).thenReturn(getCarDtoFeatures());
        when(objectMapper.readValue(getEngine(), Engine.class)).thenReturn(getCarDtoEngine());
        when(objectMapper.readValue(getWarranty(), Warranty.class)).thenReturn(getCarDtoWarranty());
        when(objectMapper.readValue(getMaintenanceDates(), LocalDate[].class)).thenReturn(getCarDtoMaintenanceDates());
        when(objectMapper.readValue(getDimensions(), Dimensions.class)).thenReturn(getCarDtoDimensions());
        CarDtoResponse response = carServiceImpl.updateCarStatus(1L, request);
        assertNotNull(response);
        assertEquals(value, response.getStatus().getValue());
    }


    private List<Car> getAllCars(){
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            boolean isElectric = i % 2 == 0;
            Car car = Car.builder()
                    .id((long) i)
                    .carMake(CarMake.builder().id(1L).build())
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
                    .createdAt(ZonedDateTime.now().toEpochSecond())
                    .updatedAt(ZonedDateTime.now().toEpochSecond())
                    .status(CarStatus.ACTIVE.getValue())
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
                .carMake(CarMake.builder().id(1L).name("Make").build())
                .model("Model")
                .year(2021)
                .price(10000D)
                .isElectric(true)
                .features(getFeatures())
                .engine(getEngine())
                .previousOwner(1)
                .warranty(getWarranty())
                .maintenanceDates(getMaintenanceDates())
                .dimensions(getDimensions())
                .createdAt(ZonedDateTime.now().toEpochSecond())
                .updatedAt(null)
                .status(CarStatus.ACTIVE.getValue())
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

    private CarDtoRequest getOneCarDto(){
        return CarDtoRequest.builder()
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

    private CarMake getCarMake(){
        return CarMake.builder()
                .id(1L)
                .name("Make")
                .isActive(true)
                .createdAt(ZonedDateTime.now().toEpochSecond())
                .updatedAt(ZonedDateTime.now().toEpochSecond())
                .deletedAt(ZonedDateTime.now().toEpochSecond())
                .build();
    }

    private CarStatusRequest getCarStatusRequest(){
        return CarStatusRequest.builder()
                .carStatus(CarStatus.ACTIVE)
                .build();
    }
}