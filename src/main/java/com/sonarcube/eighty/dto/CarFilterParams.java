package com.sonarcube.eighty.dto;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarFilterParams {
    private String make;
    private String model;
    private int year;
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
}
