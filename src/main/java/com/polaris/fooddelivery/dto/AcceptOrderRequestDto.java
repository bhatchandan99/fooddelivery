package com.polaris.fooddelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcceptOrderRequestDto {
    String orderId;
    String restrauntId;
    List<String> foodIds;
}
