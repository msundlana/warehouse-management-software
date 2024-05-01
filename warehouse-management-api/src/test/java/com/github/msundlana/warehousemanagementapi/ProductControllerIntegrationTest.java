package com.github.msundlana.warehousemanagementapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.msundlana.warehousemanagementapi.exception.ProductNotFoundException;
import com.github.msundlana.warehousemanagementapi.models.ProductDTO;
import com.github.msundlana.warehousemanagementapi.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ProductService productService;

    private final String basePath = "/api/products";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllProduct() throws Exception {
        var products = List.of(
                ProductDTO.builder().name("Test Product 1")
                        .price(4.0)
                        .build(),

                ProductDTO.builder().name("Test Product 2")
                        .price(2.0)
                        .build()
        );

        var page = new PageImpl<>(products);

        when(productService.getAllProducts(any())).thenReturn(page);

        mockMvc.perform(get(basePath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content.[0].name").value("Test Product 1"))
                .andExpect(jsonPath("$.content.[1].name").value("Test Product 2"));
    }

    @Test
    public void testGetProductById() throws Exception {
        var productId = 1L;
        var product = ProductDTO.builder()
                .id(productId).name("Test Product")
                .price(4.0)
                .build();

        when(productService.getProductById(productId)).thenReturn(product);

        mockMvc.perform(get(basePath+"/" + productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(4.0));
    }

    @Test
    public void testGetProductByInvalidId() throws Exception {
        var productId = 1L;

        when(productService.getProductById(productId)).thenThrow(
                new ProductNotFoundException("Product not found with id: " + productId));;

        mockMvc.perform(get(basePath+"/" + productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProductByConstrainViolationId() throws Exception {
        var productId = 0L;

        mockMvc.perform(get(basePath+"/" + productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddProduct() throws Exception {
        var product = ProductDTO.builder()
                .id(1L).name("Test product")
                .price(4.0)
                .build();

        when(productService.addProduct(product)).thenReturn(product);

        mockMvc.perform(post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(4.0))
                .andExpect(jsonPath("$.vegetarian").value(true));

    }

    @Test
    public void testDeleteProduct() throws Exception {
        var productId = 1L;

        mockMvc.perform(delete(basePath+"/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateProduct() throws Exception {
        var productId = 1L;

        var updatedProduct = ProductDTO.builder()
                .id(productId).name("Updated Product")
                .price(6.0)
                .build();

        when(productService.updateProduct(productId,updatedProduct)).thenReturn(updatedProduct);

        mockMvc.perform(put(basePath+"/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.stock").value(6));

    }


}

