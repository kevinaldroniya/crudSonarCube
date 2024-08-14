package com.sonarcube.eighty.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonarcube.eighty.dto.CarDto;
import com.sonarcube.eighty.exception.InvalidRequestException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import com.sonarcube.eighty.model.Car;
import com.sonarcube.eighty.dto.Dimensions;
import com.sonarcube.eighty.dto.Engine;
import com.sonarcube.eighty.dto.Warranty;
import com.sonarcube.eighty.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
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
        CarDto response = carServiceImpl.saveCar(getOneCarDto());
        //Assert
        assertNotNull(response);
        assertEquals(oneCarDto.getId(), response.getId());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_makeEmpty(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setMake("");
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'make' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_makeBlank(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setMake("   ");
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'make' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_modelEmpty(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setModel("");
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'model' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_modelBlank(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setModel("  ");
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'model' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_yearLessThan(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setYear(1949);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'year' must be greater than or equal to 1950", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_yearGreaterThan(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setYear(2025);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'year' must be less than or equal to 2024", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_priceNegative(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setPrice(-1);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'price' must be greater than 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_featuresEmptyArray(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setFeatures(new ArrayList<>());
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'features' size must be between 2 and 10", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_featuresMaxArray(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        List<String> features = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            features.add(String.valueOf(i));
        }
        oneCarDto.setFeatures(features);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'features' size must be between 2 and 10", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineNull(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setEngine(null);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'engine' must not be null",response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineTypeIsEmpty(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Engine engine = Engine.builder()
                .torque(100)
                .horsepower(100)
                .type("")
                .build();
        oneCarDto.setEngine(engine);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'engine.type' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineTypeIsBlank(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Engine engine = Engine.builder()
                .torque(100)
                .horsepower(100)
                .type("  ")
                .build();
        oneCarDto.setEngine(engine);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'engine.type' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineTorqueIsLessThanZero(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Engine engine = Engine.builder()
                .torque(-100)
                .horsepower(100)
                .type("type")
                .build();
        oneCarDto.setEngine(engine);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'engine.torque' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_engineHorsepowerIsLessThanZero(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Engine engine = Engine.builder()
                .torque(100)
                .horsepower(-100)
                .type("type")
                .build();
        oneCarDto.setEngine(engine);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'engine.horsepower' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_previousOwnerLessThanZero(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setPreviousOwner(-100);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'previousOwner' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_warrantyNull(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setWarranty(null);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'warranty' must not be null", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_warrantyBasicNull(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Warranty warranty = Warranty.builder()
                .basic(null)
                .powertrain("pwt")
                .build();
        oneCarDto.setWarranty(warranty);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'warranty.basic' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_warrantyBasicIsEmpty(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Warranty warranty = Warranty.builder()
                .basic("")
                .powertrain("pwt")
                .build();
        oneCarDto.setWarranty(warranty);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'warranty.basic' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_warrantyBasicIsBlank(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Warranty warranty = Warranty.builder()
                .basic(" ")
                .powertrain("pwt")
                .build();
        oneCarDto.setWarranty(warranty);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'warranty.basic' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_warrantyPowerTrainNull(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Warranty warranty = Warranty.builder()
                .basic("basic")
                .powertrain(null)
                .build();
        oneCarDto.setWarranty(warranty);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'warranty.powertrain' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_warrantyPowertrainIsEmpty(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Warranty warranty = Warranty.builder()
                .basic("basic")
                .powertrain("")
                .build();
        oneCarDto.setWarranty(warranty);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'warranty.powertrain' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_warrantyPowertrainIsBlank(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Warranty warranty = Warranty.builder()
                .basic("basic")
                .powertrain("  ")
                .build();
        oneCarDto.setWarranty(warranty);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        //Assert
        assertEquals("'warranty.powertrain' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsNull(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        oneCarDto.setDimensions(null);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        assertEquals("'dimensions' must not be empty", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsLengthLessThanZero(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Dimensions dimensions = Dimensions.builder()
                .length(-100)
                .height(100)
                .weight(100)
                .width(100)
                .build();
        oneCarDto.setDimensions(dimensions);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        assertEquals("'dimensions.length' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsWidthLessThanZero(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Dimensions dimensions = Dimensions.builder()
                .width(-100)
                .build();
        oneCarDto.setDimensions(dimensions);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        assertEquals("'dimensions.width' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsHeightLessThanZero(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Dimensions dimensions = Dimensions.builder()
                .height(-100)
                .build();
        oneCarDto.setDimensions(dimensions);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
        assertEquals("'dimensions.height' must be greater than or equal to 0", response.getMessage());
    }

    @Test
    void testSaveCar_shouldThrowInvalidRequestException_dimensionsWeightLessThanZero(){
        //Arrange
        CarDto oneCarDto = getOneCarDto();
        Dimensions dimensions = Dimensions.builder()
                .weight(-100)
                .build();
        oneCarDto.setDimensions(dimensions);
        //Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));
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
        CarDto oneCarDto = getOneCarDto();
        switch (field) {
            case "make":
                oneCarDto.setMake(value);
                break;
            case "model":
                oneCarDto.setModel(value);
                break;
            case "year":
                oneCarDto.setYear(Integer.parseInt(value));
                break;
            case "price":
                oneCarDto.setPrice(Double.parseDouble(value));
                break;
            case "features":
                oneCarDto.setFeatures(Collections.emptyList());
                break;
            case "engine":
                oneCarDto.setEngine(null);
                break;
            case "engine.type":
                if (oneCarDto.getEngine() == null) {
                    oneCarDto.setEngine(Engine.builder().type(value).build());
                } else {
                    oneCarDto.getEngine().setType(value);
                }
                break;
            case "engine.torque":
                if (oneCarDto.getEngine() == null) {
                    oneCarDto.setEngine(Engine.builder().torque(Integer.parseInt(value)).build());
                } else {
                    oneCarDto.getEngine().setTorque(Integer.parseInt(value));
                }
                break;
            case "engine.horsepower":
                if (oneCarDto.getEngine() == null) {
                    oneCarDto.setEngine(Engine.builder().horsepower(Integer.parseInt(value)).build());
                } else {
                    oneCarDto.getEngine().setHorsepower(Integer.parseInt(value));
                }
                break;
            case "previousOwner":
                oneCarDto.setPreviousOwner(Integer.parseInt(value));
                break;
            case "warranty":
                oneCarDto.setWarranty(null);
                break;
            case "warranty.basic":
                if (oneCarDto.getWarranty() == null) {
                    oneCarDto.setWarranty(Warranty.builder().basic(value).build());
                } else {
                    if (value.equals("null")){
                        oneCarDto.getWarranty().setBasic(null);
                    } else {
                        oneCarDto.getWarranty().setBasic(value);
                    }
                }
                break;
            case "warranty.powertrain":
                if (oneCarDto.getWarranty() == null) {
                    oneCarDto.setWarranty(Warranty.builder().powertrain(value).build());
                } else {
                    if (value.equalsIgnoreCase("null")){
                        oneCarDto.getWarranty().setPowertrain(null);
                    }else {
                        oneCarDto.getWarranty().setPowertrain(value);
                    }
                }
                break;
            case "dimensions":
                oneCarDto.setDimensions(null);
                break;
            case "dimensions.length":
                if (oneCarDto.getDimensions() == null) {
                    oneCarDto.setDimensions(Dimensions.builder().length(Integer.parseInt(value)).build());
                } else {
                    oneCarDto.getDimensions().setLength(Integer.parseInt(value));
                }
                break;
            case "dimensions.width":
                if (oneCarDto.getDimensions() == null) {
                    oneCarDto.setDimensions(Dimensions.builder().width(Integer.parseInt(value)).build());
                } else {
                    oneCarDto.getDimensions().setWidth(Integer.parseInt(value));
                }
                break;
            case "dimensions.height":
                if (oneCarDto.getDimensions() == null) {
                    oneCarDto.setDimensions(Dimensions.builder().height(Integer.parseInt(value)).build());
                } else {
                    oneCarDto.getDimensions().setHeight(Integer.parseInt(value));
                }
                break;
            case "dimensions.weight":
                if (oneCarDto.getDimensions() == null) {
                    oneCarDto.setDimensions(Dimensions.builder().weight(Integer.parseInt(value)).build());
                } else {
                    oneCarDto.getDimensions().setWeight(Integer.parseInt(value));
                }
                break;
        }
        // Act
        InvalidRequestException response = assertThrows(InvalidRequestException.class, () -> carServiceImpl.saveCar(oneCarDto));

        // Assert
        assertEquals(expectedMessage, response.getMessage());
    }


    private List<Car> getAllCars(){
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