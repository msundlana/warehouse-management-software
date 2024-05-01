package com.github.msundlana.warehousemanagementapi.controllers;

import com.github.msundlana.warehousemanagementapi.models.ArticleDTO;
import com.github.msundlana.warehousemanagementapi.models.ProductDTO;
import com.github.msundlana.warehousemanagementapi.models.ProductInventoryDTO;
import com.github.msundlana.warehousemanagementapi.services.WarehouseService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base}/warehouse")
@CircuitBreaker(name="warehouse-api")
@Bulkhead(name="warehouse-api")
@RateLimiter(name = "warehouse-api")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/articles/load")
    public ResponseEntity<String> loadArticles(@RequestBody List<ArticleDTO> articles) {
        var savedArticles = warehouseService.loadArticles(articles);
        return ResponseEntity.ok(savedArticles.size()+" Articles loaded successfully");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/products/load")
    public ResponseEntity<String> loadProducts(@RequestBody List<ProductDTO> products) {
        var savedProducts = warehouseService.loadProducts(products);
        return ResponseEntity.ok(savedProducts.size()+" Products loaded successfully");
    }


    @GetMapping("/products")
    public ResponseEntity<Page<ProductInventoryDTO>> getAllAvailableProducts(@RequestParam(required = false) String searchText,
                                                                             @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer page,
                                                                             @RequestParam(required = false, defaultValue = "20") @Positive Integer pageSize) {
        Page<ProductInventoryDTO> products = warehouseService.getAllAvailableProducts(searchText,page,pageSize);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @DeleteMapping("/products/{productId}/sell")
    public ResponseEntity<String> sellProduct(@PathVariable Long productId) {
        warehouseService.sellProduct(productId);
        return ResponseEntity.ok("Product sold successfully");
    }
}

