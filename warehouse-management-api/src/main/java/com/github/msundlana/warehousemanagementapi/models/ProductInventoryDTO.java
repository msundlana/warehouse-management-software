package com.github.msundlana.warehousemanagementapi.models;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class ProductInventoryDTO {
    @Positive
    private Long id;
    private String name;
    private double price;
    private int availableStock;
}
