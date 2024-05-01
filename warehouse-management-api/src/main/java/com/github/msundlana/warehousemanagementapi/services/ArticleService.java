package com.github.msundlana.warehousemanagementapi.services;

import com.github.msundlana.warehousemanagementapi.models.ArticleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {
    Page<ArticleDTO> getAllArticles(Pageable pageable) ;

    ArticleDTO getArticleById(Long id) ;

    List<ArticleDTO> addArticles(List<ArticleDTO> articles);

    ArticleDTO addArticle(ArticleDTO articleDTO);

    ArticleDTO updateArticle(long id, ArticleDTO articleDTO);

    ArticleDTO sellArticle(long id, int quantity);
    void deleteArticle(long id);
}
