package com.polaris.fooddelivery.repository;

import com.polaris.fooddelivery.dto.OutletWithDistance;
import com.polaris.fooddelivery.dto.RiderWithDistance;
import com.polaris.fooddelivery.models.Outlet;
import com.polaris.fooddelivery.models.Rider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class CustomRiderRepositoryImpl implements CustomRiderRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomRiderRepositoryImpl.class);
    @Autowired
//    @Qualifier("quizServiceMongoConfig")
    MongoTemplate mongoTemplate;


    public List<RiderWithDistance> getRidersForRestraunt(List<Double> coords, Integer distance, Integer maxCount) {
        final NearQuery nearQuery = NearQuery
                .near(new Point(coords.get(0), coords.get(1)));
        nearQuery.maxDistance(distance);//put to constants
        nearQuery.limit(maxCount);
        final Aggregation a = newAggregation(geoNear(nearQuery, "distance"));
        final AggregationResults<RiderWithDistance> results =
                mongoTemplate.aggregate(a, Rider.class, RiderWithDistance.class);
        List<RiderWithDistance> ls = results.getMappedResults().stream().toList();

        return ls;
    }


    public List<OutletWithDistance> getOutletByCustomerId(String city, List<Double> coords) {
        final NearQuery nearQuery = NearQuery
                .near(new Point(coords.get(0), coords.get(1)));
        nearQuery.maxDistance(10000);//put to constants
        MatchOperation matchStage = match(new Criteria("isEnabled").is(true));
        final Aggregation a = newAggregation(geoNear(nearQuery, "distance"), matchStage);
        final AggregationResults<OutletWithDistance> results =
                mongoTemplate.aggregate(a, Outlet.class, OutletWithDistance.class);
        List<OutletWithDistance> ls = results.getMappedResults().stream().toList();
        return ls;
    }

}
