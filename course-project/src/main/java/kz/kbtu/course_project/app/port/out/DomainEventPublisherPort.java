package kz.kbtu.course_project.app.port.out;

import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;

public interface DomainEventPublisherPort {

    void publish(CharacterCreatedEvent event);
}
