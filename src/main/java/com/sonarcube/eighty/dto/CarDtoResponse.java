package com.sonarcube.eighty.dto;


import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CarDtoResponse {
    private Long id;
    private String make;
    private String model;
    private int year;
    private double price;
    private boolean isElectric;
    private List<String> features;
    private Engine engine;
    private int previousOwner;
    private Warranty warranty;
    private Dimensions dimensions;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<LocalDate> maintenanceDates;
    private CarStatus status;
}
