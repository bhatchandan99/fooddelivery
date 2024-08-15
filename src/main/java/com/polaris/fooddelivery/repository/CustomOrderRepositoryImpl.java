package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;

import java.util.List;

public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    @Autowired
//    @Qualifier("quizServiceMongoConfig")
    MongoTemplate mongoTemplate;


    @Override
    public List<Order> getOrdersByCustomerId(String customerId, Integer pageNum, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Criteria criteria = Criteria.where("customerId").is(customerId);

        Query query = new Query(criteria);
        query.with(pageRequest).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Order> orders = mongoTemplate.find(query, Order.class);
        return orders;

    }

    @Override
    public Pair<Long, List<Order>> getOrdersByRiderId(String riderId, Boolean isComp, Integer pageNum, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Criteria
                .where("riderId").is(riderId);
        Criteria criteria = Criteria
                .where("isCompleted").is(isComp);
        Query query = new Query(criteria);
        long totalRecords = mongoTemplate.count(query, Order.class);
        query.with(pageRequest).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Order> orders = mongoTemplate.find(query, Order.class);
        return Pair.of(totalRecords, orders);

    }
}
