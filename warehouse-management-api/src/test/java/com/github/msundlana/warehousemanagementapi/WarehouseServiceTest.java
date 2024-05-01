package com.github.msundlana.warehousemanagementapi;

import com.github.msundlana.warehousemanagementapi.models.*;
import com.github.msundlana.warehousemanagementapi.repositories.ProductArticleRepository;
import com.github.msundlana.warehousemanagementapi.repositories.ProductRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.github.msundlana.warehousemanagementapi.services.ArticleService;
import com.github.msundlana.warehousemanagementapi.services.ProductService;
import com.github.msundlana.warehousemanagementapi.services.WarehouseService;
import com.github.msundlana.warehousemanagementapi.services.WarehouseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ActiveProfiles(value = "test")
public class WarehouseServiceTest {
    @Mock
    private ArticleService articleService;

    @Mock
    private ProductService productService;

    @Mock
    private ProductArticleRepository productArticleRepository;

    @InjectMocks
    private WarehouseService warehouseService = new WarehouseServiceImpl();

    @Test
    public void testAddArticles() {
        var mockArticles = List.of(ArticleDTO.builder()
                        .id(1L)
                        .name("Test article 1")
                        .stock(4)
                        .build(),
                ArticleDTO.builder()
                        .id(2L)
                        .name("Test article 2")
                        .stock(10)
                        .build());
        when(articleService.addArticles(anyList())).thenReturn(mockArticles);

        var savedArticle = warehouseService.loadArticles(mockArticles);

        assertNotNull(savedArticle);
        assertEquals(2, savedArticle.size());
        assertEquals("Test article 1", savedArticle.get(0).getName());
        assertEquals("Test article 2", savedArticle.get(1).getName());
    }

    @Test
    public void testLoadProducts() {
        var mockProducts = List.of(ProductDTO.builder()
                        .id(1L)
                        .name("Test product 1")
                        .price(4.0)
                        .build(),
                ProductDTO.builder()
                        .id(2L)
                        .name("Test product 2")
                        .price(5.0)
                        .build());
        when(productService.addProducts(anyList())).thenReturn(mockProducts);

        var savedProduct = productService.addProducts(mockProducts);

        assertNotNull(savedProduct);
        assertEquals(2, savedProduct.size());
        assertEquals("Test product 1", savedProduct.get(0).getName());
        assertEquals("Test product 2", savedProduct.get(1).getName());
    }

    @Test
    public void testGetAllAvailableProducts() {
        var article1 = Article.builder()
                .id(1L)
                .name("Test Article 1")
                .stock(10)
                .build();

        var article2 = Article.builder()
                .id(2L)
                .name("Test Article 2")
                .stock(10)
                .build();

        var product1 = Product.builder()
                .name("Test Product")
                .price( 10.0)
                .build();

        var productArticles = List.of(
                ProductArticle.builder()
                        .id(1L)
                        .product(product1)
                        .article(article1)
                        .quantity(1)
                        .build()
                ,
                ProductArticle.builder()
                        .id(2L)
                        .product(product1)
                        .article(article2)
                        .quantity(2)
                        .build()
        );

        var page = new PageImpl<>(productArticles);

        when(productArticleRepository.findAllByArticleStockGreaterThanAndProductNameIgnoreCaseContaining(
                0,"",PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<ProductInventoryDTO> productInventoryDTO = warehouseService.getAllAvailableProducts("",0, 10);

        assertEquals(1, productInventoryDTO.getTotalElements());
        assertEquals("Product 1", productInventoryDTO.getContent().get(0).getName());
        assertEquals(5, productInventoryDTO.getContent().get(0).getAvailableStock());
    }

    @Test
    public void testGetSpecificAvailableProducts() {
        var article1 = Article.builder()
                .id(1L)
                .name("Test Article 1")
                .stock(10)
                .build();

        var article2 = Article.builder()
                .id(2L)
                .name("Test Article 2")
                .stock(10)
                .build();

        var product1 = Product.builder()
                .name("Test Product 1")
                .price( 10.0)
                .build();


        var productArticles = List.of(
                ProductArticle.builder()
                        .id(1L)
                        .product(product1)
                        .article(article1)
                        .quantity(1)
                        .build()
                ,
                ProductArticle.builder()
                        .id(2L)
                        .product(product1)
                        .article(article2)
                        .quantity(2)
                        .build()
        );

        var page = new PageImpl<>(productArticles);

        when(productArticleRepository.findAllByArticleStockGreaterThanAndProductNameIgnoreCaseContaining(
                0,"",PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<ProductInventoryDTO> productInventoryDTO = warehouseService.getAllAvailableProducts("Product",0, 10);

        assertEquals(1, productInventoryDTO.getTotalElements());
        assertEquals("Product 1", productInventoryDTO.getContent().get(0).getName());
        assertEquals(5, productInventoryDTO.getContent().get(0).getAvailableStock());
    }

    @Test
    public void testNoAvailableProducts() {
        // Mock data
        var article1 = Article.builder()
                .id(1L)
                .name("Test Article 1")
                .stock(0)
                .build();

        var article2 = Article.builder()
                .id(2L)
                .name("Test Article 2")
                .stock(10)
                .build();

        var product1 = Product.builder()
                .name("Test Product 1")
                .price( 10.0)
                .build();


        var productArticles = List.of(
                ProductArticle.builder()
                        .id(1L)
                        .product(product1)
                        .article(article1)
                        .quantity(1)
                        .build()
                ,
                ProductArticle.builder()
                        .id(2L)
                        .product(product1)
                        .article(article2)
                        .quantity(2)
                        .build()
        );

        var page = new PageImpl<>(productArticles);

        when(productArticleRepository.findAllByArticleStockGreaterThanAndProductNameIgnoreCaseContaining(
                0,"",PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<ProductInventoryDTO> productInventoryDTO = warehouseService.getAllAvailableProducts("Product",0, 10);

        assertNotNull(productInventoryDTO);
        assertEquals(0, productInventoryDTO.getTotalElements());
    }

    @Test
    public void testSellProduct() {
        var productId = 1L;

        warehouseService.sellProduct(productId);

        verify(productService, times(1)).removeProduct(productId);
    }


}
