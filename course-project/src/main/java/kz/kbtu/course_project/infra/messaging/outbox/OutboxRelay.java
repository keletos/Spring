package kz.kbtu.course_project.infra.messaging.outbox;

import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@ConditionalOnProperty(
        name = "app.messaging.kafka.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class OutboxRelay {

    public static final String TOPIC = "characters.created";

    private static final Logger logger = LoggerFactory.getLogger(OutboxRelay.class);

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OutboxRelay(
            OutboxEventRepository outboxRepository,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${app.messaging.outbox.poll-interval:1000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventEntity> pending =
                outboxRepository.findPendingForUpdate(PageRequest.of(0, 50));

        for (OutboxEventEntity outboxEvent : pending) {
            CharacterCreatedEvent event = outboxEvent.toEvent();

            try {
                kafkaTemplate.send(
                        TOPIC,
                        outboxEvent.getAggregateId().toString(),
                        event
                ).get(10, TimeUnit.SECONDS);

                outboxEvent.markPublished();
                logger.info("Published event {} for character {}",
                        event.eventId(), event.characterId());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                logger.warn("Interrupted while publishing outbox event {}; it will be retried",
                        event.eventId(), exception);
                break;
            } catch (ExecutionException | TimeoutException exception) {
                logger.error("Could not publish outbox event {}; it will be retried",
                        event.eventId(), exception);
                break;
            }
        }
    }
}
