# Water Meter Reading Application

Backend service for managing water meter readings with photo upload and analytics.

## Features

- User authentication with JWT
- Meter management (hot/cold water)
- Reading management with photo upload
- Chart visualization of readings and consumption
- Automatic reading recognition (MVP implementation)
- RESTful API with OpenAPI documentation

## Technology Stack

- Java 21
- Spring Boot 3
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Liquibase
- MinIO (S3-compatible storage)
- MapStruct
- Lombok
- OpenAPI/Swagger

## Prerequisites

- Docker and Docker Compose
- Java 21
- Maven

## Getting Started

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean package
   ```

3. Start the services:
   ```bash
   docker-compose up -d --build
   ```

4. Access the application:
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html or http://localhost:8080/swagger-ui/index.html
- API Docs: http://localhost:8080/v3/api-docs
   - MinIO Console: http://localhost:9003

## API Documentation

Full API documentation is available through Swagger UI:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

### Key Endpoints

#### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login user
- `POST /auth/refresh` - Refresh access token

#### Meters
- `POST /meters` - Create meter
- `GET /meters` - Get user meters
- `PATCH /meters/{id}` - Update meter

#### Readings
- `POST /readings` - Create reading with photo (multipart/form-data) - requires meterId, value, and photo file
- `GET /readings?meterId={id}` - Get meter readings
- `GET /readings/files/{filename}` - Get photo file
- `PATCH /readings/{id}` - Update reading
- `DELETE /readings/{id}` - Delete reading (soft delete)

#### Recognition
- `POST /recognize` - Recognize reading from photo (multipart/form-data) - returns recognized value without saving to database

#### Charts
- `GET /meters/{id}/chart/readings` - Get readings chart
- `GET /meters/{id}/chart/consumption` - Get consumption chart

##### Chart Aggregation Logic
Charts support different time periods with smart data aggregation to optimize performance:

- **WEEK**: No aggregation - shows all individual readings
- **MONTH**: Aggregates by day - groups readings by day, showing maximum value per day
- **YEAR**: Aggregates by month - groups readings by month, showing maximum value per month
- **ALL**: Aggregates by month - groups all readings by month, showing maximum value per month

Maximum 100 data points returned to prevent overload.

#### Statistics
- `GET /meters/{id}/stats` - Get meter statistics

## Demo Credentials

- Login: `demo`
- Password: `demo1234`

## Project Structure

```
src/main/java/com/example/meters
├── config          # Configuration classes
├── security        # JWT and security configuration
├── controller      # REST controllers
├── service         # Business logic
├── repository      # Data access layers
├── entity          # JPA entities
├── dto             # Data transfer objects
├── mapper          # MapStruct mappers
├── exception       # Exception handling
├── storage         # File storage service
├── recognition     # Reading recognition service
├── chart           # Chart generation service
├── seed            # Demo data seeding
└── MetersApplication.java
```

## Database Schema

The application uses Liquibase for database migrations. The schema includes:

- `users` - User accounts
- `meters` - Water meters (hot/cold)
- `readings` - Meter readings with photos

## File Storage

Photos are stored in MinIO S3-compatible storage in the `meter-photos` bucket.

## Development

To run the application locally without Docker:

1. Start PostgreSQL and MinIO separately
2. Update `application.yml` with local connection details
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

## License

This project is licensed under the MIT License.
