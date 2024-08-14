package com.sonarcube.eighty.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dimensions {
    private int length;
    private int width;
    private int height;
    private int weight;

    @Override
    public String toString() {
        return "{" +
                "\"length\":" + length +
                ", \"width\":" + width +
                ", \"height\":" + height +
                ", \"weight\":" + weight +
                '}';
    }
}
