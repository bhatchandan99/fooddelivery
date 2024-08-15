package com.polaris.fooddelivery.controllers;

import com.polaris.fooddelivery.dto.*;
import com.polaris.fooddelivery.enums.Role;
import com.polaris.fooddelivery.models.Credential;
import com.polaris.fooddelivery.service.OrderService;
import com.polaris.fooddelivery.service.OutletService;
import com.polaris.fooddelivery.models.Outlet;
import com.polaris.fooddelivery.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class OutletController {
    private static final Logger log = LoggerFactory.getLogger(OutletController.class);

   @Autowired
   OutletService outletService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;


    @GetMapping("/outlet/{outletId}/riders")
    GetRidersForOutletDto getRidersForOutlet(@PathVariable String outletId,
                                             @RequestParam(value = "count", defaultValue = "10", required = false) int count) throws Exception {

        log.info(String.format("getRidersForOutlet : get order  menu for outletId %s ", outletId));

        return outletService.getRidersForOutlet(outletId,count);
    }

    @GetMapping("/outlet/{outletId}/menu")
    GetOutletMenuResponseDto getOutletMenu(@PathVariable String outletId,
                                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) int size,
                                           @RequestParam(value = "pageNo", defaultValue = "0", required = false) int page,
                                           @RequestParam(value = "sortKey", defaultValue = "price", required = false)  String sortKey
    ) throws Exception {

        log.info(String.format("getOutletMenu : get order  menu for outletId %s ", outletId));

        return outletService.getOutletMenu(outletId,sortKey,page,size);
    }

    @GetMapping("/customer/{userId}/outlets")
    GetOutletsResponseDto getOutlets(@PathVariable String userId) throws Exception {

        log.info(String.format("getOrders : get order  for userId %s ", userId));

        return outletService.getOutlets(userId);
    }

    @PostMapping("/loginOutlet")
    Credential loginOutlet(@RequestBody Outlet outlet) throws IOException {
        String emailId = outlet.getEmail();
        String phone = outlet.getPhone();

        // check valid email or phone
        if (emailId.isEmpty() && phone.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }
        return userService.checkValidUser(emailId, phone, Role.RESTURANT);
    }

    @PostMapping("/outlet")
    CreateEntityResponseDto createOutlet(@RequestBody Outlet outlet) throws IOException {
        if (!StringUtils.hasLength(outlet.getEmail()) || !StringUtils.hasLength(outlet.getPhone())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE,
                    "Provide phone and email"
            );
        }
        return outletService.createOutlet(outlet);
    }

    @PostMapping("/verify/outlet")
    VerifyOtpEntityResponseDto verifySignUpOtp(@RequestBody VerifyOtpDto verifyOtpDto) throws IOException {
        String email = verifyOtpDto.getEmail();
        String phone = verifyOtpDto.getPhone();
        Integer otp = verifyOtpDto.getOtp();
        if (email.isEmpty() || Objects.requireNonNull(phone).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }
        if (otp == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE,
                    "No otp found"
            );
        }
        return outletService.verifyOutletOtp(email, phone, otp);
    }
}
