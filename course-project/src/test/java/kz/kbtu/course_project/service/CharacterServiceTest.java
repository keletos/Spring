
package kz.kbtu.course_project.service;

import kz.kbtu.course_project.dto.CharacterResponse;
import kz.kbtu.course_project.dto.CreateCharacterRequest;
import kz.kbtu.course_project.entity.CharacterEntity;
import kz.kbtu.course_project.repository.CharacterRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import kz.kbtu.course_project.dto.UpdateCharacterRequest;
import kz.kbtu.course_project.exception.CharacterNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @InjectMocks
    private CharacterService characterService;

    /*
        TEST 1 - Creating a character
    */
    @Test
    void createCharacter_shouldInitializeCharacterCorrectly() {

        // Arrange: prepare request data
        CreateCharacterRequest request = new CreateCharacterRequest(
                "V", "Solo",
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        // Simulate repository save operation
        when(characterRepository.save(any(CharacterEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: execute the service method
        CharacterResponse response = characterService.createCharacter(request);

        // Assert: verify returned values
        assertEquals("V", response.name());
        assertEquals("Solo", response.role());

        assertEquals(2550, response.eddies());

        assertEquals(40, response.maxHealth());
        assertEquals(40, response.currentHealth());

        assertEquals(40, response.maxHumanity());
        assertEquals(40, response.currentHumanity());

        assertEquals(5, response.currentLuckPoints());

        // Verify that repository.save() was called
        ArgumentCaptor<CharacterEntity> captor =
                ArgumentCaptor.forClass(CharacterEntity.class);

        verify(characterRepository).save(captor.capture());

        CharacterEntity savedEntity = captor.getValue();

        assertEquals("V", savedEntity.getName());
        assertEquals(2550, savedEntity.getEddies());
        assertEquals(40, savedEntity.getMaxHitPoints());
    }

    /*
        TEST 2.1 - Getting a non existing character
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
    void getAllCharacters_shouldReturnPagedCharacterResponses() {

        // Arrange
        CharacterEntity character = new CharacterEntity(
                "V", "Solo", 500,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        character.setMaxHitPoints(40);
        character.setCurrentHitPoints(40);
        character.setMaxHumanity(40);
        character.setCurrentHumanity(40);
        character.setCurrentLuckPoints(5);

        Pageable pageable = PageRequest.of(0, 20);

        Page<CharacterEntity> characterPage =
                new PageImpl<>(List.of(character), pageable, 1);

        when(characterRepository.findAll(pageable))
                .thenReturn(characterPage);

        // Act
        Page<CharacterResponse> result =
                characterService.getAllCharacters(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        assertEquals("V", result.getContent().get(0).name());
        assertEquals("Solo", result.getContent().get(0).role());

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

        // Existing character's current values
        existing.setMaxHitPoints(40);
        existing.setCurrentHitPoints(35);

        existing.setMaxHumanity(40);
        existing.setCurrentHumanity(35);

        existing.setCurrentLuckPoints(5);

        UpdateCharacterRequest request = new UpdateCharacterRequest(
                "V2", "Netrunner", 1000,
                6, 8, 7, 5, 6,
                3, 7, 2, 2, 3
        );

        when(characterRepository.findById(characterId))
                .thenReturn(Optional.of(existing));

        // Act
        CharacterResponse response =
                characterService.updateCharacter(characterId, request);

        // Assert
        assertEquals("V2", response.name());
        assertEquals("Netrunner", response.role());
        assertEquals(1000, response.eddies());

        // New HP = 10 + 5 * ceil((2 + 3) / 2) = 25
        assertEquals(25, response.maxHealth());
        assertEquals(25, response.currentHealth());

        // New humanity = 2 * 10 = 20
        assertEquals(20, response.maxHumanity());
        assertEquals(20, response.currentHumanity());

        // New luck = 3
        assertEquals(3, response.currentLuckPoints());

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
