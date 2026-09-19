# Resource Booking System

A secure RESTful Resource Booking API built with **Java, Spring Boot, Spring Security, JWT, Spring Data JPA/Hibernate, and MySQL**.

The application supports role-based access control for `ADMIN` and `USER`, resource management, reservation management, reservation ownership, filtering, pagination, sorting, validation, and centralized error handling.

## Features

- JWT-based authentication using `POST /auth/login`
- Stateless authentication with Spring Security
- `ADMIN` and `USER` role-based access control
- ADMIN full CRUD for resources
- USER read-only access to resources
- USER can create reservations
- USER can view only their own reservations
- USER identity is taken from the authenticated JWT, not from the reservation request
- ADMIN can view and manage all reservations
- Reservation statuses: `PENDING`, `CONFIRMED`, `CANCELLED`
- Decimal reservation pricing using `BigDecimal`
- Reservation overlap validation
- Resource availability validation
- Filtering by status, minimum price, and maximum price
- Pagination using `page` and `size`
- Sorting using `sortBy` and `sortDirection`
- Request validation using Jakarta Validation
- Centralized exception handling
- BCrypt password hashing
- MySQL database using JPA/Hibernate
- Swagger/OpenAPI documentation
- Development-only seed users
- Unit, service, controller, JWT, and security tests

## Technology Stack

- Java 17+
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Hibernate
- Spring Security
- JJWT 0.12.6
- MySQL 8+
- Jakarta Validation
- Swagger / OpenAPI
- Maven
- JUnit 5
- Mockito

## Project Structure

```text
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
│   ├── BadRequestException.java
│   ├── ErrorResponse.java
│   ├── ForbiddenException.java
│   ├── ResourceNotFoundException.java
│   ├── UserNotFoundException.java
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

- Java 17 or later
- Maven 3.9+
- MySQL 8+
- Git

Verify Java and Maven:

```bash
java -version
mvn -version
```

## Database Setup

Create the MySQL database:

```sql
CREATE DATABASE resource_booking;
```

The required tables are created/updated automatically by JPA/Hibernate.

## Configuration

The application uses environment variables for database credentials, JWT configuration, and seed-user credentials.

File:

```text
src/main/resources/application.properties
```

Current configuration pattern:

```properties
spring.application.name=resource-booking

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/resource_booking}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.open-in-view=false

server.port=${SERVER_PORT:8081}

logging.level.org.springframework.security=INFO

jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:3600000}
jwt.issuer=resource-booking-api
jwt.audience=resource-booking-client

seed.admin.username=${ADMIN_USERNAME}
seed.admin.password=${ADMIN_PASSWORD}
seed.user.username=${USER_USERNAME}
seed.user.password=${USER_PASSWORD}
```

### Environment Variables

For local development, configure the following environment variables before starting the application:

```text
DB_URL=jdbc:mysql://localhost:3306/resource_booking
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

JWT_SECRET=your_long_random_secret_key_at_least_32_bytes
JWT_EXPIRATION=3600000

ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin

USER_USERNAME=user
USER_PASSWORD=password

SERVER_PORT=8081
```

### Local Demo Credentials

Use these credentials for local development when the same values are configured in the environment variables above:

| Role | Username | Password |
|---|---|---|
| ADMIN | `admin` | `admin` |
| USER | `user` | `password` |

These are **local/demo credentials only**. Do not use them in production. Change the passwords when deploying the application.

The application stores the passwords in the database using BCrypt hashing; the plain-text passwords are only used during login/seed configuration.

> Do not commit real passwords, JWT secrets, or `.env` files containing secrets to GitHub.

The JWT secret must be at least **32 bytes** long.

## Running the Application

Clone the repository:

```bash
git clone https://github.com/exelynt-learning-platform/backend-developer-as-final-12687-amar.git
cd backend-developer-as-final-12687-amar
```

Switch to the assignment branch if required:

```bash
git checkout backend-developer-assignment-deadline-30th-sep-2026-64051-2943
```

Set the required environment variables and run:

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8081
```

The application can also be started from Eclipse or IntelliJ IDEA.

## Seed Users

Seed users are enabled only when the `dev` profile is active.

Set the credentials through environment variables:

```text
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your_admin_password
USER_USERNAME=user
USER_PASSWORD=your_user_password
```

The passwords are stored in the database using BCrypt hashing.

## Authentication

### Login

```http
POST http://localhost:8081/auth/login
Content-Type: application/json
```

#### ADMIN login

```json
{
  "username": "admin",
  "password": "admin"
}
```

#### USER login

```json
{
  "username": "user",
  "password": "password"
}
```

Successful login returns a newly generated JWT:

```json
{
  "token": "<JWT_GENERATED_BY_THE_SERVER>"
}
```

The JWT **must not be hard-coded in the README** because it contains an expiration time and is generated using the configured `JWT_SECRET`. Login again to obtain a fresh token whenever the previous token expires.

For protected APIs, send the returned token in the Authorization header:

```http
Authorization: Bearer <JWT_TOKEN>
```

Example:

```http
GET http://localhost:8081/api/resources
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

The actual JWT value is returned by `POST /auth/login`. The token contains the username as its subject and is validated using the configured issuer, audience, signature, and expiration.

## API Endpoints

### Authentication

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/auth/login` | Public | Authenticate and receive JWT |

### Resources

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/resources` | USER, ADMIN | Get all resources |
| GET | `/api/resources/{id}` | USER, ADMIN | Get resource by ID |
| POST | `/api/resources` | ADMIN | Create resource |
| PUT | `/api/resources/{id}` | ADMIN | Update resource |
| DELETE | `/api/resources/{id}` | ADMIN | Delete resource |

### Reservations

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/reservations` | USER, ADMIN | Create reservation |
| GET | `/api/reservations/my` | USER, ADMIN | Get authenticated user's reservations |
| GET | `/api/reservations` | ADMIN | Get all reservations |
| GET | `/api/reservations/{id}` | USER, ADMIN | Get reservation by ID; USER ownership is checked |
| PUT | `/api/reservations/{id}` | ADMIN | Update reservation |
| DELETE | `/api/reservations/{id}` | ADMIN | Delete reservation |
| PUT | `/api/reservations/{id}/cancel` | ADMIN / Owner USER | Cancel reservation |

## Resource Request

Example:

```json
{
  "name": "Conference Room",
  "description": "Meeting room with projector",
  "type": "ROOM",
  "available": true,
  "price": 500.00
}
```

## Reservation Request

The request intentionally does **not** contain `userId` or `username`.

The authenticated user's identity is obtained from the JWT.

```json
{
  "resourceId": 1,
  "startDate": "2026-09-25T10:00:00",
  "endDate": "2026-09-25T12:00:00"
}
```

New reservations are created with:

```text
PENDING
```

For ADMIN updates, reservation status can be managed using:

```text
PENDING
CONFIRMED
CANCELLED
```

## Reservation Filtering

### Filter by status

```http
GET /api/reservations?status=PENDING
```

### Filter by minimum price

```http
GET /api/reservations?minPrice=400
```

### Filter by maximum price

```http
GET /api/reservations?maxPrice=600
```

### Filter by price range

```http
GET /api/reservations?minPrice=400&maxPrice=600
```

The same filters are available for the authenticated user's reservations:

```http
GET /api/reservations/my?status=PENDING&minPrice=400&maxPrice=600
```

If `minPrice` is greater than `maxPrice`, the API returns `400 Bad Request`.

## Pagination

Example:

```http
GET /api/reservations?page=0&size=5
```

- `page=0` is the first page.
- `size=5` returns up to five records.
- Page number cannot be negative.
- Page size must be greater than zero.

## Sorting

Sorting is supported using a controlled list of fields.

Example:

```http
GET /api/reservations?page=0&size=5&sortBy=price&sortDirection=desc
```

Supported sort fields:

```text
id
startDate
endDate
price
status
```

Supported directions:

```text
asc
desc
```

Invalid sort fields or directions return `400 Bad Request`.

## Reservation Business Rules

- A resource must be available before creating or updating a reservation.
- Start date must be before end date.
- A resource cannot have overlapping active reservations.
- Active reservations are `PENDING` and `CONFIRMED`.
- Reservation price is taken from the selected resource.
- New reservations start with `PENDING` status.
- USER can only view their own reservations.
- USER can cancel their own reservation.
- ADMIN can manage reservations according to the configured ADMIN endpoints.

## Authorization Rules

### ADMIN

ADMIN can:

- Create, read, update, and delete resources
- Create reservations
- View all reservations
- View individual reservations
- Update reservations
- Delete reservations
- Cancel reservations

### USER

USER can:

- View resources
- View individual resources
- Create reservations
- View only their own reservations
- Cancel their own reservations

USER cannot:

- Create, update, or delete resources
- View all reservations
- Update or delete reservations
- View another user's reservation
- Cancel another user's reservation

## Validation

The API validates request data including:

- Required username and password
- Required resource name and type
- Valid resource price
- Positive resource ID
- Required reservation start and end dates
- Start date must be before end date
- Non-negative page number
- Positive page size
- Valid sorting field and direction
- Valid minimum/maximum price range

## Error Handling

The application uses centralized exception handling through `GlobalExceptionHandler`.

### 400 Bad Request

Used for validation errors and invalid business requests.

Example:

```json
{
  "status": 400,
  "message": "Start date must be before end date",
  "path": "/api/reservations"
}
```

### 401 Unauthorized

Used when authentication fails or the JWT is invalid/expired.

Example:

```json
{
  "status": 401,
  "message": "Invalid username or password",
  "path": "/auth/login"
}
```

### 403 Forbidden

Used when an authenticated user does not have permission.

Example:

```json
{
  "status": 403,
  "message": "You can only view your own reservations",
  "path": "/api/reservations/10"
}
```

### 404 Not Found

Used when the requested resource, reservation, or user does not exist.

Example:

```json
{
  "status": 404,
  "message": "Resource not found",
  "path": "/api/resources/10"
}
```

### 500 Internal Server Error

Unexpected server-side errors are logged internally and a generic message is returned to the client.

## Swagger / OpenAPI

Swagger/OpenAPI configuration is enabled with the `dev` profile.

Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8081/v3/api-docs
```

The API documentation includes JWT Bearer authentication.

## Testing

The project contains tests for services, controllers, JWT functionality, authentication filters, and security configuration.

Run the complete test suite:

```bash
mvn clean test
```

Current test status:

```text
Tests run: 56
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

## Postman Testing Flow

Recommended test sequence:

1. Login as USER.
2. Login as ADMIN.
3. Get resources as USER.
4. Create a resource as ADMIN.
5. Create a reservation as USER.
6. View the USER's reservations.
7. Verify USER cannot view another user's reservation.
8. Verify USER can cancel their own reservation.
9. Verify USER cannot cancel another user's reservation.
10. Get all reservations as ADMIN.
11. Update a reservation as ADMIN.
12. Cancel a reservation as ADMIN.
13. Delete a reservation as ADMIN.
14. Test status filtering.
15. Test minimum and maximum price filtering.
16. Test pagination.
17. Test sorting.
18. Test validation errors.
19. Test invalid or expired JWT access.
20. Test ADMIN/USER authorization boundaries.

## Security

- Passwords are stored using BCrypt hashing.
- JWT provides stateless authentication.
- JWT issuer and audience are validated.
- JWT expiration is configurable.
- Spring Security manages role-based authorization.
- USER and ADMIN permissions are separated.
- Reservation ownership is checked using the authenticated username.
- `userId` is not accepted in the reservation request.
- Database credentials and JWT secrets are supplied through environment variables.
- JWT secret length is validated at application startup.
- Unexpected exceptions are logged server-side without exposing stack traces to clients.

## HTTP Status Codes

| Status | Meaning |
|---:|---|
| 200 | Successful request |
| 201 | Resource/reservation created |
| 204 | Successfully deleted |
| 400 | Invalid request or validation error |
| 401 | Authentication failed or JWT invalid |
| 403 | Access denied |
| 404 | Resource not found |
| 500 | Unexpected server error |

## Repository

GitHub Repository:

```text
https://github.com/exelynt-learning-platform/backend-developer-as-final-12687-amar.git
```

Assignment Branch:

```text
backend-developer-assignment-deadline-30th-sep-2026-64051-2943
```

## Author

**Amar Bhise**

## Assignment

**Backend Developer Assignment — Secure RESTful API for Resource Booking with JWT Authentication and Role-Based Access Control**
