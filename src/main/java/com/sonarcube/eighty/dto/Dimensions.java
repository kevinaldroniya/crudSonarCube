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
public class Dimensions {
    @Min(value = 0)
    @NotNull
    private int length;
    @Min(value = 0)
    private int width;
    @Min(value = 0)
    private int height;
    @Min(value = 0)
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
