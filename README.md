# Book Store Application
**Book Store Application** is a RESTful backend application built with **Java** and **Spring Boot** that provides a complete solution for managing an online bookstore.
The system supports **JWT-based authentication**, **role-based authorization (USER / ADMIN)**, and core e-commerce features including **book and category management**, **shopping cart**, and **order processing**.
The project follows a **layered architecture** and modern backend best practices, making it scalable, secure, and suitable for real-world use cases as well as educational purposes.

---

## Features

- User registration and authentication (JWT)
- Role-based access control (`ROLE_USER`, `ROLE_ADMIN`)
- Books browsing with pagination and sorting
- Categories management (ADMIN-only creation)
- Shopping cart management
- Order creation from shopping cart
- Order history retrieval
- Swagger / OpenAPI documentation
- Database versioning with Liquibase
- Integration testing with Testcontainers (MySQL)

---

## Technologies Used

- **Java 17**
- **Spring Boot 3.4.1**
- **Spring Security + JWT (jjwt 0.12.6)**
- **Spring Data JPA**
- **Liquibase**
- **MySQL 8.0**
- **H2 Database** (optional runtime database)
- **Swagger / OpenAPI (springdoc 2.5.0)**
- **Docker**
- **Testcontainers (MySQL)**
- **JUnit 5**
- **Mockito**
- **MapStruct 1.5.5.Final**
- **Lombok 1.18.30**
- **Checkstyle**

---

## 🗂 Project Structure

The application follows a classic Spring Boot layered architecture:

```text
src/main/java/org/example
├── config        # Security & JWT configuration
├── controller    # REST controllers
├── dto           # Request / Response DTOs
├── exception     # Custom exceptions & global handler
├── mapper        # MapStruct mappers
├── model         # JPA entities
├── repository    # Spring Data repositories
├── service       # Business logic
└── validation    # Custom validators

src/main/resources
├── application.properties
└── db
    └── changelog   # Liquibase changelogs

src/test
└── unit and integration tests (Testcontainers)

## API Documentation (Swagger)
After starting the application, Swagger UI is available at:
http://localhost:8088/api/swagger-ui.html

## Contact
For questions or feedback, feel free to reach out:
GitHub: katrienkraska
Email: kate2002.kk42@gmail.com
