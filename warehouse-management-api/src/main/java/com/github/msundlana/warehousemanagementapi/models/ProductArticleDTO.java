package com.github.msundlana.warehousemanagementapi.models;

import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductArticleDTO {

    @Positive
    private Long id;

    @Positive
    private Long productId;

    @Positive
    private Long articleId;

    @Positive
    private int quantity;

    private ArticleDTO article;
}
