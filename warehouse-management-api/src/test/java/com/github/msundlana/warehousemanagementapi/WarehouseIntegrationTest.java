package com.github.msundlana.warehousemanagementapi;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.msundlana.warehousemanagementapi.models.*;
import com.github.msundlana.warehousemanagementapi.services.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class WarehouseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    WarehouseService warehouseService;

    private final String basePath = "/api/warehouse";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLoadArticleInventory() throws Exception {
        var article1 = ArticleDTO.builder()
                .id(1L)
                .name("Test Article 1")
                .stock(10)
                .build();

        var article2 = ArticleDTO.builder()
                .id(2L)
                .name("Test Article 2")
                .stock(10)
                .build();

        when(warehouseService.loadArticles(anyList())).thenReturn(List.of(article1,article2));

        mockMvc.perform(get(basePath+"/articles/load")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content.[0].name").value("Test Article 1"))
                .andExpect(jsonPath("$.content.[1].name").value("Test Article 2"));
    }

    @Test
    public void testLoadProductInventory() throws Exception {

        var product1 = ProductDTO.builder()
                .name("Test Product")
                .price( 10.0)
                .build();

        when(warehouseService.loadProducts(anyList())).thenReturn(Collections.singletonList(product1));

        mockMvc.perform(get(basePath+"/products/load")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].name").value("Test Product 1"));
    }



    @Test
    public void testGetProductInventory() throws Exception {
        var article1 = ArticleDTO.builder()
                .id(1L)
                .name("Test Article 1")
                .stock(10)
                .build();

        var article2 = ArticleDTO.builder()
                .id(2L)
                .name("Test Article 2")
                .stock(10)
                .build();

        var product1 = ProductDTO.builder()
                .name("Test Product")
                .price( 10.0)
                .build();

//        var productArticles = List.of(
//                ProductArticleDTO.builder()
//                        .id(1L)
//                        .product(product1)
//                        .article(article1)
//                        .quantity(1)
//                        .build()
//                ,
//                ProductArticleDTO.builder()
//                        .id(2L)
//                        .product(product1)
//                        .article(article2)
//                        .quantity(2)
//                        .build()
//        );

//        var productInventories = productArticles.stream().map(productArticle->mapToProductInventoryDTO(productArticle)).toList();
//
//        var page = new PageImpl<>(productInventories);
//
//        when(warehouseService.getAllAvailableProducts("",0, 10)).thenReturn(page);

        mockMvc.perform(get(basePath+"/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content.[0].name").value("Test Product 1"));
    }


    @Test
    public void testSellProduct() throws Exception {
        var productId = 1L;

        mockMvc.perform(delete(basePath+"/products/{productId}/sell", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


    private ProductInventoryDTO mapToProductInventoryDTO(ProductDTO product) {

        var productInventoryDTO = ProductInventoryDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .availableStock(3)
                .build();

        return productInventoryDTO;
    }

}

