
package kz.kbtu.course_project.service;

import kz.kbtu.course_project.app.exception.CompendiumUnavailableException;
import kz.kbtu.course_project.app.model.CompendiumInfo;
import kz.kbtu.course_project.app.port.out.CompendiumRepositoryPort;
import kz.kbtu.course_project.app.service.CompendiumService;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompendiumServiceTest {

    private static final String REPOSITORY_URL =
            "https://github.com/DankTrain11949/CyberPunk-Red-Fan-Compendium";

    private CompendiumService service(CompendiumRepositoryPort repository) {
        return new CompendiumService(repository, REPOSITORY_URL);
    }

    @Test
    void getCompendiumInfo_whenRepositoryReturnsMetadata_shouldReturnIt() {
        CompendiumRepositoryPort repository =
                mock(CompendiumRepositoryPort.class);

        Instant updatedAt = Instant.parse("2026-11-20T10:15:30Z");

        CompendiumInfo expected = new CompendiumInfo(
                "DankTrain11949/CyberPunk-Red-Fan-Compendium",
                "Cyberpunk RED fan compendium",
                REPOSITORY_URL,
                42,
                updatedAt,
                true
        );

        when(repository.fetchCompendiumInfo()).thenReturn(expected);

        CompendiumInfo response = service(repository).getCompendiumInfo();

        assertEquals(expected, response);
        assertTrue(response.available());
        assertEquals(42, response.stars());

        verify(repository).fetchCompendiumInfo();
    }

    @Test
    void getCompendiumInfo_whenRepositoryIsUnavailable_shouldReturnFallback() {
        CompendiumRepositoryPort repository =
                mock(CompendiumRepositoryPort.class);

        when(repository.fetchCompendiumInfo()).thenThrow(
                new CompendiumUnavailableException("GitHub is unavailable.")
        );

        CompendiumInfo response = service(repository).getCompendiumInfo();

        assertFalse(response.available());
        assertEquals(
                "DankTrain11949/CyberPunk-Red-Fan-Compendium",
                response.repository()
        );
        assertEquals(REPOSITORY_URL, response.url());
        assertEquals(0, response.stars());
        assertNull(response.lastUpdated());
        assertTrue(response.description().contains("temporarily unavailable"));

        verify(repository).fetchCompendiumInfo();
    }
}