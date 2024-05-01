package com.github.msundlana.warehousemanagementapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.msundlana.warehousemanagementapi.exception.ArticleNotFoundException;
import com.github.msundlana.warehousemanagementapi.models.ArticleDTO;
import com.github.msundlana.warehousemanagementapi.models.ArticleDTO;
import com.github.msundlana.warehousemanagementapi.services.ArticleService;
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
public class ArticleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ArticleService articleService;

    private final String basePath = "/api/articles";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllArticles() throws Exception {
        var articles = List.of(
                ArticleDTO.builder().name("Test Articles 1")
                        .build(),

                ArticleDTO.builder().name("Test Articles 2")
                       .build()
        );

        var page = new PageImpl<>(articles);

        when(articleService.getAllArticles(any())).thenReturn(page);

        mockMvc.perform(get(basePath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content.[0].name").value("Test Article 1"))
                .andExpect(jsonPath("$.content.[1].name").value("Test Article 2"));
    }

    @Test
    public void testGetArticleById() throws Exception {
        var articleId = 1L;
        var article = ArticleDTO.builder()
                .id(articleId).name("Test Article")
                .stock(4)
                .build();

        when(articleService.getArticleById(articleId)).thenReturn(article);

        mockMvc.perform(get(basePath+"/" + articleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Article"))
                .andExpect(jsonPath("$.stock").value(4));
    }

    @Test
    public void testGetArticleByInvalidId() throws Exception {
        var articleId = 1L;

        when(articleService.getArticleById(articleId)).thenThrow(
                new ArticleNotFoundException("Article not found with id: " + articleId));;

        mockMvc.perform(get(basePath+"/" + articleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetArticleByConstrainViolationId() throws Exception {
        var articleId = 0L;

        mockMvc.perform(get(basePath+"/" + articleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddArticle() throws Exception {
        var article = ArticleDTO.builder()
                .id(1L).name("Test Article")
                .stock(4)
                .build();

        when(articleService.addArticle(article)).thenReturn(article);

        mockMvc.perform(post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(article)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test article"))
                .andExpect(jsonPath("$.stock").value(4));

    }

    @Test
    public void testDeleteArticle() throws Exception {
        var articleId = 1L;

        mockMvc.perform(delete(basePath+"/{id}", articleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateArticle() throws Exception {
        var articleId = 1L;

        var updatedArticle = ArticleDTO.builder()
                .id(articleId).name("Updated Article")
                .stock(6)
                .build();

        when(articleService.updateArticle(articleId,updatedArticle)).thenReturn(updatedArticle);

        mockMvc.perform(put(basePath+"/{id}", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedArticle)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Article"))
                .andExpect(jsonPath("$.stock").value(6));

    }


}

