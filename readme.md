# Checkgo API

Checkgo is a secure, containerized REST API built with **Spring Boot 3**, providing user authentication, authorization, and a modular task-management feature.  
The system follows modern security standards, supports asynchronous event handling through **RabbitMQ**, and runs fully inside Docker.

## 1. Architecture Overview

The API is designed around three core principles:

1. **Security-first architecture**
    - JWT-based authentication
    - Stateless request processing
    - Role-based access control
    - Password hashing using BCrypt
    - Centralized exception handling

2. **Event-driven integration**
    - A RabbitMQ-backed activation flow
    - User registration triggers an activation message
    - A listener processes activation and updates account state

3. **Modular domain logic**
    - User domain
    - Task domain
    - Admin domain

All modules communicate via clean DTOs and follow separation of concerns.

## 2. Features

### 🔐 Authentication & Authorization
- Register, login and JWT token handling
- Stateless Spring Security configuration
- Role-based access for protected endpoints
- Default user role: `ROLE_USER`
- Support for admin-specific operations

### 📝 Task Management Module
CRUD operations for authenticated users:

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | Retrieve tasks for current user |
| POST | `/api/tasks` | Create a new task |
| PUT | `/api/tasks/{id}` | Update an existing task |
| DELETE | `/api/tasks/{id}` | Delete task |

Tasks include:
- Title
- Description
- Status
- Due date
- Audit timestamps
- Ownership enforcement

### 🛰 Event-Driven User Activation
The API publishes an event when a new user registers.  
RabbitMQ handles the activation flow:
- **Publisher** sends `userId`
- **Listener** activates user asynchronously

### 🗄 Persistence
- File-based H2 storage (`jdbc:h2:file:./data/checkgo`)
- Data persists across container restarts
- JPA with Hibernate and automatic schema updates

### 🧩 Admin Module
`ROLE_ADMIN` users can view all registered users:

| Method | Endpoint |
|--------|----------|
| GET | `/api/admin/users` |

## 3. Technology Stack
- Java 17
- Spring Boot 3
- Spring Security (JWT)
- Hibernate / JPA
- RabbitMQ
- Docker & Docker Compose
- H2 Persistent Database
- Lombok

## 4. Running the Application

### With Docker (recommended)
```
docker compose up --build
```

### Without Docker
```
mvn spring-boot:run
```

## 5. Development Utilities
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 Console: `http://localhost:8080/h2-console`  
  JDBC: `jdbc:h2:file:./data/checkgo-db`

## 6. Project Structure
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

## 7. Future Enhancements
- React-based frontend
- Email notification microservice
- Multi-environment Docker setup
- PostgreSQL production profile  