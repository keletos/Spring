# Cyberpunk RED Character API

Spring Boot backend project for managing Cyberpunk RED characters and demonstrating resilient REST integration and event-driven communication.

## Stack

- Java 21 and Spring Boot 4.1.1
- Spring MVC, Spring Data JPA and PostgreSQL
- Flyway database migrations
- Spring for Apache Kafka
- Resilience4j retry and circuit breaker
- Caffeine bounded cache
- Springdoc OpenAPI / Swagger UI
- Maven Wrapper

## SIS 3 implementation

| Requirement | Implementation |
|---|---|
| OpenAPI for every endpoint and errors | Global OpenAPI document, `api-v1` group and `characters-v1` group |
| Resilient outbound REST client | GitHub RestClient with connect/read timeouts, three attempts, circuit breaker and fallback response |
| Logging aspect and bounded cache | Execution-time aspect logs success/failure; Caffeine cache limited to 1,000 entries with 10-minute expiry |
| Domain event and aggregate key | `CharacterCreatedEvent`; Kafka key is the character UUID |
| Consumer group and idempotency | `character-audit-consumer`; `processed_events` primary key de-duplicates deliveries |
| Dead-letter topic and poison message | `characters.created.DLT`; two retries for listener failures, malformed JSON routed directly to DLT |
| Local Kafka instructions | `course-project/compose.kafka.yml` below |

Character creation writes the character and an outbox row in the same PostgreSQL transaction. A scheduled relay publishes pending outbox rows to Kafka and marks them published after broker acknowledgement. This is an at-least-once design: a crash after Kafka accepts a record but before the outbox update can cause a duplicate. The consumer is therefore idempotent by `eventId`.

## Prerequisites

- JDK 21
- Docker Desktop with Docker Compose
- PostgreSQL running locally
- Database `Spring_CP_RED_DEV` (and `Spring_CP_RED_TEST` when running integration tests)
- PostgreSQL user/password matching your local configuration

The application reads the PostgreSQL password from `DB_PASSWORD`.

## Start Kafka locally

From the repository root:

```powershell
docker compose -f course-project/compose.kafka.yml up -d
```

Check the broker:

```powershell
docker compose -f course-project/compose.kafka.yml ps
```

Stop Kafka:

```powershell
docker compose -f course-project/compose.kafka.yml down
```

Kafka is available at `localhost:9092`. Override it with `KAFKA_BOOTSTRAP_SERVERS` if needed.

## Run the application

From `course-project`:

```powershell
$env:DB_PASSWORD = "your-postgres-password"
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

The default profile is `dev`. Flyway applies the schema migrations automatically.

Useful URLs:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Complete API OpenAPI JSON: http://localhost:8080/v3/api-docs
- Complete versioned API group: http://localhost:8080/v3/api-docs/api-v1
- Characters-only API group: http://localhost:8080/v3/api-docs/characters-v1

## Verify the Kafka flow

1. Start PostgreSQL and Kafka.
2. Start the application.
3. Create a character through Swagger UI or the REST API.
4. Check application logs for the outbox relay publishing a `CharacterCreatedEvent` and the consumer handling it.
5. Inspect the consumer audit table:

```sql
SELECT * FROM character_created_audit ORDER BY occurred_at DESC;
SELECT * FROM outbox_events ORDER BY created_at DESC;
SELECT * FROM processed_events ORDER BY handled_at DESC;
```

The producer uses the character UUID as the Kafka key. The consumer runs in the `character-audit-consumer` group with concurrency 3, matching the topic's three partitions.

## Poison-message / DLT demonstration

The consumer uses an error-handling deserializer and a `DefaultErrorHandler`. Listener-processing failures are retried twice with a one-second fixed backoff. Malformed JSON is a non-retryable deserialization failure and is routed directly to `characters.created.DLT`, because repeating the same invalid payload cannot make it valid.

Send malformed JSON to the source topic:

```powershell
docker exec -i course-project-kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic characters.created --property parse.key=true --property key.separator=:
```

At the producer prompt, enter a record with a key and invalid JSON value:

```text
poison-demo:{ this is not valid json
```

Read the dead-letter topic:

```powershell
docker exec -it course-project-kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic characters.created.DLT --from-beginning --property print.key=true --property print.headers=true
```

The failed record is parked in the DLT instead of blocking the source partition indefinitely. In production, alert on a non-empty DLT and replay only after the cause is fixed.

## Test notes

The `test` profile disables Kafka listeners and topic creation so database-backed tests do not require a running broker. Integration tests still require the configured PostgreSQL test database and `DB_PASSWORD`.
