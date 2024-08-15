package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findById(Integer userId);

    Optional<User> findByEmail(String email);

    @Query("{ 'phone' : ?0 }")
    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String email, String phone);

//    public default User getUserByPhone(String phone) {
//        Criteria criteria = Criteria.where("phone").is(phone);
//        Query query = new Query(criteria);
//        User user = mongoTemplate.findOne(query, Outlet.class);
//        return outlet;
//
//    }
}