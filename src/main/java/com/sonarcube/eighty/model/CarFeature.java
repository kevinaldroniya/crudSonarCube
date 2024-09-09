package com.sonarcube.eighty.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@Entity
@Table(name = "car_feature")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CarFeature extends CarModelFieldTemplate{
    private String feature;
}
