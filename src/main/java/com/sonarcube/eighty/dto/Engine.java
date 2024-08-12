package com.sonarcube.eighty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Engine {
    @NotNull
    @NotEmpty
    private String type;
    @Min(value = 0)
    private int horsepower;
    @Min(value = 0)
    private int torque;

    @Override
    public String toString() {
        return "{" +
                "\"type\":\"" + type + "\"" +
                ", \"horsepower\":" + horsepower +
                ", \"torque\":" + torque +
                '}';
    }
}
