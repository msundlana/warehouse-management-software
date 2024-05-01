package com.github.msundlana.warehousemanagementapi.services;

import com.github.msundlana.warehousemanagementapi.models.*;
import com.github.msundlana.warehousemanagementapi.repositories.ProductArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class WarehouseServiceImpl extends BaseService<Product,ProductDTO> implements WarehouseService{

    private static final Logger logger = LoggerFactory.getLogger(WarehouseServiceImpl.class);
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductArticleService productArticleService;

    public WarehouseServiceImpl() {
        super(Product.class,ProductDTO.class);
    }

    @Override
    public List<ArticleDTO> loadArticles(List<ArticleDTO> articles){
        logger.info("Loading articles into the warehouse");
        return articleService.addArticles(articles);
    }

    @Override
    public List<ProductDTO> loadProducts(List<ProductDTO> products){
        logger.info("Loading products into the warehouse");
        return productService.addProducts(products);
    }

    @Override
    public Page<ProductInventoryDTO> getAllAvailableProducts(String query,int page, int pageSize) {

        logger.info("Retrieving all available products");
        var productPage  = productService.getAllProducts(PageRequest.of(page, pageSize))
                .map((productDTO -> {
                    var productArticle = productArticleService.getLinkProductArticleToProduct(productDTO.getId());
                    productDTO.setArticles(productArticle);
                    return productDTO;
                }));
        return productPage.map(this::mapToProductInventoryDTO);
    }

    public void sellProduct(Long productId) {

        logger.info("Selling product with ID: {}",productId);
        productService.removeProduct(productId);
    }


    private ProductInventoryDTO mapToProductInventoryDTO(ProductDTO product) {

        var productInventoryDTO = ProductInventoryDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .availableStock(getAvailableStock(product))
                .build();

        return productInventoryDTO;
    }

    private int getAvailableStock(ProductDTO product) {
        logger.info("Getting available stock of product {} with ID: {}",product.getName(),product.getId());
        var minStock = Integer.MAX_VALUE;
        for (var productArticle : product.getArticles()) {
            var article = productArticle.getArticle();
            var requiredStock = productArticle.getQuantity();
            var availableStock = article.getStock() / requiredStock;

            minStock = Math.min(minStock, availableStock);
        }
        return minStock;
    }
}
