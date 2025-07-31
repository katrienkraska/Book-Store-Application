package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.book.BookDto;
import org.example.dto.book.CreateBookRequestDto;
import org.example.service.book.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book API", description = "Book operations")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Get a list of all books with pagination",
            description = "Fetches a paginated and sorted list of all available books. "
                    + "Sorting can be applied by providing the sorting criteria "
                    + "in the request parameters. "
                    + "The sorting criteria should be specified in the format: "
                    + "'sort: field,(ASC||DESC)' "
                    + "where 'ASC' is ascending and 'DESC' is descending. "
                    + "Default sorting is by in ascending order. "
                    + "Only users with 'USER' authority can access this endpoint."
    )
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public Page<BookDto> findAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get book by ID",
            description = "Fetches a book from the system using its unique identifier. "
                    + "Only users with 'USER' authority can access this endpoint.")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}")
    public BookDto getBookById(
            @Parameter(description = "ID book", required = true)
            @PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @Operation(summary = "Create a new book",
            description = "Adds a new book to the catalog. Only users with "
                    + "'ADMIN' authority can perform this operation. "
                    + "The request body must contain valid book details."
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New book details")
            @RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.createBook(bookDto);
    }

    @Operation(summary = "Update an existing book",
            description = "Updates an existing book identified by the given ID. "
                    + "Only users with 'ADMIN' authority can perform this operation. "
                    + "The request body must contain valid book details."
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public BookDto update(
            @Parameter(description = "Book ID to update", required = true)
            @PathVariable Long id,
            @RequestBody @Valid CreateBookRequestDto updateBook) {
        return bookService.update(id, updateBook);
    }

    @Operation(summary = "Delete book by ID",
            description = "Deletes a book identified by the given ID. "
                    + "Only users with 'ADMIN' authority can perform this operation."
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Book ID to delete", required = true)
            @PathVariable Long id) {
        bookService.delete(id);
    }
}
