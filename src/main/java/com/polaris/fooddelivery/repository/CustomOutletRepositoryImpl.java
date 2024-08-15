package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.constants.GenericConstants;
import com.polaris.fooddelivery.dto.OutletWithDistance;
import com.polaris.fooddelivery.models.Outlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class CustomOutletRepositoryImpl implements CustomOutletRepository {

    @Autowired
//    @Qualifier("quizServiceMongoConfig")
    MongoTemplate mongoTemplate;


    @Override
    public Outlet getOutletById(String outletId) {
        Criteria criteria = Criteria.where("_id").is(outletId);
        Query query = new Query(criteria);
        Outlet outlet = mongoTemplate.findOne(query, Outlet.class);
        return outlet;

    }

    @Override
    public List<OutletWithDistance> getOutletByCustomerId(String city, List<Double> coords) {
        NearQuery nearQuery = NearQuery
                .near(new Point(coords.get(0), coords.get(1)));
        nearQuery.maxDistance(GenericConstants.MAX_DISTANCE_SEARCH);//put to constants
        MatchOperation matchStage = match(new Criteria("isEnabled").is(true));
        Aggregation a = newAggregation(geoNear(nearQuery, "distance"), matchStage);
        AggregationResults<OutletWithDistance> results =
                mongoTemplate.aggregate(a, Outlet.class, OutletWithDistance.class);
        List<OutletWithDistance> ls = results.getMappedResults().stream().toList();
        return ls;
    }

}
