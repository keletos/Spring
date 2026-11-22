package kz.kbtu.course_project.service;

import kz.kbtu.course_project.app.service.CompendiumService;
import kz.kbtu.course_project.infra.external.github.GitHubCompendiumClient;
import kz.kbtu.course_project.infra.external.github.GitHubRepositoryResponse;
import kz.kbtu.course_project.web.dto.CompendiumInfoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompendiumServiceTest {

    private static final String REPOSITORY_URL =
            "https://github.com/DankTrain11949/CyberPunk-Red-Fan-Compendium";

    @Test
    void getCompendiumInfo_whenGitHubReturnsMetadata_shouldMapResponse() {
        GitHubCompendiumClient client = mock(GitHubCompendiumClient.class);
        Instant updatedAt = Instant.parse("2026-11-20T10:15:30Z");

        when(client.fetchCompendiumRepository()).thenReturn(
                new GitHubRepositoryResponse(
                        "DankTrain11949/CyberPunk-Red-Fan-Compendium",
                        "Cyberpunk RED fan compendium",
                        REPOSITORY_URL,
                        42,
                        updatedAt
                )
        );

        CompendiumService service = new CompendiumService(client, REPOSITORY_URL);

        CompendiumInfoResponse response = service.getCompendiumInfo();

        assertTrue(response.available());
        assertEquals("DankTrain11949/CyberPunk-Red-Fan-Compendium", response.repository());
        assertEquals(42, response.stars());
        assertEquals(updatedAt, response.lastUpdated());
        verify(client).fetchCompendiumRepository();
    }

    @Test
    void getCompendiumInfo_whenGitHubIsUnavailable_shouldReturnFallback() {
        GitHubCompendiumClient client = mock(GitHubCompendiumClient.class);

        when(client.fetchCompendiumRepository()).thenThrow(
                new ResourceAccessException("GitHub connection timed out")
        );

        CompendiumService service = new CompendiumService(client, REPOSITORY_URL);

        CompendiumInfoResponse response = service.getCompendiumInfo();

        assertFalse(response.available());
        assertEquals(REPOSITORY_URL, response.url());
        assertEquals(0, response.stars());
        assertNull(response.lastUpdated());
        assertTrue(response.message().contains("temporarily unavailable"));
        verify(client).fetchCompendiumRepository();
    }
}
