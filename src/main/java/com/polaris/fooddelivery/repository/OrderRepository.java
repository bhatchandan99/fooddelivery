package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.models.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface OrderRepository extends MongoRepository<Order, ObjectId>, CustomOrderRepository {

}