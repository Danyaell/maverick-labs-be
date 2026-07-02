# AGENTS.md

## Project overview

This is the backend for Maverick Labs, a portfolio/educational project built with Java 21, Spring Boot and MySQL.

The backend exposes a REST API consumed by a React + Vite + TypeScript frontend.

## Tech stack

- Java 21
- Spring Boot
- Maven Wrapper
- MySQL
- JPA/Hibernate
- Spring Boot Test

## Architecture rules

Follow a layered architecture:

- Controller: HTTP endpoints only. No business logic.
- Service: business logic, validation orchestration and use-case flow.
- Repository: database access only.
- Entity: persistence model.
- DTO: request and response models exposed through the API.
- Mapper: conversion between entities and DTOs when needed.

Do not return JPA entities directly from controllers. Use DTOs.

## Package conventions

Use clear feature-oriented or layer-oriented packages consistently.

Current preferred structure:

```text
src/main/java/com/danyaell/mavericklabsbe/
├── common
    ├── dto
    └── exception
├── config
└── feature
    ├── controller
    ├── dto
    ├── entity
    ├── repository
    ├── service
    ├── mapper
    └── exception
```

## API conventions
- Base path: /api/v1
- Use plural resource names, for example /api/v1/games.
- Use appropriate HTTP status codes.
- Validate request bodies with Bean Validation annotations.
- Keep API responses stable and frontend-friendly.

## Error handling

Use centralized exception handling with `@RestControllerAdvice`.

Avoid leaking internal exception details to the frontend.

Prefer consistent error response DTOs for validation errors, not found errors and business rule errors.

## Testing rules

Write tests for meaningful behavior.

Preferred test types:

- Unit tests for services using JUnit 5 and Mockito.
- Controller tests using MockMvc.
- Repository tests using @DataJpaTest.
- Integration tests only when they add real value.

Before finishing a backend change, run:

```./mvnw test```

On Windows PowerShell:

````.\mvnw test````

## Database rules
- Do not assume production data exists.
- Do not hardcode local credentials.
- Use environment variables or profile-specific properties for database configuration.
- For tests, prefer an isolated test profile.
## Code style
- Prefer constructor injection.
- Avoid field injection.
- Keep methods small and readable.
- Prefer explicit names over clever abbreviations.
- Avoid premature abstractions.
- Do not introduce new dependencies unless there is a clear reason.
## What not to do
- Do not rewrite large parts of the architecture without explaining why.
- Do not bypass service layer from controllers.
- Do not expose entities directly in API responses.
- Do not add authentication/security complexity unless the task asks for it.
- Do not add generated boilerplate that is not used.