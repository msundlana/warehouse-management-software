package com.github.msundlana.warehousemanagementapi.services;

import com.github.msundlana.warehousemanagementapi.models.ProductArticleDTO;

import java.util.List;
import java.util.Set;

public interface ProductArticleService {
    ProductArticleDTO linkProductArticleToProduct(Long productId, Long articleId, int quantity);
    Set<ProductArticleDTO> getLinkProductArticleToProduct(Long productId);
}
