package com.sonarcube.eighty.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "car")
@ToString
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    @Column(name = "created_at")
    private Long createdAt;
    @Column(name = "updated_at")
    private Long updatedAt;
    @Column(name = "status")
    private String status;
    @ManyToOne
    @JoinColumn(name = "make_id", nullable = false)
    private CarMake carMake;
}
