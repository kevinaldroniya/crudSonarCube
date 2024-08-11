package com.sonarcube.eighty.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonarcube.eighty.dto.*;
import com.sonarcube.eighty.model.Car;
import com.sonarcube.eighty.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

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
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() throws JsonProcessingException {
        carRepository.deleteAll();
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
        String carId = generateAlphanumericString(2);
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

    private Car intitalizeCar() throws JsonProcessingException {
        return Car.builder()
                .id(1L)
                .make("Make")
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
        return Car.builder()
                .id(1L)
                .make("Make")
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

    private static String generateAlphanumericString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return stringBuilder.toString();
    }
}