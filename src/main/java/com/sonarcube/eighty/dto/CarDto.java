package com.sonarcube.eighty.dto;

import com.sonarcube.eighty.util.annotation.NotEmptyDimensions;
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
@Builder
public class CarDto {
    @Nullable
    private Long id;

    @NotEmpty
    private String make;

    @NotEmpty
    private String model;

    @Max(value = 2024)
    @Min(value = 1950)
    private int year;

    @Positive
    private double price;

    private boolean isElectric;

    @Size(min = 2, max = 10)
    private List<@NotNull String> features;

    @Valid
    @NotNull
    private Engine engine;

    @Min(value = 0)
    private int previousOwner;

    @Valid
    @NotNull
    private Warranty warranty;

    @NotNull
    @Size(min = 2, max = 10)
    private List<@NotNull LocalDate> maintenanceDates;

    @Valid
    @NotEmptyDimensions
    private Dimensions dimensions;
}
