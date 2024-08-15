package com.polaris.fooddelivery.helpers;

import com.polaris.fooddelivery.dto.OutletWithDistance;
import com.polaris.fooddelivery.models.Credential;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GenericHelper {

    @NonNull
    public static Comparator<OutletWithDistance> getComparator() {
        return new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                Integer x1 = ((OutletWithDistance) o1).getTags().size();
                Integer x2 = ((OutletWithDistance) o2).getTags().size();
                if (x1 != x2) return x2.compareTo(x1);

                Double d1 = ((OutletWithDistance) o1).getDistance();
                Double d2 = ((OutletWithDistance) o2).getDistance();
                return d1.compareTo(d2);
            }
        };
    }

    public static void validateOtpExpiry(Integer otp, Credential credential) {
        long diffInMillies = Math.abs(new Date().getTime() - credential.getCreatedAt().getTime());

        long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if (diff > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token Expired");
        }
        if (!credential.getOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
    }

}
