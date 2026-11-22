package kz.kbtu.course_project.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable fact emitted after a character has been persisted.
 *
 * @param eventId unique identifier used for consumer de-duplication
 * @param characterId aggregate identifier; also used as the Kafka record key
 * @param name character name at creation time
 * @param role character role at creation time
 * @param occurredAt event creation time
 * @param schemaVersion event contract version
 */
public record CharacterCreatedEvent(
        UUID eventId,
        UUID characterId,
        String name,
        String role,
        Instant occurredAt,
        int schemaVersion
) {
}
