package com.polaris.fooddelivery.service;

import com.mongodb.lang.Nullable;
import com.polaris.fooddelivery.constants.GenericConstants;
import com.polaris.fooddelivery.dto.CreateEntityResponseDto;
import com.polaris.fooddelivery.dto.VerifyOtpEntityResponseDto;
import com.polaris.fooddelivery.enums.Role;
import com.polaris.fooddelivery.models.Credential;
import com.polaris.fooddelivery.models.Outlet;
import com.polaris.fooddelivery.models.Rider;
import com.polaris.fooddelivery.models.User;
import com.polaris.fooddelivery.repository.CredentialRepository;
import com.polaris.fooddelivery.repository.OutletRepository;
import com.polaris.fooddelivery.repository.RiderRepository;
import com.polaris.fooddelivery.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private OutletRepository outletRepository;

    // login check
    public Credential checkValidUser(@Nullable String emailId, @Nullable String phone, Role role) throws IOException {
        // check valid email or not
        if (phone.length() != 10) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE
            );
        }
        // check by email id
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);

        // check by phone number
        Credential credential = new Credential();
        switch (role) {
            case CUSTOMER:
                Optional<User> userDetails = userRepository.findByPhone(phone);
                if (userDetails.isPresent()) {

                    // call otp service
                    sendOtp(userDetails.get().getPhone(), otp);

                    // update otp
                    credential.setEmail(userDetails.get().getEmail());
                    credential.setPhone(phone);
                    credential.setOtp(otp);
                    credential.setRole(Role.CUSTOMER);
                    credentialRepository.updateCredentialByPhone(phone, credential);
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND
                    );
                }
                // email to do later
                break;

            case RESTURANT:
                //
                Optional<Outlet> outletDetails = outletRepository.findByEmailOrPhone(phone, emailId);
                if (outletDetails.isPresent()) {

                    // call otp service
                    sendOtp(outletDetails.get().getPhone(), otp);

                    // update otp
                    credential.setEmail(outletDetails.get().getEmail());
                    credential.setPhone(phone);
                    credential.setOtp(otp);
                    credential.setRole(Role.RESTURANT);
                    credentialRepository.updateCredentialByPhone(phone, credential);
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND
                    );
                }
                break;

            case RIDER:
                Optional<Rider> riderDetails = riderRepository.findByEmailOrPhone(phone, emailId);
                if (riderDetails.isPresent()) {

                    // call otp service
                    sendOtp(riderDetails.get().getPhone(), otp);

                    // update otp
                    credential.setEmail(riderDetails.get().getEmail());
                    credential.setPhone(phone);
                    credential.setOtp(otp);
//                    credential.setRole(riderDetails.get().getRole());
                    credential.setRole(Role.RIDER);
                    credentialRepository.updateCredentialByPhone(phone, credential);
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND
                    );
                }
                break;

        }
        return credential;

    }

    @Async
    public Integer sendOtp(String phone, Integer otp) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        String jsonBody = "{\"messages\":[{\"destinations\":[{\"to\":\"" + phone + "\"}],\"from\":\"ServiceSMS\",\"text\":\"" + otp + " Congratulations on sending your first message.\\nGo ahead and check the delivery report in the next step.\"}]}";
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(GenericConstants.SMS_2_TEXT_ADVANCED)
                .method("POST", body)
                .addHeader("Authorization", GenericConstants.BA_5_D)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return otp;
    }

    public Credential verifyCredential(String email, String phone, Integer otp) throws IOException {
        Optional<Credential> credentialOptional = credentialRepository.findByEmailOrPhone(email, phone);
        if (!credentialOptional.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }
        Credential credential = credentialOptional.get();
        if (!credential.getOtp().equals(otp)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED
            );
        }
        return credential;
    }

    public CreateEntityResponseDto createUser(User user) throws IOException {

        String email = user.getEmail();
        String phone = user.getPhone();

        // check if already there or not
        Optional<User> userDetails = userRepository.findByEmailOrPhone(email, phone);
        if (userDetails.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "User already exists!"
            );
        }

        user.setVerified(false);
        user.setEnabled(true);
        // send otp
        Random random = new Random();
        Integer otp = 1000 + random.nextInt(9000);
        sendOtp(phone, otp);

        //save credentials
        Credential credential = new Credential();
        credential.setEmail(email);
        credential.setPhone(phone);
        credential.setOtp(otp);
        credential.setRole(Role.CUSTOMER);
        credential.setCreatedAt(new Date().toString());
        credential.setUpdatedAt(new Date().toString());

        log.info("credentials saved");
        User userData = userRepository.save(user);
        credential.setUserId(userData.getId());
        credentialRepository.save(credential);
        return CreateEntityResponseDto
                .builder()
                .message("Please Verify OTP")
                .id(userData.getId())
                .build();
    }

    public VerifyOtpEntityResponseDto verifyOtp(String email, String phone, Integer otp) {
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

        User user = userRepository.findByEmailOrPhone(email, phone).orElse(null);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.setVerified(true);
        userRepository.save(user);
        return VerifyOtpEntityResponseDto.builder().message("Success").build();
    }
}
