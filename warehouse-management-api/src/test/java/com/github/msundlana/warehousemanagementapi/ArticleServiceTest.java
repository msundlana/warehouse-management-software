package com.github.msundlana.warehousemanagementapi;

import com.github.msundlana.warehousemanagementapi.exception.ArticleNotFoundException;
import com.github.msundlana.warehousemanagementapi.models.Article;
import com.github.msundlana.warehousemanagementapi.models.ArticleDTO;

import com.github.msundlana.warehousemanagementapi.repositories.ArticleRepository;
import com.github.msundlana.warehousemanagementapi.services.ArticleService;
import com.github.msundlana.warehousemanagementapi.services.ArticleServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ActiveProfiles(value = "test")
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @InjectMocks
    private ArticleService articleService = new ArticleServiceImpl();

    private static final ModelMapper mapper = new ModelMapper();


    @Test
    public void testSellArticle() {
        var articleId = 1L;
        var existingArticle = createTestArticle();

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(existingArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(existingArticle);


        var updatedArticle = articleService.sellArticle(articleId, 2);

        assertNotNull(updatedArticle);
        assertEquals("Test article", updatedArticle.getName());
        assertEquals(4,updatedArticle.getStock());

    }

    @Test
    public void testSellAndRemoveArticle() {
        var articleId = 1L;
        var existingArticle = createTestArticle();

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(existingArticle));

        articleService.sellArticle(articleId, 6);

        assertFalse(articleRepository.existsById(articleId));

    }


    @Test
    public void testGetArticleById() {
        var articleId = 1L;
        var article = createTestArticle();

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        var result = articleService.getArticleById(articleId);

        assertNotNull(result);
        assertEquals("Test article", result.getName());
        assertEquals(6.0, result.getStock());
    }

    @Test
    public void testGetArticleByInvalidId() {
        var articleId = 1L;
        when(articleRepository.findById(articleId))
                .thenThrow(new ArticleNotFoundException("Article not found with id: " + articleId));

        var exception = assertThrows(ArticleNotFoundException.class, ()->{
                articleService.getArticleById(articleId);
        });
        assertTrue(exception.getMessage().contains("Article not found with id: " + articleId));
    }

    @Test
    public void testAddArticles() {
        var mockArticles = List.of(Article.builder()
                        .id(1L)
                        .name("Test article 1")
                        .stock(4)
                        .build(),
                Article.builder()
                        .id(2L)
                        .name("Test article 2")
                        .stock(10)
                        .build());
        when(articleRepository.saveAll(anyList())).thenReturn(mockArticles);

        var mockDTOArticles = mockArticles.stream()
                .map(article -> mapper.map(article, ArticleDTO.class))
                .toList();

        var savedArticle = articleService.addArticles(mockDTOArticles);

        assertNotNull(savedArticle);
        assertEquals(2, savedArticle.size());
        assertEquals("Test article 1", savedArticle.get(0).getName());
        assertEquals("Test article 2", savedArticle.get(1).getName());
    }

    @Test
    public void testGetAllArticles() {
        var mockArticles = List.of(Article.builder()
                .id(1L)
                .name("Test article 1")
                .stock(4)
                .build(),
                Article.builder()
                .id(2L)
                .name("Test article 2")
                .stock(10)
                .build());
        var page = new PageImpl<>(mockArticles);

        when(articleRepository.findAll(any(PageRequest.class))).thenReturn(page);

        var result = articleService.getAllArticles(PageRequest.of(0, 10));
        assertNotNull(result.getContent());
        assertEquals(2, result.getContent().size());
        assertEquals("Test article 1", result.getContent().get(0).getName());
        assertEquals("Test article 2", result.getContent().get(1).getName());
    }

    @Test
    public void testAddArticle() {
        var mockArticle = createTestArticle();
        when(articleRepository.save(any(Article.class))).thenReturn(mockArticle);

        var mockDTOArticle = mapper.map(mockArticle,ArticleDTO.class);

        var savedArticle = articleService.addArticle(mockDTOArticle);

        assertNotNull(savedArticle);
        assertEquals(1L,savedArticle.getId());
        assertEquals("Test article", savedArticle.getName());
    }

    @Test
    public void testDeleteArticle() {
        var articleId = 1L;
        articleService.deleteArticle(articleId);

        assertFalse(articleRepository.existsById(articleId));

    }

    @Test
    public void testUpdateArticle() {
        var articleId = 1L;
        var existingArticle = createTestArticle();

        var updatedArticle =  Article.builder()
                .id(articleId)
                .name("Updated article")
                .stock(4)
                .build();

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(existingArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(updatedArticle);

        var updatedArticleDto =  ArticleDTO.builder()
                .id(articleId)
                .name("Updated article")
                .stock(4)
                .build();

        var updatedArticleDTO = articleService.updateArticle(articleId, updatedArticleDto);

        assertNotNull(updatedArticleDTO);
        assertEquals("Updated article", updatedArticleDTO.getName());
        assertEquals(4,updatedArticleDTO.getStock());
    }

    @Test
    public void testUpdateArticleNotFound() {

        var articleId = 1L;
        var updatedArticleDto = new ArticleDTO();

        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> articleService.updateArticle(articleId, updatedArticleDto));
    }

    private Article createTestArticle() {
        var article = Article.builder()
                .id(1L)
                .name("Test article")
                .stock(6)
                .build();
        return article;
    }
}
