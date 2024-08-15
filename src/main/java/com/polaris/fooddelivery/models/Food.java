package com.polaris.fooddelivery.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document("food")
public class Food {

    @Id
    private String id;
    private String name;
    private Boolean isAvailable;
    private Boolean isEnabled;
    private List<String> tags;
    private String categoryId;
    private String description;
    private String createdAt;
    private String updatedAt;
    private Float price;
    private String imageUrl;
    private String outletId;
    private FoodGenre foodGenre;

    public Food(String id, String name, Boolean isAvailable, Boolean isEnabled, List<String> tags, String categoryId, String description, String createdAt, String updatedAt, Float price) {
        this.id = id;
        this.name = name;
        this.isAvailable = isAvailable;
        this.isEnabled = isEnabled;
        this.tags = tags;
        this.categoryId = categoryId;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.price = price;


    }

    public enum FoodGenre {
        VEG, NONVEG, VEGAN
    }

}
