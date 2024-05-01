package com.github.msundlana.warehousemanagementapi.services;

import com.github.msundlana.warehousemanagementapi.models.ArticleDTO;
import com.github.msundlana.warehousemanagementapi.models.ProductDTO;
import com.github.msundlana.warehousemanagementapi.models.ProductInventoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;


 public interface WarehouseService {
     List<ArticleDTO> loadArticles(List<ArticleDTO> articles);

     List<ProductDTO> loadProducts(List<ProductDTO> products);

     Page<ProductInventoryDTO> getAllAvailableProducts(String query,int page, int pageSize);

     void sellProduct(Long productId);

}
