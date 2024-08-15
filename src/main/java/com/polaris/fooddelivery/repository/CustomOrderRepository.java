package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.models.Order;
import org.springframework.data.util.Pair;

import java.util.List;

public interface CustomOrderRepository {

    List<Order> getOrdersByCustomerId(String customerId, Integer pageNumber, Integer pageSize);

    Pair<Long, List<Order>> getOrdersByRiderId(String riderId, Boolean isCompleted, Integer pageNumber, Integer pageSize);


}
