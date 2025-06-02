# skillbook
A full-stack Java web app to share and book skill-based sessions. This repository contains the backend API. The frontend is implemented in React and can be found in the skillbook-ui repository.

## Tech Stack

### Backend
- Java 21
- Spring Boot 3.4.5
- Spring Security for authentication and authorization
- Spring Data JPA for data persistence
- PostgreSQL database
- Lombok for reducing boilerplate code
- SpringDoc OpenAPI for API documentation

### Development Tools
- Maven for dependency management
- JUnit for testing
- Spring DevTools for development
- IntelliJ IDEA as recommended IDE

## Features
- User authentication and registration
- Role-based access control (Admin, Instructor, Learner)
- Course management system
- Session booking functionality
- RESTful API endpoints
- Swagger UI for API documentation

## Getting Started

### Prerequisites
- Java 21 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

### Configuration
1. Clone the repository
2. Create a .env and configure secrets/credentials there
3. Set the required environment variables:
   - `DB_PASSWORD`: PostgreSQL database password

### Running the Application
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Documentation
Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
