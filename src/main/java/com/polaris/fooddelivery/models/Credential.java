package com.polaris.fooddelivery.models;

import com.polaris.fooddelivery.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("credential")
public class Credential {
    Role role;
    @Id
    private String id;
    private String userId;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String phone;
    private Integer otp;
    private String token;
    private Date createdAt = new Date();
    private Date updatedAt = new Date();
}
