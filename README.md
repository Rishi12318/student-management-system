# Student Management System

A production-grade, complete Student Management System REST API built using **Spring Boot 3.2.x**, **Spring Security 6.x**, **JPA/Hibernate**, and **MySQL**.

## System Architecture

The application implements a robust multi-layered architectural pattern:
- **API/Controller Layer**: Exposes RESTful endpoints, handles HTTP requests/responses, and performs input validation (`@Valid`).
- **Service Layer**: Contains core business logic, transactional boundaries (`@Transactional`), and security authorization logic.
- **Repository Layer**: Data access using Spring Data JPA, mapping directly to MySQL.
- **Security/JWT Layer**: Handles stateless JWT authentication, password encryption using BCrypt, and CORS configuration.

---

## Technical Stack & Dependencies

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Security**: 6.x (Stateless session management, JWT authorization)
- **Database**: MySQL 8.x
- **API Documentation**: SpringDoc OpenAPI 3 (Swagger UI)
- **Lombok**: Boilerplate reduction (getters, setters, builders, loggers)
- **Testing**: JUnit 5, Mockito, AssertJ, H2 Database (for integration testing)

---

## Project Structure

```
student-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/studentms/
│   │   │   ├── controller/      # REST API Controllers (Auth, Student, Admin)
│   │   │   ├── service/         # Business Logic Interfaces and Implementations
│   │   │   ├── repository/      # Spring Data JPA Repository Interfaces
│   │   │   ├── model/           # JPA Entities (User, Student, Admin, Course, etc.)
│   │   │   ├── dto/             # Data Transfer Objects (Requests & Responses)
│   │   │   ├── security/        # JWT Authentication entry point, filters, and helper utilities
│   │   │   ├── exception/       # Custom Exception types & Global Exception Handler
│   │   │   ├── config/          # OpenAPI, Security, and CORS configurations
│   │   │   └── StudentManagementApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/com/studentms/
│           ├── service/         # Service layer Unit Tests
│           ├── controller/      # Controller layer integration tests
│           └── StudentManagementApplicationTests.java
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## Local Setup Instructions

### Prerequisites
1. **Java Development Kit (JDK) 17** installed.
2. **Maven 3.8+** installed.
3. **MySQL Server** running locally.

### Database Setup
1. Create a MySQL database named `student_management_db`:
   ```sql
   CREATE DATABASE student_management_db;
   ```
2. Update the database credentials in `src/main/resources/application-dev.properties` if they differ from standard settings (`root`/`root`).

### Running the Application
To run the project in development mode:
```bash
mvn spring-boot:run
```

### Running Tests
To execute both unit and integration tests:
```bash
mvn test
```

---

## API Documentation (Swagger UI)

Once the application is running, you can access the interactive Swagger API documentation and try out the endpoints at:
- **URL**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Details JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## Docker Deployment (Docker Compose)

The application is completely Docker-ready. You can run the entire database and application stack locally with a single command:
```bash
docker-compose up --build
```
This command starts:
1. A **MySQL 8.0** database container running on port `3306`.
2. The **Spring Boot** application running on port `8080` (pre-configured to connect to the MySQL container).
