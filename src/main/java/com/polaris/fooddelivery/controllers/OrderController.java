package com.polaris.fooddelivery.controllers;

import com.polaris.fooddelivery.dto.*;
import com.polaris.fooddelivery.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;

    }





    @GetMapping("/customer/{userId}/orders")
    GetOrdersResponseDto getOrders(@PathVariable String userId,
                                   @RequestParam(value = "pageSize", defaultValue = "10", required = false) int size,
                                   @RequestParam(value = "pageNo", defaultValue = "0", required = false) int page
                                   ) throws Exception {

        log.info(String.format("getOrders : get order  for userId %s ", userId));

        return orderService.getOrders(userId,size,page);
    }

    @GetMapping("/rider/{userId}/orders")
    GetRiderOrdersResponseDto getRiderOrders(@PathVariable String userId,
                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) int size,
                                             @RequestParam(value = "pageNo", defaultValue = "0", required = false) int page
    ) throws Exception {

        log.info(String.format("getRiderOrders : get order  for userId %s ", userId));

        return orderService.getRiderOrders(userId,page,size);
    }






    @PutMapping("/orders/{orderId}/accept")
    AcceptOrderResponseDto acceptOrder(@RequestBody AcceptOrderRequestDto order) throws Exception {

        log.info(String.format("acceptOrder : accept order  menu for outletId %s ", order));

        return orderService.acceptOrder(order);

    }

}
