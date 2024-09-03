package com.sonarcube.eighty.dto;

import com.sonarcube.eighty.exception.ResourceConversionException;

public enum CarStatus {
    ACTIVE("active"),
    SOLD("sold"),
    ARCHIVE("archive"),
    DELETED("deleted");

    private final String value;

    CarStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CarStatus fromValue(String value) {
        for (CarStatus status : CarStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new ResourceConversionException("Car","CarDtoResponse");
    }
}