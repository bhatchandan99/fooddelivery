package com.polaris.fooddelivery.controllers;

import com.polaris.fooddelivery.models.Credential;
import com.polaris.fooddelivery.models.Rider;
import com.polaris.fooddelivery.models.User;
import com.polaris.fooddelivery.service.RiderService;
import com.polaris.fooddelivery.service.UserService;
import com.polaris.fooddelivery.dto.RiderResponseDto;
import com.polaris.fooddelivery.enums.Role;
import com.polaris.fooddelivery.dto.UpdateDriverLocationDto;
import com.polaris.fooddelivery.dto.VerifyOtpDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class RiderController {

    @Autowired
    UserService userService;
    @Autowired
    private RiderService riderService;

    private static final Logger log = LoggerFactory.getLogger(RiderController.class);

    @PostMapping("/loginRider")
    Credential loginUser(@RequestBody User user) throws IOException {
        String emailId = user.getEmail();
        String phone = user.getPhone();

        // check valid email or phone
        if (emailId.isEmpty() && phone.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }

        return userService.checkValidUser(emailId, phone, Role.RIDER);
    }

//    @PostMapping("/verifyotp")
//    Credential verifyOtp(String email, String phone, String otp) throws IOException {
//        if (!email.isEmpty() && !phone.isEmpty()) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST
//            );
//        }
//        if (!otp.isEmpty()) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_ACCEPTABLE
//            );
//        }
//        return userService.verifyCredential(email,phone,otp);
//    }

    // create rider details
    @PostMapping("/createRider")
    Rider createRider(@RequestBody Rider rider) throws IOException {
        if (rider.getEmail().isEmpty() || rider.getPhone().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }
        return riderService.createRider(rider);
    }

    @PostMapping("/verifysignUpOtp")
    Credential verifySignUpOtp(@RequestBody VerifyOtpDto verifyOtpDto) throws IOException {
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
                    HttpStatus.NOT_ACCEPTABLE
            );
        }
        return riderService.verifyRiderOtp(email, phone, otp);
    }

    @PutMapping("/rider/{riderId}/coords")
    RiderResponseDto updateRiderLocation(@PathVariable String riderId,
                                         @RequestBody UpdateDriverLocationDto updateDriverLocationDt) throws IOException {

        return riderService.updateRiderLocation(riderId, updateDriverLocationDt);
    }




}
