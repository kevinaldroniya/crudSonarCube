//package com.sonarcube.eighty.repository;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sonarcube.eighty.config.ObjectMapperConfig;
//import com.sonarcube.eighty.dto.Dimensions;
//import com.sonarcube.eighty.dto.Engine;
//import com.sonarcube.eighty.dto.Warranty;
//import com.sonarcube.eighty.model.Car;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
//import org.springframework.context.annotation.Import;
//
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@Import(ObjectMapperConfig.class)
//class CarRepositoryTest {
//
//    @Autowired
//    private CarRepository carRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//
//    @BeforeEach
//    void setUp() throws JsonProcessingException {
//        carRepository.deleteAll();
//        Car car = intitalizeCar();
//        carRepository.save(car);
//    }
//
//    @Test
//    void testFindAll(){
//        List<Car> carRepositoryAll = carRepository.findAll();
//        assertNotNull(carRepositoryAll);
//        assertEquals(1, carRepositoryAll.size());
//    }
//
//    @Test
//    void testFindById(){
//        Car car = carRepository.findAll().get(0);
//        Optional<Car> optionalCar = carRepository.findById(car.getId());
//        assertTrue(optionalCar.isPresent());
//        assertEquals(car.getMake(), optionalCar.get().getMake());
//    }
//
//    @Test
//    void testSave() throws JsonProcessingException {
//        Car car = intitalizeCar();
//        Car save = carRepository.save(car);
//        assertNotNull(save.getId());
//        assertEquals("Make",save.getMake());
//    }
//
//    @Test
//    void testDelete(){
//        Car car = carRepository.findAll().get(0);
//        carRepository.delete(car);
//        Optional<Car> optionalCar = carRepository.findById(car.getId());
//        assertFalse(optionalCar.isPresent());
//    }
//
//    @Test
//    void testDeleteById(){
//        Car car = carRepository.findAll().get(0);
//        carRepository.deleteById(car.getId());
//        Optional<Car> optionalCar = carRepository.findById(car.getId());
//        assertFalse(optionalCar.isPresent());
//    }
//
//    private Car intitalizeCar() throws JsonProcessingException {
//        return Car.builder()
//                //.id(1L)
//                .make("Make")
//                .model("Model")
//                .year(2021)
//                .price(10000)
//                .isElectric(true)
//                .features(convertFeaturesToJson())
//                .engine(convertEngineToJson())
//                .previousOwner(1)
//                .warranty(convertWarrantyToJson())
//                .maintenanceDates(convertMaintenanceDatesToJson())
//                .dimensions(convertDimensionsToJson())
//                .build();
//    }
//
//    private String convertFeaturesToJson() throws JsonProcessingException {
//        List<String> features = List.of("Feature1", "Feature2", "Feature3");
//        return objectMapper.writeValueAsString(features);
//    }
//
//    private String convertEngineToJson() throws JsonProcessingException {
//        Engine engineType = Engine.builder()
//                .type("EngineType")
//                .horsepower(200)
//                .torque(300)
//                .build();
//        return objectMapper.writeValueAsString(engineType);
//    }
//
//    private String convertWarrantyToJson() throws JsonProcessingException {
//        Warranty warranty = Warranty.builder()
//                .basic("Basic")
//                .powertrain("Powertrain")
//                .build();
//        return objectMapper.writeValueAsString(warranty);
//    }
//
//    private String convertMaintenanceDatesToJson() throws JsonProcessingException {
//        List<LocalDate> maintenanceDates = List.of(LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
//        return objectMapper.writeValueAsString(maintenanceDates);
//    }
//
//    private String convertDimensionsToJson() throws JsonProcessingException {
//        Dimensions dimensions = Dimensions.builder()
//                .length(100)
//                .width(50)
//                .height(30)
//                .build();
//        return objectMapper.writeValueAsString(dimensions);
//    }
//
//}