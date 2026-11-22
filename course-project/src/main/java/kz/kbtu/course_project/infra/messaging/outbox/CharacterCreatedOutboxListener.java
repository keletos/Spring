package kz.kbtu.course_project.infra.messaging.outbox;

import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CharacterCreatedOutboxListener {

    private final OutboxEventRepository outboxRepository;

    public CharacterCreatedOutboxListener(OutboxEventRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    /**
     * BEFORE_COMMIT keeps the aggregate write and its outbox row in one
     * PostgreSQL transaction. A rolled-back character cannot leave an event behind.
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCharacterCreated(CharacterCreatedEvent event) {
        outboxRepository.save(new OutboxEventEntity(event));
    }
}
