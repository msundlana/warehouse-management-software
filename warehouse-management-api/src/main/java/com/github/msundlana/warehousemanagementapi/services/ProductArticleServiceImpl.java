package com.github.msundlana.warehousemanagementapi.services;

import com.github.msundlana.warehousemanagementapi.models.Article;
import com.github.msundlana.warehousemanagementapi.models.Product;
import com.github.msundlana.warehousemanagementapi.models.ProductArticle;
import com.github.msundlana.warehousemanagementapi.models.ProductArticleDTO;
import com.github.msundlana.warehousemanagementapi.repositories.ProductArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductArticleServiceImpl extends BaseService<ProductArticle, ProductArticleDTO> implements ProductArticleService{
    private static final Logger logger = LoggerFactory.getLogger(ProductArticleServiceImpl.class);
    private final ProductArticleRepository productArticleRepository;

    public ProductArticleServiceImpl(ProductArticleRepository productArticleRepository) {
        super(ProductArticle.class, ProductArticleDTO.class);

        this.productArticleRepository = productArticleRepository;
    }

    @Override
    public ProductArticleDTO linkProductArticleToProduct(Long productId, Long articleId, int quantity) {
        var  existingProductArticle= productArticleRepository
                .findByProductIdAndArticleId(productId, articleId);
        if (existingProductArticle.isPresent()) {
            return convertToDto(existingProductArticle.get());
        }
        logger.info("Linking article id: {} to product id: {}",articleId,productId );
        var productRef = Product.builder().id(productId).build();
        var articleRef = Article.builder().id(articleId).build();
        var productArticle = ProductArticle.builder()
                .product(productRef)
                .article(articleRef)
                .quantity(quantity)
                .build();

        var result = productArticleRepository.save(productArticle);

        return convertToDto(result);
    }

    public Set<ProductArticleDTO> getLinkProductArticleToProduct(Long productId){
        var productArticle = productArticleRepository.findAllByProductId(productId);
        return productArticle.stream().map(this::convertToDto).collect(Collectors.toSet());
    }

}
