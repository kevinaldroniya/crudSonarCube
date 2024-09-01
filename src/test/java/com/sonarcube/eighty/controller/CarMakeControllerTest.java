package com.sonarcube.eighty.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonarcube.eighty.dto.CarMakeResponse;
import com.sonarcube.eighty.model.CarMake;
import com.sonarcube.eighty.repository.CarMakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @BeforeEach
    void setUp(){
        carMakeRepository.deleteAll();
        CarMake carMake = initializeCarModel();
        carMakeRepository.save(carMake);
    }

    @Test
    void testGetAllCarModels_shouldReturnAllCarModels() throws Exception {
        //Arrange
        mockMvc.perform(get("/models")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    List<CarMakeResponse> responses = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(responses);
                    assertEquals("Panamera", responses.get(0).getName());
                });
    }

    @Test
    void testGetCarModelById_shouldReturnCarModelBasedOnId() throws Exception{
        //Arrange
        Long id = carMakeRepository.findAll().get(0).getId();
        mockMvc.perform(get("/models/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //Act
                .andDo(result -> {
                    CarMakeResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    //Assert
                    assertNotNull(response);
                    assertEquals("Panamera", response.getName());
                });
    }

    @Test
    void testSaveCarModel_shouldSaveAndReturnCarModelResponse() throws Exception{
        //Arrange
        Map<String, Object> request = carModelRequest();
        //Act
        mockMvc.perform(post("/models")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isCreated())
                .andDo(result -> {
                    CarMakeResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals(request.get("name"), response.getName());
                });
    }

    @Test
    void testUpdateCarModel_shouldUpdateAndReturnCarModelResponse() throws Exception{
        //Arrange
        Long id = carMakeRepository.findAll().get(0).getId();
        Map<String, Object> request = carModelRequest();
        request.put("name", "CX3");
        //Act
        mockMvc.perform(put("/models/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //Assert
                .andExpect(status().isOk())
                .andDo(result -> {
                   CarMakeResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals(request.get("name"), response.getName());
                });
    }

    @Test
    void testDeleteCarModel_shouldDisableCarModel() throws Exception{
        //Arrange
        Long id = carMakeRepository.findAll().get(0).getId();
        //Act
        mockMvc.perform(delete("/models/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                   String response = result.getResponse().getContentAsString();
                   assertNotNull(response);
                   assertEquals("CarModel successfully deleted!", response);
                });
    }

    private CarMake initializeCarModel() {
        return CarMake.builder()
//                .id(1L)
                .name("Panamera")
                .isActive(true)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond())
                .updatedAt(null)
                .deletedAt(null)
                .build();
    }

    private Map<String, Object> carModelRequest(){
        Map<String, Object> request = new HashMap<>();
        request.put("name","Panamera");
        return request;
    }

}