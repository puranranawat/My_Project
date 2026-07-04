# Employee Management System

A production-ready **Spring Boot REST API** for managing employees and departments, designed as the foundation for an **AI-Powered DevSecOps Pipeline** using Jenkins, Trivy, SonarQube, Docker, Amazon ECR, Amazon EKS, and Google Gemini AI.

---

## Table of Contents

- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration Profiles](#configuration-profiles)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Docker](#docker)
- [CI/CD Pipeline](#cicd-pipeline)
- [DevSecOps Readiness](#devsecops-readiness)

---

## Architecture

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Controller  │────▶│   Service    │────▶│  Repository  │────▶│   Database   │
│   (REST)     │     │  (Business)  │     │    (JPA)     │     │  (MySQL/H2)  │
└─────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
       │                    │
       ▼                    ▼
┌─────────────┐     ┌──────────────┐
│     DTO      │     │    Mapper    │
│  (Request/   │     │  (Entity ↔   │
│   Response)  │     │    DTO)     │
└─────────────┘     └──────────────┘
```

**Layered Architecture:**
- **Controller** — REST endpoints, request validation, HTTP response handling
- **Service** — Business logic, transaction management, uniqueness validation
- **Repository** — Data access via Spring Data JPA
- **Mapper** — Entity ↔ DTO conversion (static utility classes)
- **Exception** — Global exception handling with consistent error responses
- **Config** — CORS, auditing, logging, security configuration

---

## Tech Stack

| Category       | Technology                          |
|---------------|-------------------------------------|
| Language       | Java 21                             |
| Framework      | Spring Boot 3.4.1                   |
| Persistence    | Spring Data JPA, Hibernate          |
| Database       | MySQL (prod), H2 (dev/test)         |
| Validation     | Jakarta Bean Validation             |
| Logging        | SLF4J + Logback                     |
| Build          | Maven 3.9+                          |
| Monitoring     | Spring Boot Actuator                |
| Code Gen       | Lombok                              |
| Testing        | JUnit 5, Mockito, MockMvc           |
| Coverage       | JaCoCo                              |
| Containerization | Docker (multi-stage build)        |
| Orchestration  | Kubernetes (Amazon EKS)             |
| CI/CD          | Jenkins (declarative pipeline)      |
| Code Quality   | SonarQube                           |
| Security Scan  | Trivy                               |
| Registry       | Amazon ECR                          |

---

## Project Structure

```
employee-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/devsecops/ems/
│   │   │   ├── EmsApplication.java
│   │   │   ├── config/
│   │   │   │   ├── AuditConfig.java
│   │   │   │   ├── LoggingInterceptor.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── DepartmentController.java
│   │   │   │   └── EmployeeController.java
│   │   │   ├── dto/
│   │   │   │   ├── ApiErrorResponse.java
│   │   │   │   ├── DepartmentRequest.java
│   │   │   │   ├── DepartmentResponse.java
│   │   │   │   ├── EmployeeRequest.java
│   │   │   │   └── EmployeeResponse.java
│   │   │   ├── entity/
│   │   │   │   ├── Department.java
│   │   │   │   ├── Employee.java
│   │   │   │   ├── EmployeeStatus.java
│   │   │   │   └── Gender.java
│   │   │   ├── exception/
│   │   │   │   ├── BadRequestException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── mapper/
│   │   │   │   ├── DepartmentMapper.java
│   │   │   │   └── EmployeeMapper.java
│   │   │   ├── repository/
│   │   │   │   ├── DepartmentRepository.java
│   │   │   │   └── EmployeeRepository.java
│   │   │   ├── service/
│   │   │   │   ├── DepartmentService.java
│   │   │   │   ├── EmployeeService.java
│   │   │   │   └── impl/
│   │   │   │       ├── DepartmentServiceImpl.java
│   │   │   │       └── EmployeeServiceImpl.java
│   │   │   └── util/
│   │   │       └── ApplicationConstants.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── logback-spring.xml
│   └── test/
│       ├── java/com/devsecops/ems/
│       │   ├── EmsApplicationTests.java
│       │   ├── controller/
│       │   ├── integration/
│       │   ├── mapper/
│       │   └── service/impl/
│       └── resources/
│           └── application-test.properties
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
├── Dockerfile
├── .dockerignore
├── .gitignore
├── Jenkinsfile
├── sonar-project.properties
├── pom.xml
└── README.md
```

---

## Prerequisites

- **Java 21** (JDK)
- **Maven 3.9+**
- **MySQL 8.0+** (only for production profile)
- **Docker** (for containerization)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-org/employee-management-system.git
cd employee-management-system
```

### 2. Run locally (dev profile — H2 database)

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080` with an H2 in-memory database. Access the H2 console at `http://localhost:8080/h2-console`.

### 3. Run with MySQL (prod profile)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod \
  -DDATABASE_URL=jdbc:mysql://localhost:3306/ems_db \
  -DDATABASE_USERNAME=root \
  -DDATABASE_PASSWORD=yourpassword
```

---

## Configuration Profiles

| Profile | Database | DDL Auto     | SQL Logging | Use Case          |
|---------|----------|-------------|-------------|-------------------|
| `dev`   | H2       | create-drop | Enabled     | Local development |
| `test`  | H2       | create-drop | Disabled    | Automated testing |
| `prod`  | MySQL    | validate    | Disabled    | Production        |

---

## API Endpoints

### Employees

| Method | Endpoint                   | Description           |
|--------|----------------------------|-----------------------|
| GET    | `/api/employees`           | List all employees    |
| GET    | `/api/employees/{id}`      | Get employee by ID    |
| POST   | `/api/employees`           | Create new employee   |
| PUT    | `/api/employees/{id}`      | Update employee       |
| DELETE | `/api/employees/{id}`      | Delete employee       |
| GET    | `/api/employees/search`    | Search by keyword     |

### Departments

| Method | Endpoint                   | Description             |
|--------|----------------------------|-------------------------|
| GET    | `/api/departments`         | List all departments    |
| GET    | `/api/departments/{id}`    | Get department by ID    |
| POST   | `/api/departments`         | Create new department   |
| PUT    | `/api/departments/{id}`    | Update department       |
| DELETE | `/api/departments/{id}`    | Delete department       |

### Actuator

| Method | Endpoint               | Description          |
|--------|------------------------|----------------------|
| GET    | `/actuator/health`     | Health check         |
| GET    | `/actuator/info`       | Application info     |
| GET    | `/actuator/metrics`    | Application metrics  |

---

## Testing

### Run all tests (unit + integration)

```bash
mvn clean verify
```

### Run unit tests only

```bash
mvn test
```

### Run integration tests only

```bash
mvn failsafe:integration-test failsafe:verify
```

### Code coverage report

After running `mvn clean verify`, the JaCoCo report is available at:

```
target/site/jacoco/index.html
```

---

## Docker

### Build the Docker image

```bash
docker build -t employee-management-system:1.0.0 .
```

### Run the container

```bash
docker run -d -p 8080:8080 \
  -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/ems_db \
  -e DATABASE_USERNAME=root \
  -e DATABASE_PASSWORD=yourpassword \
  employee-management-system:1.0.0
```

The container runs with the `prod` profile by default.

---

## CI/CD Pipeline

The `Jenkinsfile` defines a complete DevSecOps pipeline:

```
Checkout → Build & Test → SonarQube → Quality Gate → Docker Build → Trivy Scan → Push to ECR → Deploy to EKS → AI Review
```

Pipeline stages are prepared as placeholders for the next phase of integration.

---

## DevSecOps Readiness

| Tool          | Status    | File/Config                |
|--------------|-----------|----------------------------|
| GitHub        | ✅ Ready   | `.gitignore`              |
| Jenkins       | ✅ Ready   | `Jenkinsfile`             |
| SonarQube     | ✅ Ready   | `sonar-project.properties`, JaCoCo |
| Trivy         | ✅ Ready   | Dockerfile (non-root, Alpine) |
| Docker        | ✅ Ready   | `Dockerfile`, `.dockerignore` |
| Amazon ECR    | ✅ Ready   | Jenkinsfile ECR stage     |
| Amazon EKS    | ✅ Ready   | `k8s/deployment.yaml`, `k8s/service.yaml` |
| Maven Build   | ✅ Ready   | `mvn clean verify`        |
| Gemini AI     | ✅ Ready   | Jenkinsfile AI Review stage |

---

## License

This project is part of the AI-Powered DevSecOps Pipeline learning series.
