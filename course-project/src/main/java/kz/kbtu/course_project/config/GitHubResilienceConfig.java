package kz.kbtu.course_project.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;

@Configuration
public class GitHubResilienceConfig {

    @Bean
    public Retry gitHubRetry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryOnException(exception ->
                        exception instanceof ResourceAccessException
                                || (exception instanceof RestClientResponseException response
                                && response.getStatusCode().is5xxServerError()))
                .build();

        return Retry.of("github-compendium", retryConfig);
    }

    @Bean
    public CircuitBreaker gitHubCircuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(15))
                .permittedNumberOfCallsInHalfOpenState(2)
                .ignoreException(exception ->
                        exception instanceof RestClientResponseException response
                                && response.getStatusCode().is4xxClientError())
                .build();

        return CircuitBreaker.of("github-compendium", circuitBreakerConfig);
    }
}
