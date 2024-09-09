package com.sonarcube.eighty.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarBodyRequest {
    private String name;
}
