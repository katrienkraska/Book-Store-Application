package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("save should persist category")
    void testSaveCategory() {
        Category category = new Category();
        category.setName("History");

        Category saved = categoryRepository.save(category);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("History");
    }

    @Test
    @DisplayName("findById should return category when exists")
    void testFindById() {
        Category category = new Category();
        category.setName("Science");
        entityManager.persist(category);
        entityManager.flush();

        var found = categoryRepository.findById(category.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Science");
    }

    @Test
    @DisplayName("findAll should return all categories")
    void testFindAll() {
        Category c1 = new Category();
        c1.setName("A");
        entityManager.persist(c1);

        Category c2 = new Category();
        c2.setName("B");
        entityManager.persist(c2);

        entityManager.flush();

        var list = categoryRepository.findAll();

        assertThat(list).hasSize(2);
    }
}
