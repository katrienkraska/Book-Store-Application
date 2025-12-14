package org.example.repository;

import org.example.model.Book;
import org.example.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("findByCategoriesId should return books linked to this category")
    void testFindByCategoriesId() {
        Category category = new Category();
        category.setName("Fiction");
        Category savedCategory = categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setIsbn("123-456-789");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.getCategories().add(savedCategory);
        Book savedBook = bookRepository.save(book);

        List<Book> result = bookRepository.findByCategoriesId(savedCategory.getId());

        assertThat(result).hasSize(1);

        Book found = result.get(0);

        assertThat(found.getId()).isNotNull();
        assertThat(found.getTitle()).isEqualTo(savedBook.getTitle());
        assertThat(found.getAuthor()).isEqualTo(savedBook.getAuthor());
        assertThat(found.getIsbn()).isEqualTo(savedBook.getIsbn());
        assertThat(found.getPrice()).isEqualTo(savedBook.getPrice());

        assertThat(found.getCategories()).hasSize(1);

        Category foundCategory = found.getCategories()
                .stream()
                .findFirst()
                .orElseThrow();

        assertThat(foundCategory.getId()).isEqualTo(savedCategory.getId());
        assertThat(foundCategory.getName()).isEqualTo(savedCategory.getName());
    }

    @Test
    @DisplayName("findByCategoriesId should return empty list when category has no books")
    void testFindByCategoryId_NoBooks() {
        Category category = new Category();
        category.setName("Empty");
        Category savedCategory = categoryRepository.save(category);

        List<Book> result = bookRepository.findByCategoriesId(savedCategory.getId());

        assertThat(result).isEmpty();
    }
}
