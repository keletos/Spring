package kz.kbtu.course_project.infra.messaging.kafka;

import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Component
@ConditionalOnProperty(
        name = "app.messaging.kafka.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CharacterCreatedConsumer {

    private static final Logger logger =
            LoggerFactory.getLogger(CharacterCreatedConsumer.class);

    private final JdbcTemplate jdbcTemplate;

    public CharacterCreatedConsumer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @KafkaListener(
            topics = "characters.created",
            groupId = "character-audit-consumer",
            concurrency = "3"
    )
    @Transactional
    public void onCharacterCreated(
            CharacterCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        validate(event, key);

        int inserted = jdbcTemplate.update("""
                INSERT INTO processed_events (event_id, handled_at)
                VALUES (?, CURRENT_TIMESTAMP)
                ON CONFLICT (event_id) DO NOTHING
                """, event.eventId());

        if (inserted == 0) {
            logger.info("Skipping duplicate character event {}", event.eventId());
            return;
        }

        jdbcTemplate.update("""
                INSERT INTO character_created_audit
                    (event_id, character_id, character_name, character_role, occurred_at)
                VALUES (?, ?, ?, ?, ?)
                """,
                event.eventId(),
                event.characterId(),
                event.name(),
                event.role(),
                Timestamp.from(event.occurredAt())
        );

        logger.info("Consumed character-created event {} for character {}",
                event.eventId(), event.characterId());
    }

    private void validate(CharacterCreatedEvent event, String key) {
        if (event == null
                || event.eventId() == null
                || event.characterId() == null
                || event.occurredAt() == null
                || event.name() == null
                || event.role() == null
                || event.schemaVersion() != 1) {
            throw new IllegalArgumentException("Invalid CharacterCreatedEvent payload");
        }

        if (!event.characterId().toString().equals(key)) {
            throw new IllegalArgumentException(
                    "Kafka key must equal the character aggregate ID"
            );
        }
    }
}
