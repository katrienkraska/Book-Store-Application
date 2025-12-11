package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Save should persist category")
    void testSaveCategory() {
        Category category = new Category();
        category.setName("History");

        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(category);
    }

    @Test
    @DisplayName("FindById should return category when exists")
    void testFindById() {
        Category category = new Category();
        category.setName("Science");
        entityManager.persist(category);
        entityManager.flush();

        var foundOptional = categoryRepository.findById(category.getId());

        assertThat(foundOptional).isPresent();

        Category found = foundOptional.get();
        assertThat(found.getId()).isEqualTo(category.getId());
        assertThat(found.getName()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("FindAll should return all categories")
    void testFindAll() {
        Category category1 = new Category();
        category1.setName("A");
        entityManager.persist(category1);

        Category category2 = new Category();
        category2.setName("B");
        entityManager.persist(category2);

        entityManager.flush();

        List<Category> list = categoryRepository.findAll();

        assertThat(list).hasSize(2);
        assertThat(list)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("A", "B");
    }
}
