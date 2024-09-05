package com.sonarcube.eighty.dto;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarFilterParams {
    private String make;
    private String model;
    private int year;
    private String status;
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;


    public void setCarStatus(String status) {
        boolean isStatus;
        try {
            CarStatus.fromValue(status);
            isStatus = true;
        } catch (IllegalArgumentException e) {
            isStatus = false;
        }
        if (!isStatus) {
            this.status = CarStatus.ACTIVE.getValue(); // default value
        } else {
            this.status = CarStatus.fromValue(status).getValue();
        }
    }


    public void setSortBy(String sortBy) {
        boolean isAttribute;
        try {
            CarDtoResponse.class.getDeclaredField(sortBy);
            isAttribute = true;
        } catch (NoSuchFieldException e) {
            isAttribute = false;
        }

        // if the value is not attribute of the CarDtoResponse class, it will be set to default value
        if (!isAttribute) {
            this.sortBy = "id"; // default value
        } else {
            this.sortBy = sortBy;
        }
    }

    public void setSortDirection(String sortDirection) {
        boolean isDirection;
        isDirection = sortDirection.equalsIgnoreCase("ASC") || sortDirection.equalsIgnoreCase("DESC");
        if (!isDirection) {
            this.sortDirection = "ASC"; // default value
        } else {
            this.sortDirection = sortDirection;
        }
    }

    public void setYear(String year) {
        if (year == null || year.isEmpty()) {
            this.year = 0; // default value
        } else {
            try {
                this.year = Integer.parseInt(year);
            } catch (NumberFormatException e) {
                this.year = 0; // default value in case of format error
            }
        }
    }

    public void setPage(String page) {
        if (page == null || page.isEmpty()) {
            this.page = 0; // default value
        } else {
            try {
                this.page = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                this.page = 0; // default value in case of format error
            }
        }
    }

    public void setSize(String size) {
        if (size == null || size.isEmpty()) {
            this.size = 10; // default value
        } else {
            try {
                this.size = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                this.size = 10; // default value in case of format error
            }
        }
    }
}
