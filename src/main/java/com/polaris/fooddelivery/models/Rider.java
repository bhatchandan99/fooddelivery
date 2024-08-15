package com.polaris.fooddelivery.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
//@AllArgsConstructor
//@Builder
@Document("rider")
public class Rider {

    Location location;
    @Id
    private String id;
    private String name;
    private boolean isVerified;
    @Indexed(unique = true)
    private String email;
    private boolean isEnabled;
    private boolean isAvailable;
    private boolean isOffline;
    @Indexed(unique = true)
    private String phone;
    private String profilePicture;
    private String vehicleType;
    private Double rating;
    private String createdAt;
    private String updatedAt;

    public void userDetails(String id, String userName, String email, String phone, Location location) {
        this.id = id;
        this.name = userName;
        this.email = email;
        this.phone = phone;
//        this.role = role;
        this.location = location;
    }
}
