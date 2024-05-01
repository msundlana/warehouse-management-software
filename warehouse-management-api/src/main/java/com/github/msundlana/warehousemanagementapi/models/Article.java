package com.github.msundlana.warehousemanagementapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Article")
public class Article {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @PositiveOrZero
    @Column(nullable = false)
    private int stock;

}
