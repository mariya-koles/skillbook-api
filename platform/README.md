# Skillbook Platform

A robust fullstack Java application for session booking and management. This platform enables efficient scheduling and management of sessions between service providers and clients.

## 🚀 Features

- User authentication and authorization
- Session booking and management
- RESTful API endpoints
- Interactive web interface
- Secure data handling
- API documentation with Swagger UI

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.4.5
- **Database**: PostgreSQL
- **Security**: Spring Security
- **Frontend**: Thymeleaf
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven
- **Utilities**: Lombok

## 📋 Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Your favorite IDE (IntelliJ IDEA recommended)

## 🔧 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone [repository-url]
   cd skillbook-platform
   ```

2. **Configure PostgreSQL**
   - Create a new database
   - Update `src/main/resources/application.properties` with your database credentials

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   Or use the provided wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will be available at `http://localhost:8080`

## 📚 API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🏗️ Project Structure

```
src/main/java/com/skillbook/platform/
├── controller/    # REST controllers
├── model/        # Domain entities
├── dto/          # Data Transfer Objects
├── service/      # Business logic
├── repository/   # Data access layer
├── security/     # Security configurations
├── config/       # Application configs
└── enums/        # Enumeration types
```

## 🔒 Security

The application implements Spring Security with:
- User authentication
- Role-based authorization
- Secure session management
- Protected API endpoints

## 🧪 Testing

Run the tests using:
```bash
mvn test
```

## 🛠️ Development

1. **Hot Reloading**
   - The application uses Spring Boot DevTools
   - Changes will be automatically reflected without restart

2. **Code Style**
   - Follow Java coding conventions
   - Use Lombok annotations to reduce boilerplate
   - Keep methods focused and single-responsibility



