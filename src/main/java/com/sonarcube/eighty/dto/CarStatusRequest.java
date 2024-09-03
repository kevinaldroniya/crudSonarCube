package com.sonarcube.eighty.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CarStatusRequest {
    private CarStatus carStatus;
}
