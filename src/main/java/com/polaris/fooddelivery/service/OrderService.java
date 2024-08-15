package com.polaris.fooddelivery.service;

import com.polaris.fooddelivery.constants.GenericConstants;
import com.polaris.fooddelivery.dto.*;
import com.polaris.fooddelivery.models.*;
import com.polaris.fooddelivery.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private RiderRepository riderRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomFoodRepository foodRepository;

    @Autowired
    private OutletRepository outletRepository;

    public GetOrdersResponseDto getOrders(String userId, Integer size, Integer page) throws Exception {

        User user = userRepository.findById(userId).orElse(null);

        if (ObjectUtils.isEmpty(user)) {
            log.error(String.format("getOrders : user not found for   %s user_id  ", userId));
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("user not found for   %s user_id  ", userId)
            );
        }

        List<Order> orders = orderRepository.getOrdersByCustomerId(userId, page, size);
        if (ObjectUtils.isEmpty(orders)) {
            log.error(String.format("getOrders : orders not found for   %s user_id  ", userId));
            return GetOrdersResponseDto.builder()
                    .orders(new ArrayList<>())
                    .totalRecords(0)
                    .build();
        }
        return GetOrdersResponseDto.builder()
                .orders(orders)
                .build();

    }


    public GetRiderOrdersResponseDto getRiderOrders(String riderId, Integer page, Integer size) throws Exception {

        Rider rider = riderRepository.findById(riderId).orElse(null);

        if (ObjectUtils.isEmpty(rider)) {
            log.error(String.format("getRiderOrders : user not found for   %s user_id  ", riderId));
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("rider not found for   %s user_id  ", riderId)
            );
        }
        Pair<Long, List<Order>> orders = orderRepository.getOrdersByRiderId(riderId, true, page, size);
        if (ObjectUtils.isEmpty(orders) || ObjectUtils.isEmpty(orders.getSecond())) {
            log.error(String.format("getRiderOrders : orders not found for   %s riderId  ", riderId));
            return GetRiderOrdersResponseDto.builder()
                    .orders(new ArrayList<>())
                    .totalRecords(0)
                    .build();
        }
        log.info(String.format("getRiderOrders : r   %s riderId  ", orders));

        return GetRiderOrdersResponseDto.builder()
                .orders(orders.getSecond())
                .totalRecords(orders.getFirst())
                .build();
    }


    public AcceptOrderResponseDto acceptOrder(AcceptOrderRequestDto orderRequestDto) throws Exception {


        Order order = orderRepository.findById(new ObjectId(orderRequestDto.getOrderId())).orElse(null);

        if (ObjectUtils.isEmpty(order)) {
            log.error(String.format("acceptOrder : orders not found for   %s user_id  ", orderRequestDto));
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No order found"
            );
        }

        if (order.getIsAccepted()) {
            log.error(String.format("acceptOrder : accept failed for   %s user_id  ", orderRequestDto));
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Order Already Accepted"
            );
        }
        Outlet outlet = outletRepository.getOutletById(orderRequestDto.getRestrauntId());

        if (ObjectUtils.isEmpty(outlet) || (!outlet.getIsAvailable().equals(false))) {
            log.error(String.format("acceptOrder : accept failed  for   %s user_id  ", orderRequestDto));

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Restraunt not accepting orders"
            );
        }

        List<Food> foodList = foodRepository.getFoodFromIds(orderRequestDto.getFoodIds());

        if (foodList.size() != orderRequestDto.getFoodIds().size()) {
            log.error(String.format("getOrders : orders not found for   %s user_id  ", foodList));
            return AcceptOrderResponseDto.builder()
                    .message("Food items not available")
                    .build();
        }
        //get food
        List<Double> coordinates = outlet.getLocation().getCoordinates();

        // todo pick constant
        List<RiderWithDistance> availableRiders = riderRepository.getRidersForRestraunt(coordinates, GenericConstants.MAX_DISTANCE_SEARCH, GenericConstants.MAX_RIDER_LIMIT);
        order.setIsAccepted(true);
        order.setUpdatedAt(new Date());
        orderRepository.save(order);
        if (ObjectUtils.isEmpty(availableRiders)) {
            return AcceptOrderResponseDto.builder()
                    .message("Riders not available")
                    .build();
        }
        return AcceptOrderResponseDto.builder()
                .message("Success")
                .build();
    }


}

