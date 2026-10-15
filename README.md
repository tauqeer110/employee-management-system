# Employee Management System

A small event-driven backend built with **Spring Boot**, **Kafka**, **MySQL** and **Caffeine cache**.

It has two services:

- **Producer service** exposes the REST API. It publishes employee changes to Kafka and serves reads from the database (with caching).
- **Consumer service** listens to Kafka and saves the changes to MySQL.

---

## Tech Stack

| Area | Technology |
|------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.14 |
| Messaging | Apache Kafka (Spring Kafka) |
| Database | MySQL + Spring Data JPA |
| Cache | Caffeine (Spring Cache) |
| API Docs | springdoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, Mockito, MockMvc, JaCoCo |
| Build | Maven |

---

## Architecture

```
            POST / PUT / DELETE                       GET
 Client ─────────────────────────┐          ┌────────────────────┐
                                 ▼          │                    │
                        ┌─────────────────┐ │                    │
                        │ Producer Service│◄┘ (Caffeine cache)   │
                        │   (port 8080)   │                      │
                        └────────┬────────┘                      │
                                 │ publish event                 │ read
                                 ▼                               │
                        ┌─────────────────┐                      │
                        │  Kafka          │                      │
                        │  employee-topic │                      │
                        └────────┬────────┘                      │
                                 │ consume event                 │
                                 ▼                               │
                        ┌─────────────────┐       ┌──────────────┴─┐
                        │ Consumer Service│──────►│     MySQL      │
                        │   (port 8081)   │ write │ employee_mgmt  │
                        └─────────────────┘       └────────────────┘
```

**How it works**

1. A write request (create, update, delete) hits the producer.
2. The producer sends an `EmployeeEvent` (`CREATE`, `UPDATE` or `DELETE`) to the `employee-topic` topic. The employee ID is the message key.
3. The consumer reads the event and applies it to MySQL.
4. Read requests (`GET`) are served from MySQL through the Caffeine cache.

> Write operations are **asynchronous**: the API responds as soon as the event is published, and the consumer saves it to the database.

---

## Project Structure

```
employee-management-system/
├── employee-producer-service/     # REST API + Kafka producer + cache
│   └── src/main/java/com/ems/employee/
│       ├── config/                # Kafka producer config
│       ├── controller/            # REST endpoints
│       ├── dto/  entity/  event/  enums/
│       ├── exception/             # Global exception handling
│       ├── repository/            # JPA repository
│       ├── service/               # Interfaces + Kafka producer service
│       ├── serviceImpl/           # Business logic + caching
│       └── util/                  # Mapper
└── employee-consumer-service/     # Kafka consumer + DB writes
    └── src/main/java/com/ems/consumer/
        ├── listener/              # @KafkaListener
        ├── service/ serviceImpl/  # Event processing
        └── dto/ entity/ event/ enums/ repository/ util/
```

---

## Prerequisites

- JDK 17
- Maven 3.9+ (or use the included `mvnw`)
- MySQL running on `localhost:3306`
- Kafka running on `localhost:9092`

---

## Configuration

Both services use `src/main/resources/application.yaml`.

| Property | Default |
|----------|---------|
| MySQL URL | `jdbc:mysql://localhost:3306/employee_management_system` (created if missing) |
| MySQL user | `root` |
| MySQL password | from env variable `DB_PASSWORD` |
| Kafka brokers | `localhost:9092` |
| Kafka topic | `employee-topic` |
| Consumer group | `employee-consumer-group-v2` |
| Producer port | `8080` |
| Consumer port | `8081` |

**Set the database password before starting:**

```bash
# Linux / macOS
export DB_PASSWORD=your_password

# Windows (PowerShell)
$env:DB_PASSWORD="your_password"
```

---

## Getting Started

### 1. Start Kafka and MySQL

Make sure both are running locally. The topic `employee-topic` is created automatically if your broker has auto-create enabled; otherwise create it:

```bash
kafka-topics.sh --create --topic employee-topic --bootstrap-server localhost:9092
```

### 2. Start the consumer service

```bash
cd employee-consumer-service
./mvnw spring-boot:run
```

### 3. Start the producer service

```bash
cd employee-producer-service
./mvnw spring-boot:run
```

### 4. Open Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

## API Endpoints

Base URL: `http://localhost:8080/api/employees`

| Method | Endpoint | Description | Success |
|--------|----------|-------------|---------|
| POST | `/api/employees` | Create an employee | `201 Created` |
| GET | `/api/employees` | Get all employees | `200 OK` |
| GET | `/api/employees/{id}` | Get one employee | `200 OK` |
| PUT | `/api/employees/{id}` | Update an employee | `200 OK` |
| DELETE | `/api/employees/{id}` | Delete an employee | `204 No Content` |

### Request body (POST / PUT)

```json
{
  "name": "Abbas",
  "age": 25,
  "address": "Bangalore"
}
```

### Validation rules

| Field | Rule |
|-------|------|
| `name` | must not be blank |
| `age` | required, minimum 18 |
| `address` | must not be blank |

### Example

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Abbas","age":25,"address":"Bangalore"}'
```

### Error response

```json
{
  "status": 404,
  "message": "Employee not found with id: 123",
  "timestamp": "2026-10-16T10:30:00"
}
```

| Status | When |
|--------|------|
| `400` | Validation failed |
| `404` | Employee not found |
| `500` | Database or unexpected error |

---

## Caching

Caffeine is used in the producer service (cache name: `employees`).

| Setting | Value |
|---------|-------|
| Max size | 10 entries |
| Expiry | 10 minutes after write |

| Operation | Cache behaviour |
|-----------|-----------------|
| `GET /employees` | Cached under the key `allEmployees` |
| `GET /employees/{id}` | Cached by employee ID |
| `POST` | Clears the `allEmployees` entry |
| `PUT` | Updates the entry for that ID, clears `allEmployees` |
| `DELETE` | Clears the whole cache |

---

## Kafka Events

**Topic:** `employee-topic` | **Key:** employee ID | **Value:** JSON

```json
{
  "operation": "CREATE",
  "employeeDTO": {
    "id": "b7f1c0de-...",
    "name": "Abbas",
    "age": 25,
    "address": "Bangalore"
  }
}
```

Operations: `CREATE`, `UPDATE`, `DELETE`.

Using the employee ID as the key keeps all events for the same employee in the same partition, so they are processed in order.

---

## Testing

Tests are in the producer service.

| Layer | Approach | Class |
|-------|----------|-------|
| Controller | `@WebMvcTest` + MockMvc | `EmployeeControllerTest` |
| Service | JUnit 5 + Mockito | `EmployeeServiceImplTest` |
| Application | Spring context load | `EmployeeServiceApplicationTests` |

**Run tests:**

```bash
cd employee-producer-service
./mvnw test
```

### Test Coverage

> Coverage was measured on the application **before Kafka integration was added** (REST, service, mapper and exception layers).

| Metric | Coverage |
|--------|----------|
| Classes | 77% (7/9) |
| Methods | 88% (23/26) |
| Lines | **90% (82/91)** |

| Package | Line Coverage |
|---------|---------------|
| `controller` | 100% (22/22) |
| `serviceImpl` | 100% (32/32) |
| `util` | 100% (11/11) |

**Coverage report (JaCoCo):** generated on `mvn test`, open:

```
employee-producer-service/target/site/jacoco/index.html
```

---

## Logs

Logs are written to the `logs/` folder of each service:

- `logs/employee-service.log` (producer)
- `logs/employee-consumer-service.log` (consumer)

---

## Author

**Abbas**
