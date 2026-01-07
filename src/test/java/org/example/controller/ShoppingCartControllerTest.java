package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.model.Book;
import org.example.model.CartItem;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.example.repository.BookRepository;
import org.example.repository.CartItemRepository;
import org.example.repository.ShoppingCartRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:database/delete-all-data.sql")
class ShoppingCartControllerTest {
    @Container
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL8Dialect");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private User savedUser;
    private ShoppingCart savedCart;
    private Book savedBook;

    @BeforeEach
    void setup() {
        User u = new User();
        u.setEmail("user@mail.com");
        u.setPassword("pass");
        u.setFirstName("Test");
        u.setLastName("User");
        savedUser = userRepository.save(u);

        ShoppingCart cart = new ShoppingCart();
        cart.setUser(savedUser);
        savedCart = shoppingCartRepository.save(cart);

        Book b = new Book();
        b.setTitle("Test Book");
        b.setAuthor("Author");
        b.setIsbn("ISBN-CART-001");
        b.setPrice(BigDecimal.TEN);
        savedBook = bookRepository.save(b);
    }

    private void setPrincipalIdToSavedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        user.setId(savedUser.getId());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getByUserId() throws Exception {
        setPrincipalIdToSavedUser();

        String json = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ShoppingCartDto actual = objectMapper.readValue(json, ShoppingCartDto.class);

        assertThat(actual.getId()).isEqualTo(savedCart.getId());
        assertThat(actual.getUserId()).isEqualTo(savedUser.getId());
        assertThat(actual.getCartItems()).isEmpty();
    }

    @Test
    @WithMockUser(roles = "USER")
    void save() throws Exception {
        setPrincipalIdToSavedUser();

        String body = """
                { "bookId": %d, "quantity": 2 }
                """.formatted(savedBook.getId());

        String json = mockMvc.perform(post("/cart")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ShoppingCartDto actual = objectMapper.readValue(json, ShoppingCartDto.class);

        assertThat(actual.getUserId()).isEqualTo(savedUser.getId());
        assertThat(actual.getCartItems()).hasSize(1);

        var item = actual.getCartItems().iterator().next();
        assertThat(item.getBookId()).isEqualTo(savedBook.getId());
        assertThat(item.getBookTitle()).isEqualTo(savedBook.getTitle());
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    @WithMockUser(roles = "USER")
    void update() throws Exception {
        setPrincipalIdToSavedUser();

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(savedCart);
        cartItem.setBook(savedBook);
        cartItem.setQuantity(1);
        CartItem savedItem = cartItemRepository.save(cartItem);

        String body = """
                { "quantity": 7 }
                """;

        String json = mockMvc.perform(put("/cart/items/" + savedItem.getId())
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ShoppingCartDto actual = objectMapper.readValue(json, ShoppingCartDto.class);

        assertThat(actual.getCartItems()).hasSize(1);
        var item = actual.getCartItems().iterator().next();
        assertThat(item.getBookId()).isEqualTo(savedBook.getId());
        assertThat(item.getQuantity()).isEqualTo(7);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteById() throws Exception {
        setPrincipalIdToSavedUser();

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(savedCart);
        cartItem.setBook(savedBook);
        cartItem.setQuantity(1);
        CartItem savedItem = cartItemRepository.save(cartItem);

        mockMvc.perform(delete("/cart/items/" + savedItem.getId()))
                .andExpect(status().isNoContent());

        assertThat(cartItemRepository.findById(savedItem.getId())).isEmpty();
    }
}
