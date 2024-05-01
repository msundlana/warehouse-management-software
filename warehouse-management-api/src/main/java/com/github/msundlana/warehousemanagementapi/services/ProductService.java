package com.github.msundlana.warehousemanagementapi.services;

import com.github.msundlana.warehousemanagementapi.models.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductDTO> getAllProducts(Pageable pageable) ;

    ProductDTO getProductById(Long id) ;

    List<ProductDTO> addProducts(List<ProductDTO> products);

    ProductDTO addProduct(ProductDTO productDTO);

    ProductDTO updateProduct(long id, ProductDTO productDTO);

    void removeProduct(long id);
}
