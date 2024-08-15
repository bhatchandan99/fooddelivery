package com.polaris.fooddelivery.dto;


import com.polaris.fooddelivery.models.Rider;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@Builder
//@Document("outlet")
public class RiderWithDistance extends Rider {
    private Double distance;
//    private Integer matchCount;

    public Double getDistance() {
        return distance;
    }

    public Double setDistance(final double distance) {
        this.distance = distance;
        return distance;
    }
}