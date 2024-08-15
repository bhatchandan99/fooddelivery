package com.polaris.fooddelivery.controllers;

import com.polaris.fooddelivery.dto.CreateEntityResponseDto;
import com.polaris.fooddelivery.dto.LoginRequestDto;
import com.polaris.fooddelivery.dto.VerifyOtpDto;
import com.polaris.fooddelivery.dto.VerifyOtpEntityResponseDto;
import com.polaris.fooddelivery.enums.Role;
import com.polaris.fooddelivery.models.Credential;
import com.polaris.fooddelivery.models.User;
import com.polaris.fooddelivery.repository.ItemRepository;
import com.polaris.fooddelivery.repository.UserRepository;
import com.polaris.fooddelivery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Autowired
    UserService userService;

    public UserController(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }


    @PostMapping("/login")
    Credential loginUser(@RequestBody LoginRequestDto user) throws IOException {

        String emailId = user.getEmail();
        String phone = user.getPhone();
        // check valid email or phone
        if (emailId.isEmpty() && phone.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }

        return userService.checkValidUser(emailId, phone, Role.CUSTOMER);
    }

    @PostMapping("/verifyotp")
    Credential verifyOtp(String email, String phone, Integer otp) throws IOException {
        if (!email.isEmpty() && !phone.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST
            );
        }
        if (otp == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }
        return userService.verifyCredential(email, phone, otp);
    }

    // register user
    @PostMapping("/user")
    CreateEntityResponseDto createUser(@RequestBody User user) throws IOException {
        if (!StringUtils.hasLength(user.getEmail()) || !StringUtils.hasLength(user.getPhone())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE,
                    "Provide phone and email"
            );
        }
        return userService.createUser(user);
    }

    // verify signup
    @PostMapping("/verify/user")
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
                    HttpStatus.NOT_ACCEPTABLE
            );
        }
        return userService.verifyOtp(email, phone, otp);
    }

}
