# Resource Booking System

A secure RESTful Resource Booking API built with Spring Boot, Spring
Security, JWT authentication, JPA/Hibernate, and MySQL.

## Features

-   JWT-based login using `POST /auth/login`
-   `ADMIN` and `USER` role-based access control
-   ADMIN full CRUD for resources and reservations
-   USER read-only access to resources
-   USER can create reservations and view only their own reservations
-   USER identity is taken from the JWT, not from the reservation
    request
-   Reservation statuses: `PENDING`, `CONFIRMED`, `CANCELLED`
-   Decimal reservation pricing using `BigDecimal`
-   Filtering by status, minimum price, and maximum price
-   Pagination using `page` and `size`
-   Optional sorting using `sortBy` and `sortDirection`
-   Request validation and centralized error handling
-   MySQL with JPA/Hibernate
-   Swagger/OpenAPI documentation
-   Seed ADMIN and USER accounts

## Technology Stack

-   Java 17+
-   Spring Boot
-   Spring Web
-   Spring Data JPA
-   Hibernate
-   Spring Security
-   JWT / JJWT
-   MySQL
-   Jakarta Validation
-   Swagger / OpenAPI
-   Maven

## Project Structure

``` text
src/main/java/com/amar/resource_booking
├── config
│   ├── DataInitializer.java
│   └── OpenApiConfig.java
├── controller
│   ├── AuthController.java
│   ├── ResourceController.java
│   └── ReservationController.java
├── dto
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── ResourceRequest.java
│   ├── ResourceResponse.java
│   ├── ReservationRequest.java
│   └── ReservationResponse.java
├── entity
│   ├── User.java
│   ├── Role.java
│   ├── Resource.java
│   ├── Reservation.java
│   └── ReservationStatus.java
├── exception
│   ├── ErrorResponse.java
│   ├── ForbiddenException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── repository
│   ├── UserRepository.java
│   ├── ResourceRepository.java
│   └── ReservationRepository.java
├── security
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
└── service
    ├── AuthService.java
    ├── ResourceService.java
    └── ReservationService.java
```

## Prerequisites

Install the following before running the application:

-   Java 17 or later
-   Maven
-   MySQL 8+
-   Git

## Database Setup

Create the database:

``` sql
CREATE DATABASE resource_booking;
```

The required tables are created/updated by JPA/Hibernate.

## Configuration

Configure the application in:

``` text
src/main/resources/application.properties
```

Recommended configuration:

``` properties
spring.application.name=resource-booking

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/resource_booking}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8081

jwt.secret=${JWT_SECRET}
```

### Environment Variables

Use these variables for local or deployment environments:

``` text
DB_URL=jdbc:mysql://localhost:3306/resource_booking
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_long_random_jwt_secret
```

Do not commit real passwords, JWT secrets, or `.env` files containing
secrets to GitHub.

## Run the Application

Clone the repository:

``` bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd resource-booking
```

Run with Maven:

``` bash
mvn spring-boot:run
```

The application runs on:

``` text
http://localhost:8081
```

You can also run the main Spring Boot application class from Eclipse or
IntelliJ IDEA.

## Seed Users

The application creates the following users automatically if they do not
already exist.

### USER

``` text
Username: user
Password: password
Role: USER
```

### ADMIN

``` text
Username: admin
Password: admin
Role: ADMIN
```

These credentials are for local testing.

## Authentication

### Login

``` http
POST /auth/login
```

Request:

``` json
{
  "username": "user",
  "password": "password"
}
```

Response:

``` json
{
  "token": "JWT_TOKEN"
}
```

Use the token for protected APIs:

``` text
Authorization: Bearer <JWT_TOKEN>
```

## API Endpoints

### Authentication

  Method   Endpoint        Access   Description
  -------- --------------- -------- -----------------------
  POST     `/auth/login`   Public   Login and receive JWT

### Resources

  Method   Endpoint                Access        Description
  -------- ----------------------- ------------- --------------------
  GET      `/api/resources`        USER, ADMIN   Get all resources
  GET      `/api/resources/{id}`   USER, ADMIN   Get resource by ID
  POST     `/api/resources`        ADMIN         Create resource
  PUT      `/api/resources/{id}`   ADMIN         Update resource
  DELETE   `/api/resources/{id}`   ADMIN         Delete resource

### Reservations

  Method   Endpoint                   Access        Description
  -------- -------------------------- ------------- --------------------------------------------------
  POST     `/api/reservations`        USER, ADMIN   Create reservation
  GET      `/api/reservations/my`     USER          Get logged-in user\'s reservations
  GET      `/api/reservations`        ADMIN         Get all reservations
  GET      `/api/reservations/{id}`   USER, ADMIN   Get reservation by ID; USER ownership is checked
  PUT      `/api/reservations/{id}`   ADMIN         Update reservation
  DELETE   `/api/reservations/{id}`   ADMIN         Delete reservation

## Resource Request

Example:

``` json
{
  "name": "Conference Room",
  "description": "Meeting room with projector",
  "type": "ROOM",
  "available": true,
  "price": 500.00
}
```

## Reservation Request

The request does not contain `userId`. The logged-in user is identified
from the JWT.

``` json
{
  "resourceId": 1,
  "startDate": "2026-09-15T10:00:00",
  "endDate": "2026-09-15T12:00:00"
}
```

New reservations are created with status:

``` text
PENDING
```

For ADMIN reservation updates, the status can be managed using:

``` text
PENDING
CONFIRMED
CANCELLED
```

## Filtering

Filter by status:

``` text
GET /api/reservations?status=PENDING
```

Filter by minimum price:

``` text
GET /api/reservations?minPrice=400
```

Filter by maximum price:

``` text
GET /api/reservations?maxPrice=600
```

Filter by both:

``` text
GET /api/reservations?minPrice=400&maxPrice=600
```

USER can use the same filters on:

``` text
GET /api/reservations/my
```

Example:

``` text
GET /api/reservations/my?status=PENDING&minPrice=400&maxPrice=600
```

## Pagination

Example:

``` text
GET /api/reservations?page=0&size=5
```

-   `page=0` is the first page.
-   `size=5` returns up to five records.

## Sorting

Sorting is optional.

Example:

``` text
GET /api/reservations?page=0&size=5&sortBy=price&sortDirection=desc
```

## Authorization Rules

### ADMIN

ADMIN can:

-   Create, read, update, and delete resources
-   Create reservations
-   View all reservations
-   View individual reservations
-   Update reservations
-   Delete reservations

### USER

USER can:

-   View resources
-   View individual resources
-   Create reservations
-   View only their own reservations

USER cannot:

-   Create, update, or delete resources
-   View all reservations
-   Update or delete reservations
-   View another user\'s reservation

## Validation

The API validates request data including:

-   Required username and password
-   Required resource name and type
-   Required and non-negative resource price
-   Positive resource ID
-   Required reservation start and end dates
-   Start date must be before end date
-   Non-negative page number
-   Positive page size

## Error Responses

### 400 Bad Request {#400-bad-request}

Used for validation errors or invalid request data.

Example:

``` json
{
  "status": 400,
  "message": "Start date must be before end date"
}
```

### 401 Unauthorized {#401-unauthorized}

Used when authentication fails.

Example:

``` json
{
  "status": 401,
  "message": "Invalid username or password"
}
```

### 403 Forbidden {#403-forbidden}

Used when an authenticated user does not have permission.

Example:

``` json
{
  "status": 403,
  "message": "You can only view your own reservations"
}
```

### 404 Not Found {#404-not-found}

Used when a resource or reservation does not exist.

Example:

``` json
{
  "status": 404,
  "message": "Resource not found with id: 10"
}
```

## Swagger / OpenAPI {#swagger--openapi}

Swagger UI:

``` text
http://localhost:8081/swagger-ui.html
```

OpenAPI JSON:

``` text
http://localhost:8081/v3/api-docs
```

## Postman Testing

Recommended test flow:

1.  Login as USER
2.  Get resources as USER
3.  Create a reservation as USER
4.  Get USER\'s reservations
5.  Try to access another user\'s reservation
6.  Login as ADMIN
7.  Get all reservations
8.  Create, update, and delete resources as ADMIN
9.  Update reservation status as ADMIN
10. Delete a reservation as ADMIN
11. Test status filtering
12. Test minimum and maximum price filtering
13. Test pagination
14. Test sorting
15. Test validation errors
16. Test invalid authentication

## Security

-   Passwords are stored using BCrypt hashing.
-   JWT provides stateless authentication.
-   Spring Security manages role-based authorization.
-   USER and ADMIN permissions are separated.
-   Reservation ownership is checked using the authenticated username.
-   `userId` is not accepted in the reservation request.
-   Database credentials and JWT secrets should be supplied through
    environment variables.

## HTTP Status Codes

  Status   Meaning
  -------- -------------------------------------
  200      Successful request
  201      Resource/reservation created
  204      Successfully deleted
  400      Invalid request or validation error
  401      Authentication failed
  403      Access denied
  404      Resource not found

## Author

**Amar Bhise**

## Assignment

Backend Developer Assignment

**Secure RESTful API for Resource Booking with JWT Authentication and
Role-Based Access Control**
