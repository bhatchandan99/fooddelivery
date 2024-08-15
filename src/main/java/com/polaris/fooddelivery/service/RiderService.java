package com.polaris.fooddelivery.service;

import com.polaris.fooddelivery.dto.RiderResponseDto;
import com.polaris.fooddelivery.dto.UpdateDriverLocationDto;
import com.polaris.fooddelivery.enums.Role;
import com.polaris.fooddelivery.models.Credential;
import com.polaris.fooddelivery.models.Location;
import com.polaris.fooddelivery.models.Rider;
import com.polaris.fooddelivery.repository.CredentialRepository;
import com.polaris.fooddelivery.repository.RiderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

@Service
public class RiderService {

    private static final Logger log = LoggerFactory.getLogger(RiderService.class);
    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialRepository credentialRepository;

    public Rider createRider(Rider rider) throws IOException {

        String email = rider.getEmail();
        String phone = rider.getPhone();

        // check if already there or not
        Optional<Rider> riderDetails = riderRepository.findByEmailOrPhone(email, phone);
        if (riderDetails.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Rider already exists!"
            );
        }

        rider.setVerified(false);
        // send otp
        Random random = new Random();
        Integer otp = 1000 + random.nextInt(9000);
        userService.sendOtp(phone, otp);

        //save credentials
        Credential credential = new Credential();
        credential.setEmail(email);
        credential.setPhone(phone);
        credential.setOtp(otp);
        credential.setRole(Role.RIDER);
        credential.setCreatedAt(rider.getCreatedAt());
        credential.setUpdatedAt(rider.getUpdatedAt());
        Rider riderData = riderRepository.save(rider);
        credential.setUserId(riderData.getId());
        credentialRepository.save(credential);
        return riderData;
    }

    public Credential verifyRiderOtp(String email, String phone, Integer otp) {
        if (!phone.isEmpty() && phone.length() != 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number");
        }

        Optional<Credential> credentialDetails = credentialRepository.findByEmailOrPhone(email, phone);
        if (credentialDetails.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Credential not found");
        }
        Credential credential = credentialDetails.get();
        if (!credential.getOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        Rider rider = riderRepository.findByEmailOrPhone(email, phone).orElse(null);
        if (rider == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rider not found");
        }
        rider.setVerified(true);
        riderRepository.save(rider);
        return credential;
    }


    public RiderResponseDto updateRiderLocation(String riderId, UpdateDriverLocationDto riderLocationDto) {

        if (ObjectUtils.isEmpty(riderLocationDto.getCoordinates()) || riderLocationDto.getCoordinates().size() != 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid coordinates"
            );
        }
        Rider rider = riderRepository.findById(riderId).orElse(null);
        if (ObjectUtils.isEmpty(rider)) {
            log.info(String.format("getRidersForOutlet : get order  menu for riderId %s ", riderId));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rider not found");
        }
        Location location = rider.getLocation();
        location.setCoordinates(riderLocationDto.getCoordinates());
        rider.setLocation(location);
        riderRepository.save(rider);
        return RiderResponseDto.builder().message("Successfully updated rider").build();
    }
}
