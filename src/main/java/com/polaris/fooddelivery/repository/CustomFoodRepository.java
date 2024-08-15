package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.models.Food;
import org.springframework.data.util.Pair;

import java.util.List;

public interface CustomFoodRepository {

    //    List<Order> getOrdersByCustomerId(String customerId, Integer pageNumber, Integer pageSize);
//
    Pair<Long, List<Food>> getFoodFromOutletId(String riderId, Boolean isEnabled, Integer pageNumber, Integer pageSize, String sortKey);

    //
    List<Food> getFoodFromIds(List<String> foodIds);


}
