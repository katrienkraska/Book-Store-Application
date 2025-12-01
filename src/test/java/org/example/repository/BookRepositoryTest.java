package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Book;
import org.example.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testFindByCategoriesId() {
        Category category = new Category();
        category.setName("Fiction");
        categoryRepository.save(category);

        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setIsbn("123-456-789");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.getCategories().add(category);

        bookRepository.save(book);

        List<Book> result = bookRepository.findByCategoriesId(category.getId());

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
    }

    @Test
    @DisplayName("findByCategoriesId should return empty list when category has no books")
    void testFindByCategoryId_NoBooks() {
        Category category = new Category();
        category.setName("Empty");
        entityManager.persist(category);

        entityManager.flush();

        List<Book> result = bookRepository.findByCategoriesId(category.getId());

        assertThat(result).isEmpty();
    }
}
