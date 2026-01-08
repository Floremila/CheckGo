
# CheckGo – Degree Project

This repository contains the backend part of **CheckGo**, developed as part of my **degree project**.

The project is based on an existing backend created during a Java EE course and was later refactored and extended into a fullstack application.  
The focus of the degree project was refactoring, frontend integration, database migration, and cloud deployment to create a production-like product.

---

## Degree Project Scope

The focus of the degree project was to:

- Refactor an existing Spring Boot backend from a previous Java EE course
- Separate frontend and backend responsibilities
- Integrate a standalone frontend built with React (Vite)
- Migrate the database from H2 to PostgreSQL for production use
- Deploy the backend to Render and the frontend to Vercel
- Integrate RabbitMQ as part of the system architecture
- Verify functionality through structured manual end-to-end testing

This repository represents the **final backend solution** used in the deployed product.

## Deployment

- **Frontend (Vercel):** https://checkgo-frontend.vercel.app
- **Backend (Render):** https://checkgo.onrender.com

The frontend consumes the backend API deployed on Render.  
Both services are configured using environment variables and deployed independently.

---

## Architecture Overview

The API is designed around the following core principles:

### Security-first architecture
- JWT-based authentication
- Stateless request processing
- Role-based access control
- Password hashing using BCrypt
- Centralized exception handling

### Event-driven integration
- RabbitMQ-backed activation flow
- User registration triggers an activation message
- A listener processes activation and updates account state

### Modular domain logic
- User domain
- Task domain
- Admin domain

All modules communicate via clean DTOs and follow separation of concerns.

---

## Features

### Authentication & Authorization
- Register and login with JWT token handling
- Stateless Spring Security configuration
- Role-based access for protected endpoints
- Default user role: ROLE_USER
- Support for admin-specific operations

### Task Management Module
CRUD operations for authenticated users:

| Method | Endpoint | Description |
|------|---------|------------|
| GET | /api/tasks | Retrieve tasks for current user |
| POST | /api/tasks | Create a new task |
| PUT | /api/tasks/{id} | Update an existing task |
| DELETE | /api/tasks/{id} | Delete a task |

Tasks include title, description, status, due date, audit timestamps, and ownership enforcement.

### Event-Driven User Activation
The API publishes an event when a new user registers.  
RabbitMQ handles the activation flow asynchronously.

### Persistence
- H2 (local development)
- PostgreSQL (Docker-based environments and production)
- JPA with Hibernate
- Environment-based configuration using Spring profiles

### Admin Module
ROLE_ADMIN users can view all registered users:

| Method | Endpoint |
|------|---------|
| GET | /api/admin/users |

---

## Technology Stack
- Java 17
- Spring Boot 3
- Spring Security (JWT)
- Hibernate / JPA
- PostgreSQL
- RabbitMQ
- Docker & Docker Compose
- OpenAPI / Swagger
- Lombok
- Cloud deployment (Render)

---

## Running the Application

### With Docker (recommended)
Runs the full system with PostgreSQL and RabbitMQ.
```
docker compose up --build
```

### Without Docker
Runs the backend locally using H2 for development purposes.
Event-driven features require RabbitMQ to be available.
```
mvn spring-boot:run
```

---

## Development Utilities
- Swagger UI: http://localhost:8080/swagger-ui/index.html

---

## Project Structure
```
src/main/java/se/floremila/checkgo
├── advice/
├── config/
├── controller/
├── dto/
├── entity/
├── messaging/
├── repository/
├── security/
└── service/
```

---

## Documentation

The project is documented using:
- GitHub commit history
- Screenshots from development and debugging
- A lightweight logbook describing decisions, issues, solutions, and lessons learned

This documentation supports reflection and evaluation in the final report.

---

## Possible Improvements

- Automated testing for core backend functionality
- Improved monitoring and logging in production
- Additional frontend features and UI enhancements

### User feedback–driven improvement

During manual testing with users, a usability improvement was identified in the registration flow.

When password validation fails (for example, due to insufficient length), the backend correctly rejects the request.  
However, the frontend currently displays a generic error message.

A potential future improvement would be to surface more specific, user-friendly validation feedback (such as password requirements), so users can immediately understand what needs to be corrected.
