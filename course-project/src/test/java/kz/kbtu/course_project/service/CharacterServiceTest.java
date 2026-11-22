package kz.kbtu.course_project.service;

import kz.kbtu.course_project.app.exception.CharacterNotFoundException;
import kz.kbtu.course_project.app.model.PageResult;
import kz.kbtu.course_project.app.model.PaginationQuery;
import kz.kbtu.course_project.app.port.out.CharacterRepositoryPort;
import kz.kbtu.course_project.app.port.out.DomainEventPublisherPort;
import kz.kbtu.course_project.app.service.CharacterService;
import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.domain.event.CharacterCreatedEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @Mock
    private CharacterRepositoryPort characterRepository;

    @Mock
    private DomainEventPublisherPort eventPublisher;

    @InjectMocks
    private CharacterService characterService;

    @Test
    void createCharacter_shouldInitializeCharacterAndPublishDomainEvent() {
        UUID characterId = UUID.randomUUID();

        Character character = new Character(
                null, "V", "Solo", 2550,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        when(characterRepository.save(any(Character.class)))
                .thenAnswer(invocation -> {
                    Character saved = invocation.getArgument(0);

                    Character result = new Character(
                            characterId,
                            saved.getName(),
                            saved.getRole(),
                            saved.getEddies(),
                            saved.getIntelligence(),
                            saved.getReflex(),
                            saved.getDexterity(),
                            saved.getTechnology(),
                            saved.getCool(),
                            saved.getWill(),
                            saved.getMovement(),
                            saved.getBody(),
                            saved.getEmpathy(),
                            saved.getLuck()
                    );

                    result.initializeStats();
                    return result;
                });

        Character result = characterService.createCharacter(character);

        assertEquals(characterId, result.getId());
        assertEquals("V", result.getName());
        assertEquals("Solo", result.getRole());
        assertEquals(2550, result.getEddies());
        assertEquals(40, result.getMaxHitPoints());
        assertEquals(40, result.getCurrentHitPoints());
        assertEquals(40, result.getMaxHumanity());
        assertEquals(40, result.getCurrentHumanity());
        assertEquals(5, result.getCurrentLuckPoints());

        ArgumentCaptor<Character> characterCaptor =
                ArgumentCaptor.forClass(Character.class);

        verify(characterRepository).save(characterCaptor.capture());

        Character savedCharacter = characterCaptor.getValue();

        assertEquals("V", savedCharacter.getName());
        assertEquals(2550, savedCharacter.getEddies());
        assertEquals(40, savedCharacter.getMaxHitPoints());

        ArgumentCaptor<CharacterCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(CharacterCreatedEvent.class);

        verify(eventPublisher).publish(eventCaptor.capture());

        CharacterCreatedEvent event = eventCaptor.getValue();

        assertEquals(characterId, event.characterId());
        assertEquals("V", event.name());
        assertEquals("Solo", event.role());
        assertEquals(1, event.schemaVersion());
        assertNotNull(event.eventId());
        assertNotNull(event.occurredAt());
    }

    @Test
    void getCharacter_shouldThrowExceptionWhenCharacterDoesNotExist() {
        UUID characterId = UUID.randomUUID();

        when(characterRepository.findById(characterId))
                .thenReturn(Optional.empty());

        assertThrows(
                CharacterNotFoundException.class,
                () -> characterService.getCharacter(characterId)
        );

        verify(characterRepository).findById(characterId);
    }

    @Test
    void getAllCharacters_shouldReturnPagedCharacters() {
        Character character = new Character(
                UUID.randomUUID(), "V", "Solo", 500,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        character.initializeStats();

        PaginationQuery query = new PaginationQuery(0, 20, List.of());

        PageResult<Character> pageResult =
                new PageResult<>(List.of(character), 0, 20, 1, 1);

        when(characterRepository.findAll(query))
                .thenReturn(pageResult);

        PageResult<Character> result =
                characterService.getAllCharacters(query);

        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        assertEquals("V", result.content().get(0).getName());
        assertEquals("Solo", result.content().get(0).getRole());

        verify(characterRepository).findAll(query);
    }

    @Test
    void updateCharacter_shouldRecalculateStatsAndLimitCurrentValues() {
        UUID characterId = UUID.randomUUID();

        Character existing = new Character(
                characterId, "V", "Solo", 500,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        existing.setMaxHitPoints(40);
        existing.setCurrentHitPoints(35);
        existing.setMaxHumanity(40);
        existing.setCurrentHumanity(35);
        existing.setCurrentLuckPoints(5);

        Character updatedCharacter = new Character(
                null, "V2", "Netrunner", 1000,
                6, 8, 7, 5, 6,
                3, 7, 2, 2, 3
        );

        when(characterRepository.findById(characterId))
                .thenReturn(Optional.of(existing));

        when(characterRepository.update(eq(characterId), any(Character.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));

        Character result =
                characterService.updateCharacter(characterId, updatedCharacter);

        assertEquals("V2", result.getName());
        assertEquals("Netrunner", result.getRole());
        assertEquals(1500, result.getEddies());
        assertEquals(25, result.getMaxHitPoints());
        assertEquals(25, result.getCurrentHitPoints());
        assertEquals(20, result.getMaxHumanity());
        assertEquals(20, result.getCurrentHumanity());
        assertEquals(3, result.getCurrentLuckPoints());

        verify(characterRepository).findById(characterId);
        verify(characterRepository).update(eq(characterId), any(Character.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deleteCharacter_shouldDeleteExistingCharacter() {
        UUID characterId = UUID.randomUUID();

        Character existing = new Character(
                characterId, "V", "Solo", 500,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        when(characterRepository.findById(characterId))
                .thenReturn(Optional.of(existing));

        characterService.deleteCharacter(characterId);

        verify(characterRepository).findById(characterId);
        verify(characterRepository).deleteById(characterId);
        verifyNoInteractions(eventPublisher);
    }
}
