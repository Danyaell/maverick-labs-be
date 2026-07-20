# Maverick Labs - Backend


## API REST

### Description
REST API for modeling Mega Man X game data and analyzing player-defined routes based on boss weaknesses, collectible requirements, estimated difficulty, completion time, backtracking and actionable recommendations.
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

#### GET `/api/v1/games/{gameCode}`
Gets detailed information about a specific game, including stages, bosses, weapons, and collectibles.

**Parameters:**
- `gameCode` (path): Game code in uppercase or lowercase (case-insensitive search). Example: `MMX`, `mmx`

**Response 200 OK:**
```json
{
  "code": "MMX",
  "title": "Mega Man X",
  "releaseOrder": 1,
  "stages": [
    {
      "slug": "chill-penguin",
      "name": "Chill Penguin Stage",
      "stageOrder": 1,
      "imageAssetKey": "mmx.stage.chill-penguin",
      "boss": {
        "slug": "chill-penguin",
        "name": "Chill Penguin",
        "imageAssetKey": "mmx.boss.chill-penguin"
      },
      "weaponReward": {
        "slug": "shotgun-ice",
        "name": "Shotgun Ice",
        "description": "Fires ice projectiles.",
        "imageAssetKey": "mmx.weapon.shotgun-ice"
      },
      "collectibles": [
        {
          "slug": "chill-penguin-heart-tank",
          "name": "Heart Tank",
          "type": "HEART_TANK",
          "description": "Increases maximum health.",
          "imageAssetKey": "mmx.collectible.heart-tank",
          "sortOrder": 1
        },
        {
          "slug": "leg-upgrade-capsule",
          "name": "Leg Upgrade",
          "type": "ARMOR_UPGRADE",
          "description": "Unlocks dash movement.",
          "imageAssetKey": "mmx.collectible.leg-upgrade",
          "sortOrder": 2
        }
      ]
    }
  ]
}
```

### Route Analysis Module

#### POST `/api/v1/routes/analyze`
Analyzes a proposed stage order and returns route scoring, warnings, and recommendations.

**Request body:**
```json
{
  "gameCode": "MMX",
  "stageOrder": ["chill-penguin", "spark-mandrill", "storm-eagle", "flame-mammoth"],
  "goal": "HUNDRED_PERCENT"
}
```

**Response 200 OK (shape):**
```json
{
  "gameCode": "MMX",
  "difficultyScore": 58,
  "difficultyLabel": "MEDIUM",
  "backtrackingScore": 20,
  "estimatedMinutes": 64,
  "warnings": [
    {
      "type": "MISSING_REQUIREMENT",
      "message": "Collectible X may require revisiting Y later.",
      "stageSlug": "flame-mammoth",
      "collectibleSlug": "flame-mammoth-sub-tank"
    }
  ],
  "breakdown": {
    "baseDifficultyAverage": 64,
    "combatDifficulty": 58,
    "weaknessReduction": 6,
    "routeEfficiencyScore": 68,
    "timePenaltyMinutes": 9
  },
  "recommendations": [
    {
      "type": "BACKTRACKING",
      "severity": "WARNING",
      "message": "You may need to revisit Flame Mammoth to collect all items.",
      "relatedStages": ["flame-mammoth"]
    }
  ]
}
```

**Response 404 Not Found:**
```json
{
  "status": 404,
  "message": "Game not found: INVALID"
}
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
│   ├── GameController.java                   # REST Controller
│   └── RouteAnalysisController.java          # REST Controller for route analysis
├── service/
│   ├── GameService.java                      # Business logic
│   ├── RouteAnalysisService.java             # Business logic for route analysis
│   ├── RecommendationService.java            # Business logic for recommendations
│   └── RouteAnalysisContext.java             # Context for route analysis
├── repository/
│   ├── CollectibleRepository.java            # DAO JPA for Collectible
│   ├── GameRepository.java                   # DAO JPA for Game
│   ├── StageRepository.java                  # DAO JPA for Stage
│   └── WeaponRepository.java                 # DAO JPA for Weapon
├── entity/
│   ├── Game.java                             # JPA Entity - Game
│   ├── Stage.java                            # JPA Entity - Stage
│   ├── Boss.java                             # JPA Entity - Boss
│   ├── Weapon.java                           # JPA Entity - Weapon
│   ├── Collectible.java                      # JPA Entity - Collectible
│   ├── CollectibleRequirement.java           # JPA Entity - CollectibleRequirement
│   ├── CollectibleType.java                  # Enum for collectible types
│   └── RequirementType.java                  # Enum for requirement types
├── exception/
│   └── ResourceNotFoundException.java # Exception for not found resources
└── dto/
    ├── route/
    │   ├── AnalyzeRouteRequest.java          # Request DTO for route analysis
    │   ├── RouteAnalysisResponse.java        # Response DTO for route analysis
    │   ├── RouteBreakdownResponse.java       # Response DTO for route breakdown
    │   ├── RouteWarningResponse.java         # Response DTO for warnings
    │   └── RouteRecommendationResponse.java  # Response DTO for recommendations
    ├── GameSummaryResponse.java              # Response DTO for game list
    ├── GameDetailResponse.java               # Response DTO for game detail
    ├── StageResponse.java                    # Response DTO for stage
    ├── BossResponse.java                     # Response DTO for boss
    ├── WeaponResponse.java                   # Response DTO for weapon
    └── CollectibleResponse.java              # Response DTO for collectible

common/
├── exception/
│   └── GlobalExceptionHandler.java  # Global exception handler
└── dto/
    └── ErrorResponse.java           # Response DTO for error responses
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
The configuration should be created in `src/main/resources/application.yaml`. Here's an example configuration:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/maverick_labs?useSSL=false&serverTimezone=UTC
    username: root
    password: 1234
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

## Compilation and Execution

### Compile the application
```bash
mvn clean compile
```

### Run tests
```bash
mvn test
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

## Testing

The project includes comprehensive unit and integration tests:

### Test Types
- **Unit Tests**: Service layer tests using JUnit 5 and Mockito
- **Controller Tests**: MockMvc tests for HTTP endpoint validation
- **Repository Tests**: Data layer tests with H2 in-memory database
- **Integration Tests**: Full Spring context tests for critical workflows

### Running Tests
```bash
./mvnw test
```

### Test Coverage
- GameService: 15+ tests covering getAllGames() and getGameDetailByCode()
- GameController: 16+ tests covering both endpoints with various scenarios
- GameRepository: 19+ tests covering CRUD operations and custom queries

## Used Technologies

- **Spring Boot**: 4.1.0
- **Java**: 21
- **Spring Data JPA**: Access to data
- **MySQL Connector**: Driver for MySQL
- **Lombok**: Code reduction
- **Maven**: Dependency management

## Database Schema

### Game Entity
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier (auto-generated) |
| code | String | Unique code of the game |
| title | String | Title of the game |
| releaseOrder | Integer | Release order |

### Stage Entity
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier (auto-generated) |
| game_id | Long | Foreign key to Game |
| slug | String | URL-friendly identifier |
| name | String | Stage name |
| stageOrder | Integer | Order within the game |
| baseDifficulty | Integer | Base difficulty score for the stage |
| estimatedMinutes | Integer | Estimated completion time in minutes |
| imageAssetKey | String | Asset key for stage image |

### Boss Entity
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier (auto-generated) |
| stage | Long | Foreign key to Stage (one-to-one) |
| slug | String | URL-friendly identifier |
| name | String | Boss name |
| imageAssetKey | String | Asset key for boss image |
| weaknessWeapon | String | Slug of the weapon that is effective against this boss |

### Weapon Entity
| Field | Type | Description |
|------|------|-------------|
| id | Long | Unique identifier (auto-generated) |
| game | Long | Foreign key to Game |
| obtainedFromStage | Long | Foreign key to Stage (where weapon is obtained) |
| slug | String | URL-friendly identifier |
| name | String | Weapon name |
| description | String | Weapon description |
| imageAssetKey | String | Asset key for weapon image |

### Collectible Entity
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier (auto-generated) |
| stage | Long | Foreign key to Stage |
| slug | String | URL-friendly identifier |
| name | String | Collectible name |
| type | String | Type of collectible (HEART_TANK, SUB_TANK, ARMOR_UPGRADE, WEAPON_UPGRADE, RIDE_ARMOR, PART, LIFE_UP, OTHER) |
| description | String | Collectible description |
| imageAssetKey | String | Asset key for collectible image |
| sortOrder | Integer | Display order within stage |
| requirements | List<CollectibleRequirement> | List of requirements for this collectible |

### CollectibleRequirement Entity
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier (auto-generated) |
| collectible | Long | Foreign key to Collectible |
| requirementType | String | Type of requirement (e.g., weapon, collectible) |
| requiredKey | String | Key of the required item (slug of weapon or collectible) |
| description | String | Description of the requirement |

## Entity Relationships

- **Game** has many **Stages** (1:N)
- **Stage** has one **Boss** (1:1)
- **Stage** has many **Collectibles** (1:N)
- **Weapon** belongs to a **Game** (N:1)
- **Weapon** may be obtained from a **Stage** (N:1, nullable)
- **Collectible** has many **CollectibleRequirements** (1:N)
- **CollectibleRequirement** belongs to a **Collectible** (N:1)

## Notes

- **Game Management**: The `Game` entity is mapped to the `games` table in MySQL and serves as the root entity for the game detail hierarchy.
- **Unique Code**: The `code` field is unique and required. Game code searches are case-insensitive (`findByCodeIgnoreCase`).
- **Sorted Results**: Games are always returned ordered by `releaseOrder` in ascending order. Stages within a game are ordered by `stageOrder`, and collectibles within a stage are ordered by `sortOrder`.
- **Error Handling**: Internal errors return status 500 with a generic message for security. Resource not found errors return status 404 with a descriptive message.
- **DTO Pattern**: All API responses use DTOs (Data Transfer Objects) and never expose JPA entities directly to prevent LazyInitializationException and to decouple the API contract from the persistence model.
- **Optimized Queries**: The `StageRepository` uses JOIN FETCH queries to load related entities (bosses and collectibles) in a single query, preventing N+1 query problems.
- **Lombok Usage**: Lombok is used to reduce boilerplate code (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @RequiredArgsConstructor).
- **Dependency Injection**: Constructor injection is used throughout the application for better testability and immutability.
- **Collectible Types**: Collectibles are categorized by type using an enum with the following values:
  - `HEART_TANK` - Increases maximum health
  - `SUB_TANK` - Provides extra health reserve
  - `ARMOR_UPGRADE` - Increases defense or unlocks movement
  - `WEAPON_UPGRADE` - Enhances weapon capabilities
  - `RIDE_ARMOR` - Armor component or ride armor
  - `PART` - General part component
  - `LIFE_UP` - Increases health counter
  - `OTHER` - Miscellaneous collectible type
