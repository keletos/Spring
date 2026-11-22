
package kz.kbtu.course_project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import kz.kbtu.course_project.infra.persistence.entity.CharacterEntity;
import kz.kbtu.course_project.infra.persistence.repository.CharacterRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
    replace = AutoConfigureTestDatabase.Replace.NONE
)
class CharacterRepositoryTest {

    @Autowired
    private CharacterRepository characterRepository;

    /*
        TEST 1 - Saving and Finding a Character
     */
    @Test
    void saveAndFindCharacter_shouldWorkWithPostgreSQL() {
        // Arrange: create a character
        CharacterEntity character = new CharacterEntity(
            "Integration Test",
            "Netrunner",
            2550,
            6, 6, 6, 6, 6, 6, 6, 6, 6, 6
        );

        character.setLevel(20);

        // Act: save the character to PostgreSQL
        CharacterEntity saved = characterRepository.saveAndFlush(character);

        // Assert: the database generated an ID
        assertThat(saved.getId()).isNotNull();

        // Act: test case-insensitive role search
        Page<CharacterEntity> foundByRole =
            characterRepository.findByRoleIgnoreCase(
                "netrunner",
                PageRequest.of(0, 10)
            );

        // Assert
        assertThat(foundByRole.getContent())
            .hasSize(1)
            .extracting(CharacterEntity::getName)
            .containsExactly("Integration Test");

        // Act: test the custom JPQL query
        Page<CharacterEntity> foundByLevel =
            characterRepository.findCharactersByLevel(
                20,
                PageRequest.of(0, 10)
            );

        // Assert
        assertThat(foundByLevel.getContent())
            .extracting(CharacterEntity::getName)
            .contains("Integration Test");
    }
}
