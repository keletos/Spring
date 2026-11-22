
package kz.kbtu.course_project.infra.external.github;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import kz.kbtu.course_project.app.exception.CompendiumUnavailableException;
import kz.kbtu.course_project.app.model.CompendiumInfo;
import kz.kbtu.course_project.app.port.out.CompendiumRepositoryPort;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.function.Supplier;

@Component
public class GitHubCompendiumAdapter implements CompendiumRepositoryPort {

    private final GitHubCompendiumClient compendiumClient;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public GitHubCompendiumAdapter(
            GitHubCompendiumClient compendiumClient,
            Retry gitHubRetry,
            CircuitBreaker gitHubCircuitBreaker
    ) {
        this.compendiumClient = compendiumClient;
        this.retry = gitHubRetry;
        this.circuitBreaker = gitHubCircuitBreaker;
    }

    @Override
    public CompendiumInfo fetchCompendiumInfo() {
        try {
            Supplier<GitHubRepositoryResponse> retriedCall =
                    Retry.decorateSupplier(
                            retry,
                            compendiumClient::fetchCompendiumRepository
                    );

            Supplier<GitHubRepositoryResponse> protectedCall =
                    CircuitBreaker.decorateSupplier(
                            circuitBreaker,
                            retriedCall
                    );

            GitHubRepositoryResponse repository = protectedCall.get();

            if (repository == null) {
                throw new CompendiumUnavailableException(
                        "GitHub returned an empty repository response."
                );
            }

            return new CompendiumInfo(
                    repository.fullName(),
                    repository.description(),
                    repository.htmlUrl(),
                    repository.stars(),
                    repository.updatedAt(),
                    true
            );

        } catch (RestClientException | CallNotPermittedException exception) {
            throw new CompendiumUnavailableException(
                    "GitHub compendium is currently unavailable.",
                    exception
            );
        }
    }
}