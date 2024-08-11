package com.sonarcube.eighty.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Engine {
    private String type;
    private int horsepower;
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
