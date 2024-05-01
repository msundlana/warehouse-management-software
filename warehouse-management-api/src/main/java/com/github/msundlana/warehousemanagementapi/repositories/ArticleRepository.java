package com.github.msundlana.warehousemanagementapi.repositories;

import com.github.msundlana.warehousemanagementapi.models.Article;
import com.github.msundlana.warehousemanagementapi.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

}

