package com.sonarcube.eighty.service.implementation;

import com.sonarcube.eighty.dto.CarMakeRequest;
import com.sonarcube.eighty.dto.CarMakeResponse;
import com.sonarcube.eighty.exception.ResourceAlreadyExistsException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
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
    private CarMakeServiceImpl carMakeService;

    @Mock
    private CarMakeRepository carMakeRepository;

    @BeforeEach
    void setup() throws Exception{
        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    void testGetAllCarMakes_shouldReturnAllCarMakes(){
        //Arrange
        List<CarMake> mockCarMakes = getAllCarMakes();
        when(carMakeRepository.findAll()).thenReturn(mockCarMakes);
        //Act
        List<CarMakeResponse> responses = carMakeService.getAllCarMakes();
        assertNotNull(responses);
        assertEquals(mockCarMakes.size(), responses.size());
        assertEquals(mockCarMakes.get(0).getName(), responses.get(0).getName());
    }

    @Test
    void testGetCarMakeById_shouldReturnAllCarMakes_withAllFieldHasValue(){
        CarMake oneCarMakes = getOneCarMakes();
        oneCarMakes.setUpdatedAt(ZonedDateTime.now().toEpochSecond());
        oneCarMakes.setDeletedAt(ZonedDateTime.now().toEpochSecond());
        when(carMakeRepository.findById(1L)).thenReturn(Optional.of(oneCarMakes));
        CarMakeResponse response = carMakeService.getCarMakes(1L);
        assertNotNull(response);
        assertEquals(oneCarMakes.getName(),response.getName());
    }

    @Test
    void testGetCarModelById_shouldReturnCarMakes(){
        //Arrange
        CarMake oneCarMake = getOneCarMakes();
        when(carMakeRepository.findById(1L)).thenReturn(Optional.of(oneCarMake));
        //Act
        CarMakeResponse response = carMakeService.getCarMakes(1L);
        //Assert
        assertNotNull(response);
        assertEquals(oneCarMake.getName(), response.getName());
    }

    @Test
    void testGetCarMakeById_shouldThrowNotFoundException(){
        when(carMakeRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> carMakeService.getCarMakes(1L));
        assertNotNull(response);
        assertEquals("Car Make not found with id : '1'", response.getMessage());
    }

    @Test
    void testSaveCarModel_shouldSaveAndReturnCarMake(){
        //Arrange
        CarMakeRequest request = getCarMakeRequest();
        when(carMakeRepository.save(any(CarMake.class))).thenReturn(getCarMake());
        //Act
        CarMakeResponse response = carMakeService.saveCarMake(request);
        //Assert
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
    }

    @Test
    void testSaveCarMake_shouldThrowResourceAlreadyExistsException(){
        //Arrange
        CarMakeRequest carMakeRequest = getCarMakeRequest();
        CarMake carMake = getCarMake();
        when(carMakeRepository.findByName(carMakeRequest.getName())).thenReturn(Optional.of(carMake));
        //Act
        ResourceAlreadyExistsException response = assertThrows(ResourceAlreadyExistsException.class, () -> carMakeService.saveCarMake(carMakeRequest));
        //Assert
        assertNotNull(response);
        assertEquals("Car Make already exists with name : 'make'", response.getMessage());
    }

    @Test
    void testUpdateCarModel_shouldUpdateAndReturnCarMakeResponse_carMakeNameNotExists(){
        //Arrange
        CarMake carMake = getCarMake();
        carMake.setName("update");
        CarMakeRequest carMakeRequestUpdate = getCarMakeRequest();
        carMakeRequestUpdate.setName("update");
        when(carMakeRepository.findById(1L)).thenReturn(Optional.of(carMake));
        when(carMakeRepository.findByName(carMakeRequestUpdate.getName())).thenReturn(Optional.empty());
        when(carMakeRepository.save(any(CarMake.class))).thenReturn(carMake);
        //Act
        CarMakeResponse response = carMakeService.updateCarMake(1L, carMakeRequestUpdate);
        //Assert
        assertNotNull(response);
        assertEquals(carMakeRequestUpdate.getName(), response.getName());
    }

    @Test
    void testUpdateCarModel_shouldUpdateAndReturnCarMakeResponse_carMakeNameIsExists(){
        //Arrange
        CarMake carMake = getCarMake();
        CarMakeRequest carMakeRequestUpdate = getCarMakeRequest();
        carMake.setName("update");
        carMakeRequestUpdate.setName("update");
        when(carMakeRepository.findById(1L)).thenReturn(Optional.of(carMake));
        when(carMakeRepository.findByName(carMakeRequestUpdate.getName())).thenReturn(Optional.of(carMake));
        when(carMakeRepository.save(any(CarMake.class))).thenReturn(carMake);
        //Act
        CarMakeResponse response = carMakeService.updateCarMake(1L, carMakeRequestUpdate);
        //Assert
        assertNotNull(response);
        assertEquals(carMakeRequestUpdate.getName(), response.getName());
    }

    @Test
    void testUpdateCarMake_shouldThrowNotFoundException(){
        CarMakeRequest carMakeRequest = getCarMakeRequest();
        when(carMakeRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> carMakeService.updateCarMake(1L, carMakeRequest));
        assertNotNull(response);
        assertEquals("Car Make not found with id : '1'", response.getMessage());
    }

    @Test
    void testUpdateCar_shouldThrowAlreadyExistsException(){
        //Arrange
        CarMake carMake = getCarMake();
        CarMake otherCarMake = getCarMake();
        CarMakeRequest carMakeRequestUpdate = getCarMakeRequest();
        carMake.setName("make");
        otherCarMake.setName("old");
        otherCarMake.setId(2L);
        carMakeRequestUpdate.setName("old");
        when(carMakeRepository.findById(1L)).thenReturn(Optional.of(carMake));
        when(carMakeRepository.findByName(carMakeRequestUpdate.getName())).thenReturn(Optional.of(otherCarMake));

        //Act
        ResourceAlreadyExistsException response = assertThrows(ResourceAlreadyExistsException.class, () -> carMakeService.updateCarMake(1L, carMakeRequestUpdate));
        //Assert
        assertNotNull(response);
        assertEquals("Car Make already exists with name : 'old'", response.getMessage());
    }

    @Test
    void testDeleteCarModel_shouldDisabledCarMake(){
        CarMake carMake = getCarMake();
        when(carMakeRepository.findById(1L)).thenReturn(Optional.of(carMake));
        String response = carMakeService.deleteCarMake(1L);
        assertNotNull(response);
        assertEquals("Car Make successfully deleted!", response);
    }

    @Test
    void testDeleteCarMake_shouldThrowNotFoundException(){
        when(carMakeRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> carMakeService.deleteCarMake(1L));
        assertNotNull(response);
        assertEquals("Car Make not found with id : '1'", response.getMessage());
    }

    private List<CarMake> getAllCarMakes(){
        List<CarMake> carMakes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CarMake oneCarMake = getOneCarMakes();
            carMakes.add(oneCarMake);
        }
        return carMakes;
    }

    private CarMake getOneCarMakes(){
        return  CarMake.builder()
                .id(1L)
                .name("make")
                .createdAt(ZonedDateTime.now().toEpochSecond())
                .isActive(true)
                .updatedAt(null)
                .deletedAt(null)
                .build();
    }

    private CarMakeRequest getCarMakeRequest() {
        return CarMakeRequest.builder()
                .name("make")
                .build();
    }

    private CarMake getCarMake(){
        return CarMake.builder()
                .id(1L)
                .name("make")
                .isActive(true)
                .createdAt(ZonedDateTime.now().toEpochSecond())
                .updatedAt(null)
                .deletedAt(null)
                .build();
    }

}