package kz.kbtu.course_project.infra.messaging.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "aggregate_id", nullable = false, updatable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, updatable = false, length = 120)
    private String eventType;

    @Column(name = "character_name", nullable = false, updatable = false, length = 100)
    private String characterName;

    @Column(name = "character_role", nullable = false, updatable = false, length = 50)
    private String characterRole;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(name = "schema_version", nullable = false, updatable = false)
    private int schemaVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected OutboxEventEntity() {
    }

    public OutboxEventEntity(CharacterCreatedEvent event) {
        this.eventId = event.eventId();
        this.aggregateId = event.characterId();
        this.eventType = CharacterCreatedEvent.class.getSimpleName();
        this.characterName = event.name();
        this.characterRole = event.role();
        this.occurredAt = event.occurredAt();
        this.schemaVersion = event.schemaVersion();
        this.createdAt = Instant.now();
    }

    public CharacterCreatedEvent toEvent() {
        return new CharacterCreatedEvent(
                eventId,
                aggregateId,
                characterName,
                characterRole,
                occurredAt,
                schemaVersion
        );
    }

    public void markPublished() {
        this.publishedAt = Instant.now();
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
