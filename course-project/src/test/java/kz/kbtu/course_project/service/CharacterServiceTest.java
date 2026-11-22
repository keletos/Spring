
package kz.kbtu.course_project.service;

import kz.kbtu.course_project.app.service.CharacterService;
import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.exception.CharacterNotFoundException;
import kz.kbtu.course_project.infra.persistence.entity.CharacterEntity;
import kz.kbtu.course_project.infra.persistence.mapper.CharacterPersistenceMapper;
import kz.kbtu.course_project.infra.persistence.repository.CharacterRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Spy
    private CharacterPersistenceMapper persistenceMapper =
            new CharacterPersistenceMapper();

    @InjectMocks
    private CharacterService characterService;

    /*
        TEST 1 - Creating a character
    */
    @Test
    void createCharacter_shouldInitializeCharacterCorrectly() {

        // Arrange
        Character character = new Character(
                null, "V", "Solo", 2550,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        when(characterRepository.save(any(CharacterEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Character result = characterService.createCharacter(character);

        // Assert
        assertEquals("V", result.getName());
        assertEquals("Solo", result.getRole());
        assertEquals(2550, result.getEddies());

        assertEquals(40, result.getMaxHitPoints());
        assertEquals(40, result.getCurrentHitPoints());

        assertEquals(40, result.getMaxHumanity());
        assertEquals(40, result.getCurrentHumanity());

        assertEquals(5, result.getCurrentLuckPoints());

        ArgumentCaptor<CharacterEntity> captor =
                ArgumentCaptor.forClass(CharacterEntity.class);

        verify(characterRepository).save(captor.capture());

        CharacterEntity savedEntity = captor.getValue();

        assertEquals("V", savedEntity.getName());
        assertEquals(2550, savedEntity.getEddies());
        assertEquals(40, savedEntity.getMaxHitPoints());
    }

    /*
        TEST 2.1 - Getting a non-existing character
    */
    @Test
    void getCharacter_shouldThrowExceptionWhenCharacterDoesNotExist() {

        // Arrange
        UUID characterId = UUID.randomUUID();

        when(characterRepository.findById(characterId))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                CharacterNotFoundException.class,
                () -> characterService.getCharacter(characterId)
        );

        verify(characterRepository).findById(characterId);
    }

    /*
        TEST 2.2 - Getting all characters
    */
    @Test
    void getAllCharacters_shouldReturnPagedCharacters() {

        // Arrange
        CharacterEntity entity = new CharacterEntity(
                "V", "Solo", 500,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        entity.setMaxHitPoints(40);
        entity.setCurrentHitPoints(40);
        entity.setMaxHumanity(40);
        entity.setCurrentHumanity(40);
        entity.setCurrentLuckPoints(5);

        Pageable pageable = PageRequest.of(0, 20);

        Page<CharacterEntity> entityPage =
                new PageImpl<>(List.of(entity), pageable, 1);

        when(characterRepository.findAll(pageable))
                .thenReturn(entityPage);

        // Act
        Page<Character> result =
                characterService.getAllCharacters(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        assertEquals("V", result.getContent().get(0).getName());
        assertEquals("Solo", result.getContent().get(0).getRole());

        verify(characterRepository).findAll(pageable);
    }

    /*
        TEST 3 - Updating a character
    */
    @Test
    void updateCharacter_shouldRecalculateStatsAndLimitCurrentValues() {

        // Arrange
        UUID characterId = UUID.randomUUID();

        CharacterEntity existing = new CharacterEntity(
                "V", "Solo", 500,
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

        // Act
        Character result =
                characterService.updateCharacter(characterId, updatedCharacter);

        // Assert
        assertEquals("V2", result.getName());
        assertEquals("Netrunner", result.getRole());
        assertEquals(1500, result.getEddies());

        assertEquals(25, result.getMaxHitPoints());
        assertEquals(25, result.getCurrentHitPoints());

        assertEquals(20, result.getMaxHumanity());
        assertEquals(20, result.getCurrentHumanity());

        assertEquals(3, result.getCurrentLuckPoints());

        verify(characterRepository).findById(characterId);
    }

    /*
        TEST 4 - Deleting a character
    */
    @Test
    void deleteCharacter_shouldDeleteExistingCharacter() {

        // Arrange
        UUID characterId = UUID.randomUUID();

        CharacterEntity existing = new CharacterEntity(
                "V", "Solo", 500,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        when(characterRepository.findById(characterId))
                .thenReturn(Optional.of(existing));

        // Act
        characterService.deleteCharacter(characterId);

        // Assert
        verify(characterRepository).findById(characterId);
        verify(characterRepository).delete(existing);
    }
}
