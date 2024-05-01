package com.github.msundlana.warehousemanagementapi.models;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @Positive
    private Long id;

    private String name;

    private double price;

    private Set<ProductArticleDTO> articles;
}
