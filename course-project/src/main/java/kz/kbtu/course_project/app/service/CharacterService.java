package kz.kbtu.course_project.app.service;

import kz.kbtu.course_project.app.exception.CharacterNotFoundException;
import kz.kbtu.course_project.app.model.PageResult;
import kz.kbtu.course_project.app.model.PaginationQuery;
import kz.kbtu.course_project.app.port.out.CharacterRepositoryPort;
import kz.kbtu.course_project.app.port.out.DomainEventPublisherPort;
import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

public class CharacterService {

    private final CharacterRepositoryPort characterRepository;
    private final DomainEventPublisherPort eventPublisher;

    public CharacterService(
            CharacterRepositoryPort characterRepository,
            DomainEventPublisherPort eventPublisher
    ) {
        this.characterRepository = characterRepository;
        this.eventPublisher = eventPublisher;
    }

    @CacheEvict(value = {"characters", "charactersPage"}, allEntries = true)
    @Transactional
    public Character createCharacter(Character character) {
        character.initializeStats();

        Character createdCharacter = characterRepository.save(character);

        eventPublisher.publish(new CharacterCreatedEvent(
                UUID.randomUUID(),
                createdCharacter.getId(),
                createdCharacter.getName(),
                createdCharacter.getRole(),
                Instant.now(),
                1
        ));

        return createdCharacter;
    }

    @CacheEvict(value = {"characters", "charactersPage"}, allEntries = true)
    @Transactional
    public Character updateCharacter(UUID id, Character updatedCharacter) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        character.setName(updatedCharacter.getName());
        character.setRole(updatedCharacter.getRole());
        character.updateEddies(updatedCharacter.getEddies());

        character.updateCharacteristics(
                updatedCharacter.getIntelligence(),
                updatedCharacter.getReflex(),
                updatedCharacter.getDexterity(),
                updatedCharacter.getTechnology(),
                updatedCharacter.getCool(),
                updatedCharacter.getWill(),
                updatedCharacter.getMovement(),
                updatedCharacter.getBody(),
                updatedCharacter.getEmpathy(),
                updatedCharacter.getLuck()
        );

        return characterRepository.update(id, character);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"characters", "charactersPage"}, allEntries = true)
    @Transactional
    public void deleteCharacter(UUID id) {
        characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        characterRepository.deleteById(id);
    }

    @Cacheable(value = "characters", key = "#id")
    @Transactional(readOnly = true)
    public Character getCharacter(UUID id) {
        return characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));
    }

    @Cacheable(value = "charactersPage", key = "#query")
    @Transactional(readOnly = true)
    public PageResult<Character> getAllCharacters(PaginationQuery query) {
        return characterRepository.findAll(query);
    }
}
