package org.example;

import model.Book;
import service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;

@SpringBootApplication
public final class BookAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(BookService bookService) {
        return args -> {
            Book book = new Book();
            book.setTitle("Head First. Java");
            book.setAuthor("Katie Sierra");
            book.setIsbn("12345");
            book.setPrice(new BigDecimal("23.50"));
            bookService.save(book);
        };
    }
}
