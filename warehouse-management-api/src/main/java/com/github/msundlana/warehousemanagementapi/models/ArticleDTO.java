package com.github.msundlana.warehousemanagementapi.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
public class ArticleDTO {
    @Positive
    private Long id;

    private String name;

    @PositiveOrZero
    private int stock;

}
