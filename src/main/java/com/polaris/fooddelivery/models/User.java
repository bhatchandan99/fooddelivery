package com.polaris.fooddelivery.models;

import com.polaris.fooddelivery.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("user")
public class User {
    Role role;
    List<Location> locations;
    @Id
    private String id;
    private String userName;
    //private String firstName;
    //private String lastName;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String phone;
    private boolean isVerified = false;
    private boolean isEnabled = false;
    private List<String> tags;


    public void userDetails(String id, String userName, String email, String phone, List<String> tags) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.tags = tags;
    }

}

