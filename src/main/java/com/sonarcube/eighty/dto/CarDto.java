package com.sonarcube.eighty.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDto {
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
    private List<LocalDate> maintenanceDates;
    private Dimensions dimensions;
}
