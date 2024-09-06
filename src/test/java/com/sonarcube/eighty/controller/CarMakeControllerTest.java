package com.sonarcube.eighty.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonarcube.eighty.dto.CarMakeResponse;
import com.sonarcube.eighty.dto.ErrorDetails;
import com.sonarcube.eighty.model.CarMake;
import com.sonarcube.eighty.repository.CarMakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarMakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarMakeRepository carMakeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    List<CarMake> carMakes;

    @BeforeEach
    void setUp() {
        carMakes = carMakeRepository.findAll();
    }

    @Test
    void testGetAllCarModels_shouldReturnAllCarModels() throws Exception {
        //Arrange
        mockMvc.perform(get("/makes")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    List<CarMakeResponse> responses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(responses);
                    assertEquals(carMakes.size(), responses.size());
                });
    }

    @Test
    void testGetCarModelById_shouldReturnCarModelBasedOnId() throws Exception{
        //Arrange
        int getRandomId = new Random().nextInt(carMakes.size()-1);
        String carMakeName = carMakes.get(getRandomId).getName();
        Long id = carMakes.get(getRandomId).getId();
        mockMvc.perform(get("/makes/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //Act
                .andDo(result -> {
                    CarMakeResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    //Assert
                    assertNotNull(response);
                    assertEquals(carMakeName, response.getName());
                });
    }

    @Test
    void testSaveCarModel_shouldSaveAndReturnCarModelResponse() throws Exception{
        //Arrange
        Map<String, Object> request = carModelRequest();
        request.put("name","test");
        //Act
        mockMvc.perform(post("/makes")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isCreated())
                .andDo(result -> {
                    CarMakeResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals(request.get("name"), response.getName());
                    carMakeRepository.deleteById(response.getId());
                });
    }

    @Test
    void testSaveCarMake_shouldThrowAlreadyExists() throws Exception{
        Map<String, Object> request = carModelRequest();
        int id = new Random().nextInt(carMakes.size()-1);
        String carMakeName = carMakes.get(id).getName();
        request.put("name",carMakeName);
        mockMvc.perform(post("/makes")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("Car Make already exists with name : '" + carMakeName + "'", response.getDetails());
                });
    }

    @Test
    void testUpdateCarModel_shouldUpdateAndReturnCarModelResponse() throws Exception{
        //Arrange
        int getRandomId = new Random().nextInt(carMakes.size()-1);
        Map<String, Object> request = carModelRequest();
        request.put("name", "Updated Car Make");
        CarMake carMake = carMakes.get(getRandomId);
        String name = carMakes.get(getRandomId).getName();

        //Act
        mockMvc.perform(put("/makes/"+carMake.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isOk())
                .andDo(result -> {
                   CarMakeResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals(request.get("name"), response.getName());
                    carMake.setName(name);
                    carMakeRepository.save(carMake);
                });
    }

    @Test
    void testUpdateCar_shouldThrowAlreadyExists() throws Exception{
        int getRandomId = new Random().nextInt(carMakes.size()-1);
        String carMakeName = carMakes.get(getRandomId).getName();
        Map<String, Object> request = carModelRequest();
        request.put("name", carMakeName);
        Long id = carMakeRepository.findAll().get(0).getId();
        mockMvc.perform(put("/makes/"+id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("Car Make already exists with name : '" + carMakeName + "'", response.getDetails());
                });
    }

    @Test
    void testDeleteCarModel_shouldDisableCarModel() throws Exception{
        //Arrange
        int getRandomId = new Random().nextInt(carMakes.size()-1);
        Long id = carMakeRepository.findAll().get(getRandomId).getId();
        //Act
        mockMvc.perform(delete("/makes/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                   String response = result.getResponse().getContentAsString();
                   assertNotNull(response);
                   assertEquals("Car Make successfully deleted!", response);
                });
    }

    @Test
    void testDeleteCarMake_shouldThrowNotFoundException() throws Exception{
        long id = 12312312L;
        mockMvc.perform(delete("/makes/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("Car Make not found with id : '12312312'", response.getDetails());
                });
    }

    private Map<String, Object> carModelRequest(){
        Map<String, Object> request = new HashMap<>();
        request.put("name","MerryGo");
        return request;
    }

}