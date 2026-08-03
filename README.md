# Gasto Claro

API REST de finanzas personales multi-usuario, construida con Spring Boot 4.1 y Java 21. Permite registrar transacciones, organizarlas por categorías y etiquetas, definir presupuestos mensuales y consultar resúmenes y tendencias de gasto.

## Tabla de contenidos

- [Características](#características)
- [Stack tecnológico](#stack-tecnológico)
- [Modelo de datos](#modelo-de-datos)
- [Cómo levantar el proyecto](#cómo-levantar-el-proyecto)
- [Documentación de la API](#documentación-de-la-api)
- [Autenticación](#autenticación)
- [Tests](#tests)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Roadmap](#roadmap)

## Características

- **Multi-usuario**: cada usuario ve y gestiona únicamente sus propios datos (categorías, transacciones, presupuestos y tags).
- **Autenticación con JWT**: registro y login con contraseñas hasheadas (BCrypt) y tokens firmados.
- **CRUD completo** de categorías, transacciones, presupuestos y tags.
- **Filtros y paginación** en el listado de transacciones (por rango de fechas y categoría).
- **Resumen mensual**: ingresos, gastos, balance y desglose por categoría.
- **Tendencia de los últimos N meses**, reutilizando la lógica del resumen mensual.
- **Estado de presupuestos**: cuánto se lleva gastado de cada presupuesto y si se excedió.
- **Transacciones recurrentes** (marcadas con `recurring`), pensadas para automatizar gastos fijos como sueldo o suscripciones.
- **Manejo de errores centralizado**, con respuestas de error consistentes en toda la API.
- **Documentación interactiva** con Swagger UI.

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1 |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | PostgreSQL 17 |
| Seguridad | Spring Security + JWT (jjwt) |
| Documentación | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, Testcontainers |
| Contenedores | Docker + Docker Compose |
| Build | Maven |

## Modelo de datos

`User` es dueño de todo: cada `Category`, `Transaction`, `Budget` y `Tag` pertenece a un único usuario. Una `Transaction` pertenece a una sola `Category` (para efectos de presupuesto) y puede tener múltiples `Tag` (relación muchos-a-muchos, de uso libre).

```
User 1---N Category
User 1---N Transaction
User 1---N Budget
User 1---N Tag

Category 1---N Transaction
Category 1---N Budget

Transaction N---N Tag
```

## Cómo levantar el proyecto

### Opción 1: con Docker (recomendada)

Requiere [Docker Desktop](https://www.docker.com/products/docker-desktop/) corriendo.

```bash
git clone https://github.com/Alexis-P252/gastoClaroBackend
cd gastoClaroBackend
docker compose up --build
```

La API queda disponible en `http://localhost:8080`.

### Opción 2: local, sin Docker

Requiere JDK 21 y una instancia de PostgreSQL corriendo localmente.

1. Creá una base de datos llamada `gastoclaro`.
2. Ajustá las credenciales en `src/main/resources/application.properties` si no coinciden con las tuyas.
3. Generá tu propio `jwt.secret` (por ejemplo con `openssl rand -base64 32`) y reemplazá el valor por defecto.
4. Corré el proyecto:

```bash
./mvnw spring-boot:run
```

## Documentación de la API

Con el proyecto corriendo, la documentación interactiva está disponible en:

```
http://localhost:8080/swagger-ui.html
```

Desde ahí se puede probar cada endpoint directamente, incluyendo los protegidos (ver sección de autenticación).

## Autenticación

1. Registrar un usuario:
   ```
   POST /api/auth/register
   { "email": "vos@ejemplo.com", "password": "unaClaveDeAlMenos8Caracteres" }
   ```
2. La respuesta incluye un `token`. En Swagger UI, hacer clic en **Authorize** y pegar ese token (sin la palabra "Bearer", se agrega solo).
3. A partir de ahí, todos los endpoints protegidos (`/api/categories`, `/api/transactions`, `/api/budgets`, `/api/tags`, `/api/summary/**`) quedan accesibles.

Para iniciar sesión con un usuario existente: `POST /api/auth/login`.

## Tests

```bash
./mvnw test
```

Incluye tests unitarios (Mockito, sin dependencias externas) y de integración (Testcontainers, requiere Docker corriendo). Para correr solo los unitarios:

```bash
./mvnw test -Dtest=*ServiceTest
```

## Estructura del proyecto

```
src/main/java/com/api1/demo/
├── entity/        # Entidades JPA
├── repository/    # Interfaces Spring Data JPA
├── service/       # Lógica de negocio
├── controller/     # Endpoints REST
├── dto/           # Objetos de entrada (request) y salida (response)
├── mapper/        # Conversión Entity <-> DTO
├── exception/     # Excepciones propias y manejo global de errores
├── security/      # JWT, filtros de autenticación
└── config/        # Configuración de Security, Swagger, etc.
```

## Roadmap

- [ ] Frontend (React/Angular, a definir) para consumir la API
- [ ] Integración con IA (Spring AI / LangChain4j) para consultas en lenguaje natural sobre los propios datos financieros (RAG)
- [ ] Deploy en vivo (Render/Railway)

---

Proyecto desarrollado por [Alexis Peralta](https://github.com/Alexis-P252) como parte de un proceso de actualización profesional en desarrollo backend con Java/Spring.
