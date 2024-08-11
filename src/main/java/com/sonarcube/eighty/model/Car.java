package com.sonarcube.eighty.model;

import com.sonarcube.eighty.dto.Engine;
import com.sonarcube.eighty.dto.Warranty;
import com.sonarcube.eighty.dto.Dimensions;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "make")
    private String make;
    @Column(name = "model")
    private String model;
    @Column(name = "year")
    private int year;
    @Column(name = "price")
    private double price;
    @Column(name = "is_electric")
    private boolean isElectric;
    @Column(name = "features")
    private String features;
    @Column(name = "engine_specs")
    private String engine;
    @Column(name = "previous_owner")
    private int previousOwner;
    @Column(name = "warranty")
    private String warranty;
    @Column(name = "maintenance_dates")
    private String maintenanceDates;
    @Column(name = "dimensions")
    private String dimensions;
}
