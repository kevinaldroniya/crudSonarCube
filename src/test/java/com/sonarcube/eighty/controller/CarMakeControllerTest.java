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
        mockMvc.perform(get("/makes")
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
        mockMvc.perform(get("/makes/"+id)
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
                });
    }

    @Test
    void testSaveCarMake_shouldThrowAlreadyExists() throws Exception{
        Map<String, Object> request = carModelRequest();
        request.put("name","Panamera");
        mockMvc.perform(post("/makes")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    ErrorDetails response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertNotNull(response);
                    assertEquals("Car Make already exists with name : 'Panamera'", response.getDetails());
                });
    }

    @Test
    void testUpdateCarModel_shouldUpdateAndReturnCarModelResponse() throws Exception{
        //Arrange
        Long id = carMakeRepository.findAll().get(0).getId();
        Map<String, Object> request = carModelRequest();
        request.put("name", "CX3");
        //Act
        mockMvc.perform(put("/makes/"+id)
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
    void testUpdateCar_shouldThrowAlreadyExists() throws Exception{
        CarMake carMake = initializeCarModel();
        carMake.setName("Already");
        carMakeRepository.save(carMake);
        Map<String, Object> request = carModelRequest();
        request.put("name","Already");
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
                    assertEquals("Car Make already exists with name : 'Already'", response.getDetails());
                });
    }

    @Test
    void testDeleteCarModel_shouldDisableCarModel() throws Exception{
        //Arrange
        Long id = carMakeRepository.findAll().get(0).getId();
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