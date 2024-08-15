package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.dto.OutletWithDistance;
import com.polaris.fooddelivery.models.Outlet;

import java.util.List;

public interface CustomOutletRepository {

    Outlet getOutletById(String orderId);

    List<OutletWithDistance> getOutletByCustomerId(String city, List<Double> coords);

}
