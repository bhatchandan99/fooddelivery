package com.polaris.fooddelivery.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
//@Builder
@Document("order")
public class Order {

    @Id
    private String id;
    private String name;
    //    private Boolean isAvailable;
    private String categoryId;
    private String customerId;
    private String riderId;
    private String description;
    private String instruction;
    private String outletId;
    private Boolean isCompleted;
    private String remarks;
    private Boolean isAccepted = false;
    private Date createdAt = new Date();
    private Date updatedAt = new Date();
    private Date deliveredAt = new Date();
    private List<String> foodIds;


    public Order(String id, String name, Boolean isAvailable, String category, String customer, String rider, String description, String instruction, String outletId, Boolean isCompleted, String remarks, Date createdAt, Date updatedAt, Boolean isAccepted, List<String> foodIds) {
        super();
        this.id = id;
        this.name = name;
        this.customerId = customer;
        this.categoryId = category;
        this.riderId = rider;
        this.description = description;
        this.instruction = instruction;
        this.outletId = outletId;
        this.isCompleted = isCompleted;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAccepted = isAccepted;
        this.foodIds = foodIds;


    }
}