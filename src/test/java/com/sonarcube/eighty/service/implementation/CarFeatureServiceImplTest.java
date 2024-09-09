package com.sonarcube.eighty.service.implementation;

import com.sonarcube.eighty.dto.CarFeatureResponse;
import com.sonarcube.eighty.exception.ResourceAlreadyExistsException;
import com.sonarcube.eighty.exception.ResourceNotFoundException;
import com.sonarcube.eighty.model.CarFeature;
import com.sonarcube.eighty.repository.CarFeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CarFeatureServiceImplTest {
    @InjectMocks
    private CarFeatureServiceImpl carFeatureService;

    @Mock
    private CarFeatureRepository carFeatureRepository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    void testGetAllCarFeatures_shouldReturnCarFeatureResponseList() {
        // Arrange
        List<CarFeature> mockCarFeature = getAllCarFeatures();
        when(carFeatureRepository.findAll()).thenReturn(mockCarFeature);
        // Act
        List<CarFeatureResponse> responses = carFeatureService.getAllCarFeatures();
        // Assert
        assertNotNull(responses);
        assertEquals(mockCarFeature.size(), responses.size());
    }

    @Test
    void testGetCarFeatureById_shouldReturnCarFeatureResponse(){
        //Arrange
        CarFeature mockCarFeature = getCarFeature();
        when(carFeatureRepository.findById(1L)).thenReturn(Optional.of(mockCarFeature));
        //Act
        CarFeatureResponse response = carFeatureService.getCarFeatureById(1L);
        //Assert
        assertNotNull(response);
        assertEquals(mockCarFeature.getFeature(), response.getFeature());
    }

    @Test
    void testGetCarById_shouldThrowNotFoundException(){
        //Arrange
        String expectedMessage = "Car Feature not found with id : '1'";
        when(carFeatureRepository.findById(1L)).thenReturn(Optional.empty());
        //Act
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> carFeatureService.getCarFeatureById(1L));
        //Assert
        assertNotNull(response);
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void testCreateCarFeature_shouldReturnCarFeatureResponse(){
        //Arrange
        int randomIndex = new Random().nextInt(getFeatureData().length);
        String carFeatureRequest = getFeatureData()[randomIndex];
        CarFeature carFeature = getCarFeature();
        carFeature.setFeature(carFeatureRequest);
        when(carFeatureRepository.findByFeature(carFeatureRequest)).thenReturn(Optional.empty());
        when(carFeatureRepository.save(any(CarFeature.class))).thenReturn(carFeature);
        //Act
        CarFeatureResponse response = carFeatureService.createCarFeature(carFeatureRequest);
        //Assert
        assertNotNull(response);
        assertEquals(carFeatureRequest, response.getFeature());
    }

    @Test
    void testCreateFeature_shouldThrowAlreadyException(){
        //Arrange
        int randomIndex = new Random().nextInt(getFeatureData().length);
        String carFeatureRequest = getFeatureData()[randomIndex];
        String expectedMessage = "Car Feature already exists with feature : '" + carFeatureRequest + "'";
        when(carFeatureRepository.findByFeature(carFeatureRequest)).thenReturn(Optional.of(getCarFeature()));
        //Act
        ResourceAlreadyExistsException response = assertThrows(ResourceAlreadyExistsException.class, () -> carFeatureService.createCarFeature(carFeatureRequest));
        //Assert
        assertNotNull(response);
        assertEquals(expectedMessage, response.getMessage());
    }


    private List<CarFeature> getAllCarFeatures() {
        List<CarFeature> carFeatures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CarFeature carFeature = getCarFeature();
            carFeature.setId((long) i+1);
            carFeature.setFeature(getFeatureData()[i]);
            carFeatures.add(carFeature);
        }
        return carFeatures;
    }

    private CarFeature getCarFeature() {
        String[] featuresData = getFeatureData();
        int randomIndex = new Random().nextInt(featuresData.length);
        return CarFeature.builder()
                .id(1L)
                .feature(featuresData[randomIndex])
                .isActive(true)
                .createdAt(ZonedDateTime.now().toEpochSecond())
                .updatedAt(ZonedDateTime.now().toEpochSecond())
                .deletedAt(ZonedDateTime.now().toEpochSecond())
                .build();
    }

    private String[] getFeatureData() {
        return new String[]{
                "Bluetooth",
                "GPS",
                "Airbags",
                "ABS",
                "Cruise Control",
                "Parking Sensors",
                "Rear Camera",
                "Sunroof",
                "Leather Seats",
                "Alloy Wheels"
        };
    }


}