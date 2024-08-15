package com.polaris.fooddelivery.service;

import com.polaris.fooddelivery.constants.GenericConstants;
import com.polaris.fooddelivery.dto.*;
import com.polaris.fooddelivery.enums.Role;
import com.polaris.fooddelivery.helpers.GenericHelper;
import com.polaris.fooddelivery.models.Credential;
import com.polaris.fooddelivery.models.Food;
import com.polaris.fooddelivery.models.Outlet;
import com.polaris.fooddelivery.models.User;
import com.polaris.fooddelivery.repository.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
public class OutletService {

    private static final Logger log = LoggerFactory.getLogger(OutletService.class);
    @Autowired
    UserService userService;

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    OutletRepository outletRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RiderRepository riderRepository;

    @Autowired
    FoodRepository foodRepository;


    public GetRidersForOutletDto getRidersForOutlet(String outletId, int count) throws Exception {
        //get user here
        Outlet outlet = outletRepository.findById(new ObjectId(outletId)).orElse(null);

        if (ObjectUtils.isEmpty(outlet)) {

            log.error(String.format("getRidersForOutlet : outlet not found for   %s outletId  ", outletId));
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("outlet not found for   %s outletId  ", outletId)
            );
        }

        List<RiderWithDistance> riderWithDistances = riderRepository.getRidersForRestraunt(outlet.getLocation().getCoordinates(), GenericConstants.MAX_DISTANCE_SEARCH, GenericConstants.MAX_RIDER_LIMIT);

        if (ObjectUtils.isEmpty(riderWithDistances)) {
            log.error(String.format("getRidersForOutlet : riders not found for   %s outletId  ", outletId));
            return GetRidersForOutletDto.builder()
                    .riders(new ArrayList<>())
                    .message("No riders found")
                    .build();
        }

        return GetRidersForOutletDto.builder()
                .riders(riderWithDistances)
                .message("Success!!")
                .build();

    }

    public GetOutletMenuResponseDto getOutletMenu(String outletId, String sortKey, int page, int size) throws Exception {

        // validation -> isAvailable; isEnabled; optional as req wont come;

        Outlet outlet = outletRepository.getOutletById(outletId);

        if (ObjectUtils.isEmpty(outlet) || (!ObjectUtils.isEmpty(outlet) && outlet.getIsEnabled().equals(false))) {
            //todo send error
            log.error(String.format("getOrders : orders not found for   %s user_id  ", outlet));
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No outlet found"
            );
        }
        Pair<Long, List<Food>> food = foodRepository.getFoodFromOutletId(outletId, true, page, size, sortKey);
        if (ObjectUtils.isEmpty(food)) {
            log.error(String.format("getOrders : food not found for   %s user_id  ", outletId));
            return GetOutletMenuResponseDto.builder()
                    .food(new ArrayList<>())
                    .outlet(outlet)
                    .totalRecords(0)
                    .build();
        }

        return GetOutletMenuResponseDto.builder()
                .food(food.getSecond())
                .outlet(outlet)
                .totalRecords(food.getFirst())
                .build();
    }


    public GetOutletsResponseDto getOutlets(String userId) throws Exception {

        User user = userRepository.findById(userId).orElse(null);

        if (ObjectUtils.isEmpty(user)) {
            log.error(String.format("getOutlets : user not found for   %s user_id  ", userId));
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("user not found for   %s user_id  ", userId)
            );
        }

        List<OutletWithDistance> outlets = outletRepository.getOutletByCustomerId(userId, user.getLocations().get(0).getCoordinates());


        if (ObjectUtils.isEmpty(outlets)) {
            log.error(String.format("getOutlets : outlets not found for   %s user_id  ", userId));
            return GetOutletsResponseDto.builder()
                    .outlets(new ArrayList<>())
                    .message("No outlets found")
                    .build();
        }

        ArrayList<OutletWithDistance> outletWithDistances = new ArrayList<>(outlets);
        outletWithDistances.stream().forEach(outletWithDistance -> {
            outletWithDistance.getTags().retainAll(user.getTags());
        });

        Collections.sort(outletWithDistances, GenericHelper.getComparator());
        return GetOutletsResponseDto.builder()
                .outlets(outletWithDistances)
                .message("Success!!")
                .build();

    }


    public CreateEntityResponseDto createOutlet(Outlet outlet) throws IOException {

        String email = outlet.getEmail();
        String phone = outlet.getPhone();

        // check if already there or not
        Optional<Outlet> outletDetails = outletRepository.findByEmailOrPhone(email, phone);
        if (outletDetails.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Outlet already exists!"
            );
        }

        outlet.setIsAvailable(false);
        outlet.setIsEnabled(false);
        // send otp
        Random random = new Random();
        Integer otp = 1000 + random.nextInt(9000);
        userService.sendOtp(phone, otp);

        //save credentials
        Credential credential = new Credential();
        credential.setEmail(email);
        credential.setPhone(phone);
        credential.setOtp(otp);
        credential.setRole(Role.RESTURANT);

        Outlet outletData = outletRepository.save(outlet);
        credential.setUserId(outletData.getId());
        credentialRepository.save(credential);
        return CreateEntityResponseDto.builder()
                .message("Please verify the OTP ")
                .id(outletData.getId())
                .build();
    }

    public VerifyOtpEntityResponseDto verifyOutletOtp(String email, String phone, Integer otp) {

        if (!phone.isEmpty() && phone.length() != 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number");
        }

        Optional<Credential> credentialDetails = credentialRepository.findByEmailOrPhone(email, phone);
        if (credentialDetails.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Credential not found");
        }
        Credential credential = credentialDetails.get();
        GenericHelper.validateOtpExpiry(otp, credential);
        if (!credential.getOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        Outlet outletDetails = outletRepository.findByEmailOrPhone(email, phone).orElse(null);
        if (outletDetails == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Outlet not found");
        }
        outletDetails.setIsAvailable(true);
        outletDetails.setIsEnabled(true);
        outletRepository.save(outletDetails);
        return VerifyOtpEntityResponseDto.builder()
                .message("OTP Verified")
                .build();
    }
}
