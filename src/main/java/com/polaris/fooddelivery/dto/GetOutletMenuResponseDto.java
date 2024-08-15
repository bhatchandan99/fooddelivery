package com.polaris.fooddelivery.dto;

import com.polaris.fooddelivery.models.Food;
import com.polaris.fooddelivery.models.Outlet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetOutletMenuResponseDto {
    private List<Food> food;
    private Outlet outlet;
    private long totalRecords;
}
