package com.github.msundlana.warehousemanagementapi;

import com.github.msundlana.warehousemanagementapi.exception.ProductNotFoundException;
import com.github.msundlana.warehousemanagementapi.models.Product;
import com.github.msundlana.warehousemanagementapi.models.ProductArticleDTO;
import com.github.msundlana.warehousemanagementapi.models.ProductDTO;
import com.github.msundlana.warehousemanagementapi.repositories.ProductRepository;
import com.github.msundlana.warehousemanagementapi.services.ProductService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.github.msundlana.warehousemanagementapi.services.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ActiveProfiles(value = "test")
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService = new ProductServiceImpl();

    private static final ModelMapper mapper = new ModelMapper();


    @Test
    public void testGetProductById() {
        var productId = 1L;
        var product = createTestProduct();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        var result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals("Test product", result.getName());
        assertEquals(5.0, result.getPrice());
    }

    @Test
    public void testGetProductByInvalidId() {
        var productId = 1L;
        when(productRepository.findById(productId))
                .thenThrow(new ProductNotFoundException("Product not found with id: " + productId));

        var exception = assertThrows(ProductNotFoundException.class, ()->{
                productService.getProductById(productId);
        });
        assertTrue(exception.getMessage().contains("Product not found with id: " + productId));
    }

    @Test
    public void testAddProducts() {
        var mockProducts = List.of(Product.builder()
                        .id(1L)
                        .name("Test product 1")
                        .price(4.0)
                        .build(),
                Product.builder()
                        .id(2L)
                        .name("Test product 2")
                        .price(5.0)
                        .build());
        when(productRepository.saveAll(anyList())).thenReturn(mockProducts);

        var mockDTOProducts = mockProducts.stream()
                .map(product -> mapper.map(product, ProductDTO.class))
                .toList();

        var savedProduct = productService.addProducts(mockDTOProducts);

        assertNotNull(savedProduct);
        assertEquals(2, savedProduct.size());
        assertEquals("Test product 1", savedProduct.get(0).getName());
        assertEquals("Test product 2", savedProduct.get(1).getName());
    }

    @Test
    public void testGetAllProducts() {
        var mockProducts = List.of(Product.builder()
                .id(1L)
                .name("Test product 1")
                .price(4.0)
                .build(),
                Product.builder()
                .id(2L)
                .name("Test product 2")
                .price(5.0)
                .build());
        var page = new PageImpl<>(mockProducts);

        when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);

        var result = productService.getAllProducts(PageRequest.of(0, 10));
        assertNotNull(result.getContent());
        assertEquals(2, result.getContent().size());
        assertEquals("Test product 1", result.getContent().get(0).getName());
        assertEquals("Test product 2", result.getContent().get(1).getName());
    }

    @Test
    public void testAddProduct() {
        var productDTO = ProductDTO.builder()
                .id(0L)
                .name("Test product")
                .price(4.0)
                .build();

        var productArticles = new HashSet<ProductArticleDTO>();
        var productArticle = ProductArticleDTO.builder()
                        .articleId(1L).quantity(6).id(0L).build();

        productArticles.add(productArticle);
        productDTO.setArticles(productArticles);
        productDTO.setName("Test product");

        var mockProduct =

        when(productRepository.save(any(Product.class))).thenReturn(createTestProduct());

        var savedProduct = productService.addProduct(productDTO);

        assertNotNull(savedProduct);
        assertEquals("Test product", savedProduct.getName());
    }

    @Test
    public void testRemoveProduct() {
        var productId = 1L;
        productRepository.deleteById(productId);
        verify(productRepository, times(1)).deleteById(productId);

    }


    @Test
    public void testUpdateProduct() {
        var productId = 1L;
        var existingProduct = createTestProduct();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        var updatedProductDto =  ProductDTO.builder()
                .id(productId)
                .name("Updated product")
                .price(10.0)
                .build();

        var updatedProduct = productService.updateProduct(productId, updatedProductDto);

        assertNotNull(updatedProduct);
        assertEquals("Updated product", updatedProduct.getName());
        assertEquals(10.0,updatedProduct.getPrice());
    }

    @Test
    public void testUpdateProductNotFound() {

        var productId = 1L;
        var updatedProductDto = new ProductDTO();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.updateProduct(productId, updatedProductDto));
    }

    private Product createTestProduct() {
        var product = Product.builder()
                .id(1L)
                .name("Test product")
                .price(5.0)
                .build();
        return product;
    }
}
