package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.models.Outlet;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

//public interface OutletRepository extends MongoRepository<Outlet, ObjectId>, CustomOutletRepository {

public interface OutletRepository extends MongoRepository<Outlet, ObjectId>, CustomOutletRepository {

    Optional<Outlet> findByEmailOrPhone(String email, String phone);
}