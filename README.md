This application demostrates how to create, Save , update, delete and search REST APIs.

I tried to implement 3 APIs that are User, Order and Product that are the main components of ecommerce application

Key Features:

Layered Architecture - Clean separation: Controller → Service → Repository

Pagination & Sorting - Handle large datasets efficiently

Caching - Caffeine cache to reduce database load

Validation - Input validation with custom error messages

Exception Handling - Global exception handler with proper HTTP status codes

DTOs - Separate domain models from API contracts

Transaction Management - Proper @Transactional usage

Logging - SLF4J for monitoring and debugging

Scalability Features:

Database connection pooling (built-in with Spring Boot)
Caching strategy to reduce database queries
Pagination to avoid loading large datasets
HTTP/2 and compression enabled
Stateless design for horizontal scaling

Technology stack:
- Spring Boot
- JPA/Hibernate
- PostgresSQL - to store data
