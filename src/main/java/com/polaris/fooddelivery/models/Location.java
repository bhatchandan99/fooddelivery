package com.polaris.fooddelivery.models;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class Location {
    private String id;
    private String line1;
    private String line2;
    private String line3;
    private String city;
    private String state;
    private String country;
    private String zip;
    private List<Double> coordinates = new ArrayList<>();


    public Location(String id, String line1, String line2, String line3, String city, String state, String country, String zip, List<Double> coordinates) {
        super();
        this.id = id;
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.coordinates = coordinates;

    }


}
