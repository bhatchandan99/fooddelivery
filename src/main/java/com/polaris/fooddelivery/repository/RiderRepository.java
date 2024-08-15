package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.models.Credential;
import com.polaris.fooddelivery.models.Rider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface RiderRepository extends MongoRepository<Rider, String>, CustomRiderRepository {
    Optional<Rider> findByEmail(String email);

    Optional<Rider> findByPhone(String phone);

    Optional<Rider> findByEmailOrPhone(String email, String phone);


    @Query("{ 'email': ?0 }")
    void updateRiderByEmail(String email, Credential credential);

    @Query("{ 'phone': ?0 }")
    void updateRiderByPhone(String phone, Credential credential);

}
