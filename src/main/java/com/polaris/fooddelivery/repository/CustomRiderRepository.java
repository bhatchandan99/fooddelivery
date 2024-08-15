package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.dto.RiderWithDistance;

import java.util.List;

public interface CustomRiderRepository {

    List<RiderWithDistance> getRidersForRestraunt(List<Double> coords, Integer distance, Integer count);

}
