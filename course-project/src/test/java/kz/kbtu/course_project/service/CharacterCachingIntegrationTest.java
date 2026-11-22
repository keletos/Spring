package kz.kbtu.course_project.service;

import kz.kbtu.course_project.app.service.CharacterService;
import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.exception.CharacterNotFoundException;
import kz.kbtu.course_project.infra.persistence.entity.CharacterEntity;
import kz.kbtu.course_project.infra.persistence.repository.CharacterRepository;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManagerFactory;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CharacterCachingIntegrationTest {

    @Autowired
    private CharacterService characterService;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    private EntityManager entityManager;

    private Statistics statistics;

    private final List<UUID> createdCharacterIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        cacheManager.getCache("characters").clear();
        cacheManager.getCache("charactersPage").clear();

        SessionFactory sessionFactory =
                entityManagerFactory.unwrap(SessionFactory.class);

        statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
    }

    private CharacterEntity createCharacter() {
    CharacterEntity entity = characterRepository.saveAndFlush(
            new CharacterEntity(
                    "Cache Test",
                    "Solo",
                    500,
                    6, 8, 7, 5, 6,
                    5, 7, 6, 4, 5
            )
    );

    createdCharacterIds.add(entity.getId());

    entityManager.flush();
    entityManager.clear();

    return entity;
    }

    @Test
    void getCharacter_shouldUseCacheOnSecondRequest() {

        // Arrange
        CharacterEntity entity = createCharacter();
        UUID id = entity.getId();

        statistics.clear();

        // Act: first request
        Character first = characterService.getCharacter(id);

        long firstQueryCount =
                statistics.getPrepareStatementCount();

        // Act: second request
        Character second = characterService.getCharacter(id);

        long secondQueryCount =
                statistics.getPrepareStatementCount() - firstQueryCount;

        // Assert
        assertThat(first.getId()).isEqualTo(id);
        assertThat(second.getId()).isEqualTo(id);

        assertThat(firstQueryCount).isGreaterThan(0);
        assertThat(secondQueryCount).isZero();
    }

    @Test
    void updateCharacter_shouldEvictCachedCharacter() {

        // Arrange
        CharacterEntity entity = createCharacter();
        UUID id = entity.getId();

        characterService.getCharacter(id);

        Character updated = new Character(
                null, "Updated Cache Test", "Solo", 500,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        statistics.clear();

        // Act
        characterService.updateCharacter(id, updated);

        entityManager.flush();
        entityManager.clear();

        statistics.clear();

        Character result = characterService.getCharacter(id);

        long queryCount =
                statistics.getPrepareStatementCount();

        // Assert
        assertThat(result.getName()).isEqualTo("Updated Cache Test");
        assertThat(queryCount).isGreaterThan(0);
    }

    @Test
    void deleteCharacter_shouldEvictCachedCharacter() {

        // Arrange
        CharacterEntity entity = createCharacter();
        UUID id = entity.getId();

        characterService.getCharacter(id);

        // Act: delete character
        characterService.deleteCharacter(id);

        entityManager.flush();
        entityManager.clear();

        statistics.clear();

        // Assert: next lookup must query DB and fail
        assertThrows(
                CharacterNotFoundException.class,
                () -> characterService.getCharacter(id)
        );

        assertThat(statistics.getPrepareStatementCount())
                .isGreaterThan(0);
    }

    @Test
    void getAllCharacters_shouldUseCacheOnSecondRequest() {
        createCharacter();

        Pageable pageable = PageRequest.of(0, 10);

        // Exclude the INSERT query from our measurements.
        statistics.clear();

        Page<Character> first = characterService.getAllCharacters(pageable);
        long firstQueryCount = statistics.getPrepareStatementCount();

        Page<Character> second = characterService.getAllCharacters(pageable);
        long secondQueryCount =
                statistics.getPrepareStatementCount() - firstQueryCount;

        assertThat(first.getTotalElements()).isGreaterThan(0);
        assertThat(second.getTotalElements()).isEqualTo(first.getTotalElements());

        assertThat(firstQueryCount).isGreaterThan(0);
        assertThat(secondQueryCount).isZero();
    }

    @Test
    void createCharacter_shouldEvictCachedPages() {
        Pageable pageable = PageRequest.of(0, 100);

        // Populate the pagination cache.
        Page<Character> before =
                characterService.getAllCharacters(pageable);

        long previousTotal = before.getTotalElements();

        Character newCharacter = new Character(
                null, "Created Cache Test", "Solo", 500,
                6, 8, 7, 5, 6,
                5, 7, 6, 4, 5
        );

        // Creating a character should evict cached pages.
        characterService.createCharacter(newCharacter);

        statistics.clear();

        Page<Character> after =
                characterService.getAllCharacters(pageable);

        long queryCount = statistics.getPrepareStatementCount();

        assertThat(after.getTotalElements()).isEqualTo(previousTotal + 1);
        assertThat(after.getContent())
                .anyMatch(character ->
                        character.getName().equals("Created Cache Test"));
        assertThat(queryCount).isGreaterThan(0);
    }
}
