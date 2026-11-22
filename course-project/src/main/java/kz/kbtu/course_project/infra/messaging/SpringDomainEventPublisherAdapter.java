package kz.kbtu.course_project.infra.messaging;

import kz.kbtu.course_project.app.port.out.DomainEventPublisherPort;
import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringDomainEventPublisherAdapter implements DomainEventPublisherPort {

    private final ApplicationEventPublisher eventPublisher;

    public SpringDomainEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(CharacterCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
