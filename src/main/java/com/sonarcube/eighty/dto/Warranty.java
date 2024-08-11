package com.sonarcube.eighty.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Warranty {
    private String basic;
    private String powertrain;

    @Override
    public String toString() {
        return "{" +
                "\"basic\":\"" + basic + "\"" +
                ", \"powertrain\":\"" + powertrain + "\"" +
                '}';
    }
}
