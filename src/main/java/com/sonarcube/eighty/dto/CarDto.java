package com.sonarcube.eighty.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @Nullable
    private Long id;
    private String make;
    private String model;
    @Max(value = 2024)
    private int year;
    @Positive
    private double price;
    private boolean isElectric;
    @NotEmpty
    private List<String> features;
    @Valid
    @NotNull
    private Engine engine;
    @Min(value = 0)
    private int previousOwner;
    @Valid
    @NotNull
    private Warranty warranty;
    @NotEmpty
    @NotNull
    private List<LocalDate> maintenanceDates;
    @Valid
    @NotNull
    private Dimensions dimensions;
}
