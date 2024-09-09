package com.sonarcube.eighty.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "car_body_style")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
public class CarBodyStyle extends CarModelFieldTemplate{
    @Column(name = "name")
    private String name;
}
