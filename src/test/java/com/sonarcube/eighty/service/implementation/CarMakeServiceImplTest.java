package com.sonarcube.eighty.service.implementation;

import com.sonarcube.eighty.dto.CarMakeRequest;
import com.sonarcube.eighty.dto.CarMakeResponse;
import com.sonarcube.eighty.model.CarMake;
import com.sonarcube.eighty.repository.CarMakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CarMakeServiceImplTest {

    @InjectMocks
    private CarMakeServiceImpl carModelService;

    @Mock
    private CarMakeRepository carMakeRepository;

    @BeforeEach
    void setup() throws Exception{
        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    void testGetAllCarModels_shouldReturnAllCarModels(){
        //Arrange
        List<CarMake> mockCarMakes = getAllCarModels();
        when(carMakeRepository.findAll()).thenReturn(mockCarMakes);
        //Act
        List<CarMakeResponse> responses = carModelService.getAllCarModels();
        assertNotNull(responses);
        assertEquals(mockCarMakes.size(), responses.size());
        assertEquals(mockCarMakes.get(0).getName(), responses.get(0).getName());
    }

    @Test
    void testGetCarModelById_shouldReturnCarModel(){
        //Arrange
        CarMake oneCarMake = getOneCarModel();
        when(carMakeRepository.findById(1L)).thenReturn(Optional.of(oneCarMake));
        //Act
        CarMakeResponse response = carModelService.getCarModel(1L);
        //Assert
        assertNotNull(response);
        assertEquals(oneCarMake.getName(), response.getName());
    }

    @Test
    void testSaveCarModel_shouldSaveAndReturnCarModel(){
        //Arrange
        CarMakeRequest request = getCarModelRequest();
        when(carMakeRepository.save(any(CarMake.class))).thenReturn(getCarMake());
        //Act
        CarMakeResponse response = carModelService.saveCarModel(request);
        //Assert
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
    }

    @Test
    void testUpdateCarModel_shouldUpdateAndReturnCarModelResponse(){
        //Arrange
        CarMake carMake = getCarMake();
        carMake.setName("update");
        CarMakeRequest carMakeRequestUpdate = getCarModelRequest();
        carMakeRequestUpdate.setName("update");
        when(carMakeRepository.findById(1L)).thenReturn(Optional.of(carMake));
        when(carMakeRepository.findByName(carMakeRequestUpdate.getName())).thenReturn(Optional.empty());
        when(carMakeRepository.save(any(CarMake.class))).thenReturn(carMake);
        //Act
        CarMakeResponse response = carModelService.updateCarModel(1L, carMakeRequestUpdate);
        //Assert
        assertNotNull(response);
        assertEquals(carMakeRequestUpdate.getName(), response.getName());
    }

    @Test
    void testDeleteCarModel_shouldDisabledCarModel(){
        CarMake carMake = getCarMake();
        when(carMakeRepository.findById(1L)).thenReturn(Optional.of(carMake));
        String response = carModelService.deleteCarModel(1L);
        assertNotNull(response);
        assertEquals("CarModel successfully deleted!", response);
    }

    private List<CarMake> getAllCarModels(){
        List<CarMake> carMakes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CarMake oneCarMake = getOneCarModel();
            carMakes.add(oneCarMake);
        }
        return carMakes;
    }

    private CarMake getOneCarModel(){
        return  CarMake.builder()
                .id(1L)
                .name("model")
                .createdAt(ZonedDateTime.now().toEpochSecond())
                .isActive(true)
                .updatedAt(null)
                .deletedAt(null)
                .build();
    }

    private CarMakeRequest getCarModelRequest() {
        return CarMakeRequest.builder()
                .name("model")
                .build();
    }

    private CarMake getCarMake(){
        return CarMake.builder()
                .id(1L)
                .name("model")
                .isActive(true)
                .createdAt(ZonedDateTime.now().toEpochSecond())
                .updatedAt(null)
                .deletedAt(null)
                .build();
    }

}