package com.polaris.fooddelivery.dto;

import com.polaris.fooddelivery.models.Outlet;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
//@Builder
@Document("outlet")


public class OutletWithDistance extends Outlet {
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