package com.sonarcube.eighty.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Warranty {

    @NotEmpty
    private String basic;

    @NotEmpty
    private String powertrain;

    @Override
    public String toString() {
        return "{" +
                "\"basic\":\"" + basic + "\"" +
                ", \"powertrain\":\"" + powertrain + "\"" +
                '}';
    }
}
