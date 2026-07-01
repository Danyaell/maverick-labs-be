# Maverick Labs - Backend


## API REST

### Description
REST API to manage games from the Mega Man X series developed with Spring Boot 4.1.0, Java 21 and MySQL.
## Endpoints

### Game Module

#### GET `/api/v1/games`
Gets the list of all games ordered by `releaseOrder` (ascending).

**Response 200 OK:**
```json
[
  {
    "code": "MMX",
    "title": "Mega Man X",
    "releaseOrder": 1
  },
  {
    "code": "MMX2",
    "title": "Mega Man X2",
    "releaseOrder": 2
  },
  {
    "code": "MMX3",
    "title": "Mega Man X3",
    "releaseOrder": 3
  }
]
```

**Response 500 Error:**
```json
{
  "status": 500,
  "message": "Unexpected server error"
}
```

## Project Structure

```
game/
├── controller/
│   └── GameController.java      # REST Controller
├── service/
│   └── GameService.java         # Bussiness logic
├── repository/
│   └── GameRepository.java      # DAO JPA
├── entity/
│   └── Game.java                # JPA Entity
└── dto/
    └── GameSummaryResponse.java # Response DTO

common/
├── exception/
│   └── GlobalExceptionHandler.java # Global exception handler
└── dto/
    └── ErrorResponse.java       # Response DTO for error responses
```

## Data Base Configuration

### Requirements
- MySQL 8.0+
- Database: maverick_labs

### Create the database
Run the `init-db.sql` script:
```sql
mysql -u root -p < init-db.sql
```

### Configure the application.yaml
The configuration is ready in `src/main/resources/application.yaml`:
- URL: jdbc:mysql://localhost:3306/maverick_labs
- DDL: update (create/update tables automatically)
- Dialect: MySQLDialect

## Compilation and Execution

### Compile the application
```bash
mvn clean compile
```

### Package the application
```bash
mvn clean package
```

### Run the application
```bash
mvn spring-boot:run
```

Or run the packaged jar:
```bash
java -jar target/maverick-labs-be-0.0.1-SNAPSHOT.jar
```

## Used Technologies

- **Spring Boot**: 4.1.0
- **Java**: 21
- **Spring Data JPA**: Access to data
- **MySQL Connector**: Driver for MySQL
- **Lombok**: Code reduction
- **Maven**: Dependency management

## Game Entity Fields

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier (auto-generated) |
| code | String | Unique code of the game |
| title | String | Title of the game |
| releaseOrder | Integer | Release order |

## Notes

- The `Game` entity is mapped to the `games` table in MySQL
- The `code` field is unique and required
- Games are always returned ordered by `releaseOrder` in ascending order
- Internal errors return status 500 with a generic message for security
- Lombok is used to reduce boilerplate code (@Data, @NoArgsConstructor, @AllArgsConstructor)
- Dependency injection is implemented with @RequiredArgsConstructor from Lombok

