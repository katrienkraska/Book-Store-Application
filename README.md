# Book Store Application
Book Store Application is a RESTful backend application built with Java and Spring Boot that provides a complete solution for managing an online bookstore.  
The system supports JWT-based authentication, role-based authorization (USER / ADMIN), and core e-commerce features including book and category management, shopping cart and order processing.
The project is designed using a layered architecture and modern backend practices, making it scalable, secure, and suitable for real-world use cases or educational purposes.

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
- Docker & Docker Compose support
- Integration testing with Testcontainers (MySQL)

---

## Technologies Used
Java 17 — core programming language
Spring Boot 3.4.1 — application framework
Spring Security + JWT (jjwt 0.12.6) — authentication and authorization
Spring Data JPA — persistence layer
Liquibase — database migrations
MySQL 8.0 — primary relational database
H2 Database — optional runtime database
Swagger / OpenAPI (springdoc 2.5.0) — API documentation
Docker & Docker Compose — containerization and local deployment
Testcontainers (MySQL) — integration testing
JUnit 5 — testing framework
Mockito — mocking in tests
MapStruct 1.5.5.Final — entity ↔ DTO mapping
Lombok 1.18.30 — boilerplate code reduction
Checkstyle — code quality enforcement

## Project Structure
The application follows a classic Spring Boot layered architecture:

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
src/main/resources — application properties & Liquibase changelogs
src/test — unit and integration tests (Testcontainers)

## Controllers Overview

AuthenticationController (`/api/auth`):
Handles user registration and login.
- `POST /api/auth/registration` — register a new user (Public)
- `POST /api/auth/login` — authenticate user and return JWT token (Public)

BookController (/api/books):
Provides endpoints for browsing and managing books.
- `GET /api/books` — get paginated list of books (ROLE_USER)
- `GET /api/books/{id}` — get book by ID (ROLE_USER)
- `POST /api/books` — create a new book (ROLE_ADMIN)
- `PUT /api/books/{id}` — update an existing book (ROLE_ADMIN)
- `DELETE /api/books/{id}` — delete a book (ROLE_ADMIN)

CategoryController (`/api/categories`)
Provides endpoints for managing book categories and retrieving books by category.
- `POST /api/categories` — create a new category (ROLE_ADMIN)
- `GET /api/categories` — get paginated list of categories (ROLE_USER)
- `GET /api/categories/{id}` — get category by ID (ROLE_USER)
- `PUT /api/categories/{id}` — update category (ROLE_ADMIN)
- `DELETE /api/categories/{id}` — delete category (ROLE_ADMIN)
- `GET /api/categories/{id}/books` — get books by category ID (ROLE_USER)

ShoppingCartController (`/api/cart`)
Provides endpoints for managing the authenticated user's shopping cart.
- `GET /api/cart` — retrieve authenticated user's cart (ROLE_USER)
- `POST /api/cart` — add item to shopping cart (ROLE_USER)
- `PUT /api/cart/items/{cartItemId}` — update item in shopping cart (ROLE_USER)
- `DELETE /api/cart/items/{cartItemId}` — remove item from shopping cart (ROLE_USER)

OrderController (`/api/orders`)
Provides endpoints for creating orders and working with order items.
- `POST /api/orders` — place an order using the shopping cart (ROLE_USER)
- `GET /api/orders` — get authenticated user's order history (paginated) (ROLE_USER)
- `PATCH /api/orders/{id}` — update order status (ROLE_ADMIN)
- `GET /api/orders/{orderId}/items` — get paginated order items by order ID (ROLE_USER)
- `GET /api/orders/{orderId}/items/{id}` — get specific order item by ID (ROLE_USER)

## API Documentation (Swagger)
After starting the application, Swagger UI is available at:
http://localhost:8088/api/swagger-ui.html

## Docker & Docker Compose
The project is fully containerized and can be run using Docker Compose.

docker-compose.yml
yaml
Копировать код
services:
  mysql:
    image: mysql:8.0
    restart: unless-stopped
    env_file: .env
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQLDB_ROOT_PASSWORD}
      - MYSQL_DATABASE=${MYSQLDB_DATABASE}
      - MYSQL_USER=${MYSQLDB_USER}
      - MYSQL_PASSWORD=${MYSQLDB_PASSWORD}
    ports:
      - "${MYSQLDB_LOCAL_PORT}:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  bookstore:
    depends_on:
      - mysql
    build: .
    image: bookstore
    restart: on-failure
    env_file: .env
    ports:
      - "${SPRING_LOCAL_PORT}:${SPRING_DOCKER_PORT}"
      - "${DEBUG_PORT}:${DEBUG_PORT}"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/${MYSQLDB_DATABASE}
      SPRING_DATASOURCE_USERNAME: ${MYSQLDB_USER}
      SPRING_DATASOURCE_PASSWORD: ${MYSQLDB_PASSWORD}
      SPRING_DATASOURCE_DRIVER_CLASS_NAME: com.mysql.cj.jdbc.Driver
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
      JAVA_TOOL_OPTIONS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT}"

volumes:
  mysql_data:

## Environment Configuration
Example .env file 
# MySQL
MYSQLDB_USER=bookuser
MYSQLDB_PASSWORD=Mate12345*
MYSQLDB_DATABASE=bookstore
MYSQLDB_LOCAL_PORT=3307
MYSQLDB_DOCKER_PORT=3306
MYSQLDB_ROOT_PASSWORD=Mate12345*

# Spring Boot
SPRING_LOCAL_PORT=8088
SPRING_DOCKER_PORT=8080
DEBUG_PORT=5005

# JWT
JWT_EXPIRATION=3000000
JWT_SECRET=your_jwt_secret_key

Setup & Run
1. Clone the repository
git clone https://github.com/katrienkraska/Book-Store-Application
cd Book-Store-Application

2. Build the project
mvn clean package

3. Run with Docker Compose
docker compose build
docker compose up

Testing
Run all tests (unit + integration):
mvn test
Docker must be running for Testcontainers.

Code Quality
Checkstyle validation:
mvn verify

## Contact
For questions or feedback, feel free to reach out:
GitHub: katrienkraska
Email: kate2002.kk42@gmail.com
