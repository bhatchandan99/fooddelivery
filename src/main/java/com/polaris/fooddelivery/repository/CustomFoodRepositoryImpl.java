package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.models.Food;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;

import java.util.List;

public class CustomFoodRepositoryImpl implements CustomFoodRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomFoodRepositoryImpl.class);
    @Autowired
//    @Qualifier("quizServiceMongoConfig")
    MongoTemplate mongoTemplate;


    @Override
    public Pair<Long, List<Food>> getFoodFromOutletId(String outletId, Boolean isEnabled, Integer pageNum, Integer pageSize, String sortKey) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        Criteria.where("outletId").is(outletId);
        Criteria criteria = Criteria
                .where("isEnabled").is(isEnabled);
        Query query = new Query(criteria);
        long totalRecords = mongoTemplate.count(query, Food.class);
        query.with(pageRequest).with(Sort.by(Sort.Direction.ASC, sortKey));
        List<Food> orders = mongoTemplate.find(query, Food.class);
        return Pair.of(totalRecords, orders);


    }

    public List<Food> getFoodFromIds(List<String> foodIds) {
        log.info("getFoodFromIds  " + foodIds);
        Criteria criteria = Criteria.where("_id").in(foodIds);
        return mongoTemplate.find(new Query(criteria), Food.class);
    }

}
