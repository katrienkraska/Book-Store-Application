package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Table(name = "books")
@SQLRestriction("deleted = false")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(nullable = false)
    @NotBlank
    private String author;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String isbn;

    @Column(nullable = false)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    @Size(max = 1000)
    private String description;

    private String coverImage;

    @Column(nullable = false)
    private boolean isDeleted = false;
}
