package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.models.Credential;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends MongoRepository<Credential, String> {
    Optional<Credential> findByEmail(String email);

    Optional<Credential> findByPhone(String phone);

    Optional<Credential> findByEmailOrPhone(String email, String phone);


    @Query("{ 'email': ?0 }")
    void updateCredentialByEmail(String email, Credential credential);

    @Query("{ 'phone': ?0 }")
    void updateCredentialByPhone(String phone, Credential credential);
}
