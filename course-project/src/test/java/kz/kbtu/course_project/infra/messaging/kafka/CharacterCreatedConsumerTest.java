package kz.kbtu.course_project.infra.messaging.kafka;

import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CharacterCreatedConsumerTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final CharacterCreatedConsumer consumer =
            new CharacterCreatedConsumer(jdbcTemplate);

    @Test
    void onCharacterCreated_shouldRecordFirstDeliveryAndAuditIt() {
        UUID eventId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        CharacterCreatedEvent event = new CharacterCreatedEvent(
                eventId, characterId, "V", "Solo", Instant.now(), 1
        );

        when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenReturn(1);

        consumer.onCharacterCreated(event, characterId.toString());

        verify(jdbcTemplate, times(2)).update(anyString(), any(Object[].class));
    }

    @Test
    void onCharacterCreated_shouldSkipDuplicateEventId() {
        UUID characterId = UUID.randomUUID();
        CharacterCreatedEvent event = new CharacterCreatedEvent(
                UUID.randomUUID(), characterId, "V", "Solo", Instant.now(), 1
        );

        when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenReturn(0);

        consumer.onCharacterCreated(event, characterId.toString());

        verify(jdbcTemplate, times(1)).update(anyString(), any(Object[].class));
    }

    @Test
    void onCharacterCreated_shouldRejectWrongAggregateKey() {
        UUID characterId = UUID.randomUUID();
        CharacterCreatedEvent event = new CharacterCreatedEvent(
                UUID.randomUUID(), characterId, "V", "Solo", Instant.now(), 1
        );

        assertThrows(IllegalArgumentException.class,
                () -> consumer.onCharacterCreated(event, "wrong-key"));

        verifyNoInteractions(jdbcTemplate);
    }
}
