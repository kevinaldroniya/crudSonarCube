package com.sonarcube.eighty.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonarcube.eighty.dto.*;
import com.sonarcube.eighty.model.Car;
import com.sonarcube.eighty.model.CarMake;
import com.sonarcube.eighty.repository.CarMakeRepository;
import com.sonarcube.eighty.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarMakeRepository carMakeRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() throws JsonProcessingException {
        carRepository.deleteAll();
        carMakeRepository.deleteAll();
        CarMake carMake = initCarMake();
        carMakeRepository.save(carMake);
        Car car = intitalizeCar();
        carRepository.save(car);
    }

    @Test
    void testGetAllCars_shouldReturnAllCars() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(get("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //Assert
                .andDo(result -> {
                    List<CarDto> cars = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    List<Car> carRepositoryAll = carRepository.findAll();
                    assertEquals(carRepositoryAll.size(), cars.size());
                    assertNotNull(cars);
                    assertEquals(carRepositoryAll.size(), cars.size());
                });
    }

    @Test
    void testGetAllCars_shouldReturnEmptyList() throws Exception {
        //Arrange
        carRepository.deleteAll();
        //Act
        mockMvc.perform(get("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //Assert
                .andDo(result -> {
                    List<CarDto> cars = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertTrue(cars.isEmpty());
                });
    }

    @Test
    void testGetAllCars_shouldReturnInternalServerError() throws Exception {
        //Arrange
        carRepository.save(badInitializeCar());
        //Act
        mockMvc.perform(get("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                   assertEquals("Resource conversion error", response.getMessage());
                });
    }

    @Test
    void testGetCarById_shouldReturnCarById() throws Exception {
        List<Car> carRepositoryAll = carRepository.findAll();
        Long carId = carRepositoryAll.get(0).getId();
        mockMvc.perform(
                get("/car/" + carId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    CarDto response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals(carId, response.getId());
                });
    }

    @Test
    void testGetCarById_shouldThrowResourceNotFoundException() throws Exception {
        //Arrange
        long carId = 99L;
        //Act
        mockMvc.perform(
                get("/car/" + carId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                //Assert
                .andExpect(status().isNotFound())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals(404, Integer.parseInt(response.getStatus()));
                    assertEquals("Car not found with id : '" + carId + "'", response.getDetails());
                });
    }

    @Test
    void testGetCarById_shouldThrowBadRequest() throws Exception {
        //Arrange
        String carId = "asd";
        //Act
        mockMvc.perform(
                get("/car/" + carId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                //Assert
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals(400,Integer.parseInt(response.getStatus()));
                });
    }

    @Test
    void testGetCarById_shouldThrowInternalServerError() throws Exception {
        //Arrange
        carRepository.deleteAll();
        carRepository.save(badInitializeCar());
        List<Car> carRepositoryAll = carRepository.findAll();
        Long id = carRepositoryAll.get(0).getId();
        //Act
        mockMvc.perform(get("/car/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                //Assert
                .andExpect(status().isInternalServerError())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("Resource conversion error", response.getMessage());
                });
    }

    @Test
    void testSaveCar_shouldReturnCarDto() throws Exception{
        //Arrange
        CarDto carDto = getOneCarDto();
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDto)))
                //assert
                .andExpect(status().isCreated())
                .andDo(result -> {
                    CarDto response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    List<Car> carList = carRepository.findAll();
                    Car car = carList.get(carList.size() - 1);
                    assertNotNull(response);
                    assertEquals(carDto.getMake(), car.getCarMake().getName());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_makeNullOrEmpty() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("make", null);
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'make' must not be empty", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_modelNullOrEmpty() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("model", null);
        //Act
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'model' must not be empty", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_yearMin() throws Exception{
        //Act
        Map<String, Object> request = carRequest();
        request.put("year",1949);
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response  = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'year' must be greater than or equal to 1950",response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_yearMax() throws Exception{
        //Act
        Map<String, Object> request = carRequest();
        request.put("year",2050);
        //Act
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response  = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'year' must be less than or equal to 2024",response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_yearType() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("year","asd");
        //Act
        mockMvc.perform(
                        post("/car")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("Invalid value provided for field 'year'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_malformedRequest() throws Exception{
        //Arrange
        String malformedJsonRequest = malformedJsonRequest();
        //Act
        mockMvc.perform(
                        post("/car")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(malformedJsonRequest))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("Your request could not be processed due to invalid input.", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_priceNullOrEmpty() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("price",null);
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals("'price' must be greater than 0", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_priceInvalidType() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("price","asd");
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("Invalid value provided for field 'price'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_priceNegativeValue() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("price",-100);
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'price' must be greater than 0",response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_featuresInvalidType() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("features", 123);
        //Act
        mockMvc.perform(
                        post("/car")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("Invalid value provided for field 'features'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_featuresSizeMin() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("features", new ArrayList<>());
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'features' size must be between 2 and 10", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_featuresSizeMax() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        List<String> features = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            features.add(String.valueOf(i));
        }
        request.put("features", features);
        //Act
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'features' size must be between 2 and 10", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_engineInvalidType() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        Map<String, Object> engineBadRequest = new HashMap<>();
        engineBadRequest.put("type","type");
        engineBadRequest.put("horsepower","asd");
        engineBadRequest.put("torque",100);
        request.put("engine", engineBadRequest);

        //Act
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("Invalid value provided for field 'engine'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_engineNull() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("engine", null);

        //Act
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'engine' must not be null", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_engineTypeNullOrEmpty() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        Map<String, Object> engineBadRequest = new HashMap<>();
        engineBadRequest.put("type","");
        engineBadRequest.put("horsepower","100");
        engineBadRequest.put("torque",100);
        request.put("engine", engineBadRequest);
        //Act
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'engine.type' must not be empty", response.getDetails());
                });
    }

    @ParameterizedTest
    @CsvSource({
            "'invalid', 'horsepower', Invalid value provided for field 'engine'. Please ensure the value is correct and of the right type.",
            "'invalid', 'torque', Invalid value provided for field 'engine'. Please ensure the value is correct and of the right type."
    })
    void testSaveCar_shouldThrowBadRequest_engineInvalid(String value, String field, String expectedMessage) throws Exception{
        Map<String, Object> request = carRequest();
        Map<String, Object> engine = new HashMap<>();
        engine.put("horsepower", 23);
        engine.put("torque",12);
        engine.put(field, value);
        request.put("engine", engine);
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                   ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                   assertNotNull(response.getError());
                   assertEquals(expectedMessage, response.getDetails());
                });

    }

    @Test
    void testSaveCar_shouldThrowBadRequest_prvOwnerInvalid() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("previousOwner", "asd");
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                   ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                   });
                   assertNotNull(response.getError());
                   assertEquals("Invalid value provided for field 'previousOwner'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_prvOwnerNegative() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("previousOwner",-1);
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                   ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                   });
                   assertNotNull(response.getError());
                   assertEquals("'previousOwner' must be greater than or equal to 0", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_warrantyEmptyObject() throws Exception {
        //Arrange
        Map<String, Object> request = carRequest();
        request.put("warranty",new HashMap<>());
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'warranty.basic' must not be empty", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_warrantyInvalidType() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("warranty","");
        //Act
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("Invalid value provided for field 'warranty'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_warrantyNull() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("warranty", null);
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'warranty' must not be null", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_maintenanceDatesNull() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("maintenanceDates",null);
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'maintenanceDates' must not be null", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_maintenanceDatesMin() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("maintenanceDates", new ArrayList<>());
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'maintenanceDates' size must be between 2 and 10", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_maintenanceDatesMax() throws Exception{
        Map<String, Object> request = carRequest();
        List<LocalDate> maintenanceDate = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            maintenanceDate.add(LocalDate.now());
        }
        request.put("maintenanceDates", maintenanceDate);
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'maintenanceDates' size must be between 2 and 10", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_maintenanceDatesInvalidType() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("maintenanceDates", List.of("1",2,"a"));
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("Invalid value provided for field 'maintenanceDates'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_dimensionsNull() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("dimensions",null);
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals("'dimensions' must not be empty", response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_dimensionsEmpty() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("dimensions", new HashMap<>());
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'dimensions' must not be empty",response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_dimensionsInvalidType() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("dimensions","as");
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response);
                    assertEquals("Invalid value provided for field 'dimensions'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @ParameterizedTest
    @CsvSource({
            "'text', 'length', Invalid value provided for field 'dimensions'. Please ensure the value is correct and of the right type.",
            "null, 'length', 'dimensions' must not be empty",
            "-1, 'length', 'dimensions' must not be empty",
            "0, 'length', 'dimensions' must not be empty",
            "null, 'width', 'dimensions' must not be empty",
            "-1, 'width', 'dimensions' must not be empty",
            "0, 'width', 'dimensions' must not be empty",
            "'text', 'width', Invalid value provided for field 'dimensions'. Please ensure the value is correct and of the right type.",
            "null, 'height', 'dimensions' must not be empty",
            "-1, 'height', 'dimensions' must not be empty",
            "0, 'height', 'dimensions' must not be empty",
            "'text', 'height', Invalid value provided for field 'dimensions'. Please ensure the value is correct and of the right type.",
            "null, 'weight', 'dimensions' must not be empty",
            "-1, 'weight', 'dimensions' must not be empty",
            "0, 'weight', 'dimensions' must not be empty",
            "'text', 'weight', Invalid value provided for field 'dimensions'. Please ensure the value is correct and of the right type."
    })
    void testSaveCar_shouldThrowBadRequest_dimensionsInvalid(String value, String field, String expectedMessage) throws Exception {
        Map<String, Object> request = carRequest();
        Map<String, Object> dimensions = new HashMap<>();
        dimensions.put("length",1);
        dimensions.put("width",1);
        dimensions.put("height",1);
        dimensions.put("weight",1);
        if (value.equalsIgnoreCase("null")){
            dimensions.put(field, null);
        }else {
            dimensions.put(field, value);
        }
        request.put("dimensions", dimensions);
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals(expectedMessage, response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_dimensionsInvalidFieldsValueNullOrLessThanZero() throws Exception{
        Map<String, Object> request = carRequest();
        Map<String, Object> dimensions = new HashMap<>();
        dimensions.put("length",null);
        dimensions.put("width",0);
        dimensions.put("height",0);
        dimensions.put("weight",0);
        request.put("dimensions", dimensions);
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'dimensions' must not be empty",response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowBadRequest_dimensionsInvalidFieldsType() throws Exception{
        Map<String, Object> request = carRequest();
        Map<String, Object> dimensions = new HashMap<>();
        dimensions.put("length","asd");
        dimensions.put("width","asd");
        dimensions.put("height","asd");
        dimensions.put("weight","asd");
        request.put("dimensions", dimensions);
        mockMvc.perform(post("/car")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("Invalid value provided for field 'dimensions'. Please ensure the value is correct and of the right type.",response.getDetails());
                });
    }

    @Test
    void testSaveCar_shouldThrowNotFoundException_whenMakeNotFound() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("make","notFound");
        mockMvc.perform(post("/car")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andDo(result -> {
                   ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                   assertNotNull(response.getError());
                   assertEquals("Car Make not found with make : 'notFound'", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldUpdateCar_returnUpdatedCarDto() throws Exception{
        //Arrange
        Map<String, Object> request = carRequest();
            request.put("make","Test");
        Long id = carRepository.findAll().get(0).getId();
        //Act
        mockMvc.perform(put("/car/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isOk())
                .andDo(result -> {
                    CarDto carDto = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(carDto.getId());
                    assertEquals(id, carDto.getId());
                    assertEquals("Test",carDto.getMake());
                });

    }

    @Test
    void testUpdateCar_shouldThrowNotFoundException() throws Exception{
        //Arrange
        long id = 12312312L;
        Map<String, Object> request = carRequest();
        request.put("make", "Test Update");
        //Act
        mockMvc.perform(put("/car/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isNotFound())
                .andDo(result -> {
                   ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                   });
                   assertNotNull(response.getError());
                   assertEquals("Car not found with id : '" + id + "'", response.getDetails());
                });
    }



    @ParameterizedTest
    @CsvSource({
            "null, 'make', 'make' must not be empty",
            "'', 'make', 'make' must not be empty",
            "' ', 'make', 'make' must not be empty",
            "null, 'model', 'model' must not be empty",
            "'', 'model', 'model' must not be empty",
            "' ', 'model', 'model' must not be empty",
            "null, 'price', 'price' must be greater than 0",
            "'asd', 'price', Invalid value provided for field 'price'. Please ensure the value is correct and of the right type.",
            "-100, 'price', 'price' must be greater than 0",
            "'asd', 'previousOwner', Invalid value provided for field 'previousOwner'. Please ensure the value is correct and of the right type.",
            "null, 'warranty', 'warranty' must not be null",
            "'asd', 'warranty', Invalid value provided for field 'warranty'. Please ensure the value is correct and of the right type.",
            "null, 'maintenanceDates', 'maintenanceDates' must not be null"
    })
    void testUpdateCar_shouldThrowBadRequest_invalidRequest(String value, String field, String expectedMessage) throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        if (value.equalsIgnoreCase("null")){
            request.put(field, null);
        }else {
            request.put(field, value);
        }
        //Act
        mockMvc.perform(put("/car/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals(expectedMessage, response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_yearMin() throws Exception{
        //Act
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("year",1949);
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response  = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'year' must be greater than or equal to 1950",response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_yearMax() throws Exception{
        //Act
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("year",2050);
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response  = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'year' must be less than or equal to 2024",response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_yearInvalidType() throws Exception {
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("year","asd");
        //Act
        mockMvc.perform(put("/car/"+id)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("Invalid value provided for field 'year'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_featuresInvalidType() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("features", 123);
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("Invalid value provided for field 'features'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_featuresSizeMin() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("features", new ArrayList<>());
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'features' size must be between 2 and 10", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_featuresSizeMax() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        List<String> features = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            features.add(String.valueOf(i));
        }
        request.put("features", features);
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'features' size must be between 2 and 10", response.getDetails());
                });
    }

    @ParameterizedTest
    @CsvSource({
            "'asd', 'engine', Invalid value provided for field 'engine'. Please ensure the value is correct and of the right type.",
            "null, 'engine', 'engine' must not be null",
            "null, 'engine.type', 'engine.type' must not be empty",
            "'', 'engine.type', 'engine.type' must not be empty",
            "' ', 'engine.type', 'engine.type' must not be empty",
    })
    void testUpdateCar_shouldThrowBadRequest_engineInvalid(String value, String field, String expectedMessage) throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        Map<String, Object> engineBadRequest = new HashMap<>();

        switch (field){
            case "engine":
                if (value.equalsIgnoreCase("null")){
                    request.put(field, null);
                }else{
                    request.put(field, value);
                }
                break;
            case "engine.type":
                if (value.equalsIgnoreCase("null")){
                    engineBadRequest.put(field, null);
                } else {
                    engineBadRequest.put(field, value);
                }
                request.put("engine",engineBadRequest);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + field);
        }

        //Act
        mockMvc.perform(put("/car/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals(expectedMessage, response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_prvOwnerNegative() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("previousOwner",-1);
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'previousOwner' must be greater than or equal to 0", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_warrantyEmptyObject() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("warranty",new HashMap<>());
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("'warranty.basic' must not be empty", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_maintenanceDatesMin() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("maintenanceDates", new ArrayList<>());
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'maintenanceDates' size must be between 2 and 10", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_maintenanceDatesMax() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        List<LocalDate> maintenanceDate = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            maintenanceDate.add(LocalDate.now());
        }
        request.put("maintenanceDates", maintenanceDate);
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'maintenanceDates' size must be between 2 and 10", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_maintenanceDatesInvalidType() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("maintenanceDates", List.of("1",2,"a"));
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("Invalid value provided for field 'maintenanceDates'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_dimensionsNull() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("dimensions",null);
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("'dimensions' must not be empty", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_dimensionsEmpty() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("dimensions", new HashMap<>());
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'dimensions' must not be empty", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_dimensionsInvalidType() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        request.put("dimensions","as");
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("Invalid value provided for field 'dimensions'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_dimensionsInvalidFieldsValueNullOrLessThanZero() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        Map<String, Object> dimensions = new HashMap<>();
        dimensions.put("length",null);
        dimensions.put("width",0);
        dimensions.put("height",0);
        dimensions.put("weight",0);
        request.put("dimensions", dimensions);
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("'dimensions' must not be empty", response.getDetails());
                });
    }

    @Test
    void testUpdateCar_shouldThrowBadRequest_dimensionsInvalidFieldsType() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        Map<String, Object> request = carRequest();
        Map<String, Object> dimensions = new HashMap<>();
        dimensions.put("length","asd");
        dimensions.put("width","asd");
        dimensions.put("height","asd");
        dimensions.put("weight","asd");
        request.put("dimensions", dimensions);
        //Act
        mockMvc.perform(put("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("Invalid value provided for field 'dimensions'. Please ensure the value is correct and of the right type.", response.getDetails());
                });
    }


    @Test
    void testUpdateCar_shouldThrowNotFoundException_whenMakeNotFound() throws Exception{
        Map<String, Object> request = carRequest();
        request.put("make","notFound");
        Long id = carRepository.findAll().get(0).getId();
        mockMvc.perform(put("/car/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response.getError());
                    assertEquals("Car Make not found with make : 'notFound'", response.getDetails());
                });
    }

    @Test
    void testDeleteCar() throws Exception{
        //Arrange
        Long id = carRepository.findAll().get(0).getId();
        //Act
        mockMvc.perform(delete("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                //Assert
                .andExpect(status().isOk())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertNotNull(response);
                    assertEquals("Car with id: " + id + " deleted successfully", response);
                });
    }

    @Test
    void testDeleteCar_shouldThrowNotFoundException() throws Exception{
        //Arrange
        long id = 12312312L;
        //Act
        mockMvc.perform(delete("/car/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                //Assert
                .andExpect(status().isNotFound())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getError());
                    assertEquals("Car not found with id : '" + id + "'", response.getDetails());
                });
    }

    private String convertFeaturesToJson() throws JsonProcessingException {
        List<String> features = List.of("Feature1", "Feature2", "Feature3");
        return objectMapper.writeValueAsString(features);
    }

    private String convertEngineToJson() throws JsonProcessingException {
        Engine engineType = Engine.builder()
                .type("EngineType")
                .horsepower(200)
                .torque(300)
                .build();
        return objectMapper.writeValueAsString(engineType);
    }

    private String convertWarrantyToJson() throws JsonProcessingException {
        Warranty warranty = Warranty.builder()
                .basic("Basic")
                .powertrain("Powertrain")
                .build();
        return objectMapper.writeValueAsString(warranty);
    }

    private String convertMaintenanceDatesToJson() throws JsonProcessingException {
        List<LocalDate> maintenanceDates = List.of(LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        return objectMapper.writeValueAsString(maintenanceDates);
    }

    private String convertDimensionsToJson() throws JsonProcessingException {
        Dimensions dimensions = Dimensions.builder()
                .length(100)
                .width(50)
                .height(30)
                .build();
        return objectMapper.writeValueAsString(dimensions);
    }

    private CarMake initCarMake(){
        return CarMake.builder()
                .name("Test")
                .createdAt(ZonedDateTime.now().toEpochSecond())
                .isActive(true)
                .updatedAt(null)
                .deletedAt(null)
                .build();
    }

    private Car intitalizeCar() throws JsonProcessingException {
        CarMake carMake = carMakeRepository.findAll().get(0);
        return Car.builder()
                //.id(1L)
                .carMake(carMake)
                .model("Model")
                .year(2021)
                .price(10000)
                .isElectric(true)
                .features(convertFeaturesToJson())
                .engine(convertEngineToJson())
                .previousOwner(1)
                .warranty(convertWarrantyToJson())
                .maintenanceDates(convertMaintenanceDatesToJson())
                .dimensions(convertDimensionsToJson())
                .build();
    }

    private Car badInitializeCar(){
        CarMake carMake = carMakeRepository.findAll().get(0);
        return Car.builder()
                .id(1L)
                .carMake(carMake)
                .model("Model")
                .year(2021)
                .price(10000)
                .isElectric(true)
                .features("Feature1,Feature2,Feature3")
                .engine("EngineType")
                .previousOwner(1)
                .warranty("Basic,Powertrain")
                .maintenanceDates("2021-08-01,2021-08-02,2021-08-03")
                .dimensions("100,50,30")
                .build();
    }

    private CarDto getOneCarDto(){
        return CarDto.builder()
                //.id(1L)
                .make("Test")
                .model("Model")
                .year(2021)
                .price(10000)
                .isElectric(true)
                .features(getFeaturesDto())
                .engine(getEngineDto())
                .previousOwner(2)
                .warranty(getWarrantyDto())
                .maintenanceDates(getMaintenanceDatesDto())
                .dimensions(getDimensionsDto())
                .build();
    }

    private List<String> getFeaturesDto(){
        return List.of("Feature 1", "Feature 2", "Feature 3");
    }

    private Engine getEngineDto(){
        return Engine.builder()
                .type("type")
                .horsepower(100)
                .torque(100)
                .build();
    }

    private Warranty getWarrantyDto(){
        return Warranty.builder()
                .basic("basic")
                .powertrain("powertrain")
                .build();
    }

    private Dimensions getDimensionsDto(){
        return Dimensions.builder()
                .width(100)
                .weight(100)
                .length(100)
                .height(100)
                .build();
    }

    private List<LocalDate> getMaintenanceDatesDto(){
        return List.of(LocalDate.now(), LocalDate.of(2020,9,19));
    }

    private Map<String, Object> carRequest(){
        Map<String, Object> engine = new HashMap<>();
        engine.put("type","type");
        engine.put("horsepower",100);
        engine.put("torque",100);

        Map<String, Object> warranty = new HashMap<>();
        warranty.put("basic","basic");
        warranty.put("powertrain","powertrain");

        Map<String, Object> dimensions = new HashMap<>();
        dimensions.put("length", 1);
        dimensions.put("width", 1);
        dimensions.put("height", 1);
        dimensions.put("weight", 1);

        Map<String, Object> carRequest = new HashMap<>();
        carRequest.put("make", "Test");
        carRequest.put("model", "Model");
        carRequest.put("year", 2000);
        carRequest.put("price", 100000);
        carRequest.put("isElectric", true);
        carRequest.put("features", List.of("Feature 1", "Feature 2", "Feature 3"));
        carRequest.put("engine", engine);
        carRequest.put("previousOwner",2);
        carRequest.put("warranty",warranty);
        carRequest.put("dimensions", dimensions);
        carRequest.put("maintenanceDates", getMaintenanceDatesDto());
        return carRequest;
    }

    private String malformedJsonRequest(){
       return """
               {
                   "id": null,
                   "make": "Test",
                   "model": "Model",
                   "year": 2024,
                   "price": 100000.0,
                   "features": [
                       "Feature1",
                       "Feature2",
                       "Feature3"
                   ],
                   "engine": {
                       "type": "EngineType",
                       "horsepower": 100,
                       "torque": 300
                   },
                   "previousOwner": 1,
                   "warranty": {
                       "basic": "Basic",
                       "powertrain": "Powertrain"
                   },
                   "maintenanceDates": [
                       "2024-08-12",
                       "2024-08-12"
                   ],
                   "dimensions": {
                       "length": 100,
                       "width": 100,
                       "height": 30,
                       "weight": 10
                   },
                   "electric": true
               """;
    }
}