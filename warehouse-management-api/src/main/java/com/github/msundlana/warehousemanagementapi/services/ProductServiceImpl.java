package com.github.msundlana.warehousemanagementapi.services;

import com.github.msundlana.warehousemanagementapi.exception.ProductNotFoundException;
import com.github.msundlana.warehousemanagementapi.models.Product;
import com.github.msundlana.warehousemanagementapi.models.ProductArticle;
import com.github.msundlana.warehousemanagementapi.models.ProductArticleDTO;
import com.github.msundlana.warehousemanagementapi.models.ProductDTO;
import com.github.msundlana.warehousemanagementapi.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProductServiceImpl extends BaseService<Product,ProductDTO> implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductArticleService productArticleService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    public ProductServiceImpl() {
        super(Product.class, ProductDTO.class);
    }

    private static final ModelMapper mapper = new ModelMapper();

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        logger.info("Retrieving all products with pagination: {}", pageable);
        var products = productRepository.findAll(pageable);
        logger.info("Retrieved {} products", products.getTotalElements());
        return products.map(this::convertToDto);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        logger.info("Retrieving product by ID: {}", id);
        var product = findProductById(id);
        logger.info("product found: {}", product);
        return this.convertToDto(product);
    }

    @Override
    @Transactional
    public List<ProductDTO> addProducts(List<ProductDTO> productDTOLists){
        logger.info("Adding {} products", productDTOLists.size());
        var savedProductDTOList = new ArrayList<ProductDTO>();
        for (var productDTO:productDTOLists) {
            var savedProduct = saveProduct(productDTO);
            var savedProductDTO = convertToDto(savedProduct);
            savedProductDTO.setArticles(linkProductArticleToProduct(savedProduct.getId(),productDTO));
            savedProductDTOList.add(savedProductDTO);

        }
        logger.info("{} products added successfully", savedProductDTOList.size());
        return savedProductDTOList;
    }
    @Override
    public ProductDTO addProduct(ProductDTO productDto) {
        logger.info("Adding new product: {}", productDto);
        var product = this.convertToEntity(productDto);
        var savedProduct = productRepository.save(product);
        var savedProductDTO = convertToDto(savedProduct);
        savedProductDTO.setArticles(linkProductArticleToProduct(savedProduct.getId(),savedProductDTO));
        logger.info("Product added: {}", product);
        return this.convertToDto(product);
    }

    @Override
    public ProductDTO updateProduct(long id, ProductDTO productDto) {
        logger.info("Updating Product with ID {}: {}", id, productDto);
        var product = findProductById(id);
        mapper.map(productDto, product);
        var savedProduct = productRepository.save(product);
        logger.info("Product updated: {}", savedProduct);
        return this.convertToDto(savedProduct);
    }

    @Override
    public void removeProduct(long id) {
        logger.info("Selling product with ID: {}", id);
        var product = findProductById(id);
        for (var productArticle: product.getArticles()) {
            var soldArticle = articleService.sellArticle(
                    productArticle.getArticle().getId()
                    ,productArticle.getQuantity());
            if(soldArticle.getStock()<productArticle.getQuantity()){
                productRepository.deleteById(id);
                logger.info("Product with ID: {} sold out", id);
            }else {
                logger.info("Product sold with ID: {}", id);
            }
        }

    }

    private Product findProductById(long id){
        return productRepository.findById(id).orElseThrow(()
                -> new ProductNotFoundException("Product not found with id: " + id));
    }

    private Product saveProduct(ProductDTO productDTO){
        var product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice()).build();
        return productRepository.save(product);
    }

    private Set<ProductArticleDTO> linkProductArticleToProduct(long productID,ProductDTO product){
        var productArticles = new HashSet<ProductArticleDTO>();
        for (var productArticle: product.getArticles()) {
            var result = productArticleService.linkProductArticleToProduct(productID
                    ,productArticle.getArticleId(),productArticle.getQuantity());
            productArticles.add(result);

        }
        return productArticles;
    }

}
