package com.sonarcube.eighty.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarBodyResponse {
    private Long id;
    private String name;
    private boolean isActive;
    private Long createdAt;
    private Long updatedAt;
    private Long deletedAt;
}
