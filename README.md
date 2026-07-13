# Maverick Labs BE

Backend REST para Maverick Labs, construido con **Java 21**, **Spring Boot 4.1.0**, **Maven** y **MySQL**.

## Stack y dependencias principales

- Spring Web
- Spring Data JPA
- Bean Validation
- MySQL Connector/J
- Lombok
- Spring Boot Test + H2 (tests)

## Requisitos

- JDK 21
- MySQL 8+
- Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Configuracion de base de datos

La app usa `application.yaml` y espera la configuracion de datasource por propiedades `spring.datasource.*` (por variables de entorno o argumentos).

Ejemplo (PowerShell):

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/maverick_labs"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="tu_password"
```

Script de inicializacion disponible en `init-db.sql`:

```bash
mysql -u root -p < init-db.sql
```

## CORS

Configurado globalmente por propiedades bajo `app.cors` en `src/main/resources/application.yaml`:

- `allowed-origins` (default: `http://localhost:3000`)
- `allowed-methods`
- `allowed-headers`
- `allow-credentials`
- `max-age`

## Ejecutar el proyecto

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Empaquetar:

```bash
./mvnw clean package
```

## Ejecutar tests

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

## API REST (base path: `/api/v1`)

### 1. Games

#### `GET /games`
Retorna juegos ordenados por `releaseOrder` ascendente.

Ejemplo de respuesta:

```json
[
  {
    "code": "MMX",
    "title": "Mega Man X",
    "releaseOrder": 1
  }
]
```

#### `GET /games/{gameCode}`
Retorna detalle del juego por codigo (busqueda case-insensitive).

Ejemplo de respuesta:

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
        }
      ]
    }
  ]
}
```

### 2. Route Analysis

#### `POST /routes/analyze`
Analiza una ruta y devuelve puntajes, advertencias y recomendaciones.

Request:

```json
{
  "gameCode": "MMX",
  "stageOrder": ["chill-penguin", "storm-eagle", "flame-mammoth", "spark-mandrill"],
  "goal": "HUNDRED_PERCENT"
}
```

Reglas de validacion principales:

- `gameCode`: obligatorio, no vacio
- `stageOrder`: obligatorio, no vacio, sin duplicados
- `goal`: obligatorio (actualmente solo `HUNDRED_PERCENT`)
- Para `HUNDRED_PERCENT`, la ruta debe incluir **todas** las stages del juego exactamente una vez

Respuesta (shape):

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
    "bossDifficulty": 233,
    "weaknessOptimization": 40,
    "backtrackingPenalty": 20,
    "timePenalty": 9
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

Enums usados en la respuesta:

- `difficultyLabel`: `EASY`, `MEDIUM`, `HARD`
- `warnings[].type`: `MISSING_REQUIREMENT`
- `recommendations[].type`: `BOSS_ORDER`, `BACKTRACKING`, `ROUTE_EFFICIENCY`
- `recommendations[].severity`: `INFO`, `WARNING`, `SUCCESS`

## Manejo de errores

La API usa un `@RestControllerAdvice` global y devuelve errores con este formato:

```json
{
  "status": 400,
  "message": "descripcion del error"
}
```

Casos comunes:

- `400`: errores de validacion/request invalido
- `404`: recurso no encontrado
- `500`: error interno (`"Unexpected server error"`)

## Estructura actual del proyecto

```text
src/main/java/com/danyaell/mavericklabsbe
├── common
│   ├── dto
│   └── exception
├── config
└── game
    ├── controller
    ├── dto
    │   └── route
    ├── entity
    ├── exception
    ├── repository
    └── service
```
