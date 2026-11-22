package kz.kbtu.course_project.config;

import kz.kbtu.course_project.app.port.out.CharacterRepositoryPort;
import kz.kbtu.course_project.app.port.out.DomainEventPublisherPort;
import kz.kbtu.course_project.app.service.CharacterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CharacterServiceConfig {

    @Bean
    public CharacterService characterService(
            CharacterRepositoryPort characterRepository,
            DomainEventPublisherPort eventPublisher
    ) {
        return new CharacterService(characterRepository, eventPublisher);
    }
}
