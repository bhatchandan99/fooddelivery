package com.polaris.fooddelivery.controllers;

import com.polaris.fooddelivery.dto.LoginRequestDto;
import com.polaris.fooddelivery.models.Credential;
import com.polaris.fooddelivery.models.GroceryItem;
import com.polaris.fooddelivery.models.User;
import com.polaris.fooddelivery.repository.ItemRepository;
import com.polaris.fooddelivery.repository.UserRepository;
import com.polaris.fooddelivery.service.UserService;
import com.polaris.fooddelivery.enums.Role;
import com.polaris.fooddelivery.dto.VerifyOtpDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Autowired
    UserService userService;

    public UserController(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/hello")
    String home(){
        return "Hi guys";
    }

    @GetMapping("/users")
    List<GroceryItem> getUsers() {
        System.out.println("Hello World!111"+ itemRepository.findAll());
        return itemRepository.findAll();
//        return userRepository.findALl();
    }

    @GetMapping("/{id}")
    User findById(@PathVariable Integer id) {
        System.out.println("Hello World!1111234");


        Optional<User> user =  userRepository.findById(id);
        System.out.println("Hello World!112221" + user);

        if(user.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }
        System.out.println("Hello World!111333" + user);

        return user.get();
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
        return userService.verifyCredential(email,phone,otp);
    }

    // register user
    @PostMapping("/createUser")
    User createUser(@RequestBody User user) throws IOException {
        if (user.getEmail().isEmpty() || user.getPhone().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }
        return userService.createUser(user);
    }

    // verify signup
    @PostMapping("/verifyUser")
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
        return userService.verifyOtp(email,phone,otp);
    }

}
