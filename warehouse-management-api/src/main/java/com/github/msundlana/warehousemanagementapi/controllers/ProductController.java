package com.github.msundlana.warehousemanagementapi.controllers;

import com.github.msundlana.warehousemanagementapi.models.ProductDTO;
import com.github.msundlana.warehousemanagementapi.services.ProductService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base}/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer page,
                                                          @RequestParam(required = false, defaultValue = "20") @Positive Integer size) {
        logger.info("Received request to get all products");

        var products = productService.getAllProducts(PageRequest.of(page, size));

        logger.info("Returning {} products", products.getTotalElements());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") @Positive Long id) {
        logger.info("Received request to get product by ID: {}", id);

        var product = productService.getProductById(id);

        logger.info("product found with ID {}: {}", id, product);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@RequestBody @Validated @NonNull ProductDTO product) {
        logger.info("Received request to add product: {}", product);

        var addedProduct = productService.addProduct(product);

        logger.info("product added successfully with ID: {}", addedProduct.getId());
        return new ResponseEntity<>(addedProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") @Positive Long id, @RequestBody @Validated ProductDTO product) {
        logger.info("Received request to update product with ID {}: {}", id, product);

        var updatedProduct = productService.updateProduct(id, product);

        logger.info("product updated successfully with ID: {}", id);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") @Positive Long id) {
        logger.info("Received request to delete product with ID: {}", id);

        productService.removeProduct(id);

        logger.info("product deleted successfully with ID: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

