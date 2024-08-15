package com.polaris.fooddelivery.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document("outlet")
public class Outlet {

    @Id
    private String id;
    private String name;
    private Boolean isAvailable;
    private Boolean isEnabled;
    private List<String> tags;
    private String categoryId;
    private String description;
    private String remarks;
    private String createdAt;
    private String updatedAt;
    private String franchiseId;//todo
    private Location location;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String phone;
    private List<String> foodIds;
    private Double rating;

    public Outlet(String name, Boolean isAvailable, Boolean isEnabled, List<String> tags, String description, String remarks, String createdAt, String updatedAt, String franchiseId, Location location) {
        super();
        this.name = name;
        this.isAvailable = isAvailable;
        this.isEnabled = isEnabled;
        this.tags = tags;
        this.description = description;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.franchiseId = franchiseId;
        this.location = location;

    }
}