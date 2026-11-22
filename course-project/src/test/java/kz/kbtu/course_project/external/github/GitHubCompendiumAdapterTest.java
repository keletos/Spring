package kz.kbtu.course_project.external.github;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import kz.kbtu.course_project.app.exception.CompendiumUnavailableException;
import kz.kbtu.course_project.app.model.CompendiumInfo;
import kz.kbtu.course_project.infra.external.github.GitHubCompendiumAdapter;
import kz.kbtu.course_project.infra.external.github.GitHubCompendiumClient;
import kz.kbtu.course_project.infra.external.github.GitHubRepositoryResponse;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubCompendiumAdapterTest {

    private static final String REPOSITORY_URL =
            "https://github.com/DankTrain11949/CyberPunk-Red-Fan-Compendium";

    private Retry retry() {
        return Retry.of(
                "test-github",
                RetryConfig.custom()
                        .maxAttempts(3)
                        .waitDuration(Duration.ZERO)
                        .retryOnException(exception ->
                                exception instanceof ResourceAccessException
                                        || (exception instanceof org.springframework.web.client.RestClientResponseException response
                                        && response.getStatusCode().is5xxServerError())
                        )
                        .build()
        );
    }

    private CircuitBreaker circuitBreaker() {
        return CircuitBreaker.of(
                "test-github",
                CircuitBreakerConfig.custom()
                        .minimumNumberOfCalls(10)
                        .slidingWindowSize(10)
                        .build()
        );
    }

    private GitHubCompendiumAdapter adapter(GitHubCompendiumClient client) {
        return new GitHubCompendiumAdapter(
                client,
                retry(),
                circuitBreaker()
        );
    }

    @Test
    void fetchCompendiumInfo_whenGitHubReturnsMetadata_shouldMapResponse() {
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

        CompendiumInfo response = adapter(client).fetchCompendiumInfo();

        assertTrue(response.available());
        assertEquals(
                "DankTrain11949/CyberPunk-Red-Fan-Compendium",
                response.repository()
        );
        assertEquals(42, response.stars());
        assertEquals(updatedAt, response.lastUpdated());

        verify(client, times(1)).fetchCompendiumRepository();
    }

    @Test
    void fetchCompendiumInfo_whenGitHubIsUnavailable_shouldRetryThreeTimes() {
        GitHubCompendiumClient client = mock(GitHubCompendiumClient.class);

        when(client.fetchCompendiumRepository()).thenThrow(
                new ResourceAccessException("GitHub connection timed out")
        );

        assertThrows(
                CompendiumUnavailableException.class,
                () -> adapter(client).fetchCompendiumInfo()
        );

        verify(client, times(3)).fetchCompendiumRepository();
    }

    @Test
    void fetchCompendiumInfo_whenGitHubReturnsNotFound_shouldNotRetry() {
        GitHubCompendiumClient client = mock(GitHubCompendiumClient.class);

        when(client.fetchCompendiumRepository()).thenThrow(
                HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        HttpHeaders.EMPTY,
                        new byte[0],
                        StandardCharsets.UTF_8
                )
        );

        assertThrows(
                CompendiumUnavailableException.class,
                () -> adapter(client).fetchCompendiumInfo()
        );

        verify(client, times(1)).fetchCompendiumRepository();
    }

    @Test
    void fetchCompendiumInfo_whenCircuitIsOpen_shouldNotCallGitHub() {
        GitHubCompendiumClient client = mock(GitHubCompendiumClient.class);

        Retry retry = retry();
        CircuitBreaker breaker = circuitBreaker();
        breaker.transitionToOpenState();

        GitHubCompendiumAdapter adapter =
                new GitHubCompendiumAdapter(client, retry, breaker);

        assertThrows(
                CompendiumUnavailableException.class,
                adapter::fetchCompendiumInfo
        );

        verifyNoInteractions(client);
    }
}