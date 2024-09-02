package com.sonarcube.eighty.dto;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarMakeResponse {
    private Long id;
    private String name;
    private boolean isActive;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime deletedAt;
}
