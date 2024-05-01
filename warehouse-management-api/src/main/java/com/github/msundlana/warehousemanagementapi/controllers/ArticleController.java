package com.github.msundlana.warehousemanagementapi.controllers;

import com.github.msundlana.warehousemanagementapi.models.ArticleDTO;
import com.github.msundlana.warehousemanagementapi.services.ArticleService;
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
@RequestMapping("${api.base}/articles")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<Page<ArticleDTO>> getAllArticles(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer page,
                                                           @RequestParam(required = false, defaultValue = "20") @Positive Integer size) {
        logger.info("Received request to get all articles");

        var articles = articleService.getAllArticles(PageRequest.of(page, size));

        logger.info("Returning {} articles", articles.getTotalElements());
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable("id") @Positive Long id) {
        logger.info("Received request to get article by ID: {}", id);

        var article = articleService.getArticleById(id);

        logger.info("article found with ID {}: {}", id, article);
        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ArticleDTO> addArticle(@RequestBody @Validated @NonNull ArticleDTO article) {
        logger.info("Received request to add article: {}", article);

        var addedArticle = articleService.addArticle(article);

        logger.info("article added successfully with ID: {}", addedArticle.getId());
        return new ResponseEntity<>(addedArticle, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable("id") @Positive Long id, @RequestBody @Validated ArticleDTO article) {
        logger.info("Received request to update article with ID {}: {}", id, article);

        var updatedArticle = articleService.updateArticle(id, article);

        logger.info("article updated successfully with ID: {}", id);
        return new ResponseEntity<>(updatedArticle, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable("id") @Positive Long id) {
        logger.info("Received request to delete article with ID: {}", id);

        articleService.deleteArticle(id);

        logger.info("article deleted successfully with ID: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

