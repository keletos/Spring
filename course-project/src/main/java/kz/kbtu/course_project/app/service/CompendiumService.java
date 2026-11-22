
package kz.kbtu.course_project.app.service;

import kz.kbtu.course_project.infra.external.github.GitHubCompendiumClient;
import kz.kbtu.course_project.infra.external.github.GitHubRepositoryResponse;
import kz.kbtu.course_project.web.dto.CompendiumInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class CompendiumService {

    private static final Logger logger =
            LoggerFactory.getLogger(CompendiumService.class);

    private final GitHubCompendiumClient compendiumClient;
    private final String fallbackRepositoryUrl;

    public CompendiumService(
            GitHubCompendiumClient compendiumClient,
            @Value("${integrations.github.compendium-url}") String fallbackRepositoryUrl
    ) {
        this.compendiumClient = compendiumClient;
        this.fallbackRepositoryUrl = fallbackRepositoryUrl;
    }

    public CompendiumInfoResponse getCompendiumInfo() {
        try {
            GitHubRepositoryResponse repository =
                    compendiumClient.fetchCompendiumRepository();

            if (repository == null) {
                logger.warn("GitHub returned an empty repository response.");
                return fallbackResponse();
            }

            return new CompendiumInfoResponse(
                    repository.fullName(),
                    repository.description(),
                    repository.htmlUrl(),
                    repository.stars(),
                    repository.updatedAt(),
                    true,
                    "Repository metadata retrieved successfully."
            );
        } catch (RestClientException exception) {
            logger.warn(
                    "Failed to retrieve Cyberpunk RED compendium metadata from GitHub: {}",
                    exception.getMessage()
            );
            return fallbackResponse();
        }
    }

    private CompendiumInfoResponse fallbackResponse() {
        return new CompendiumInfoResponse(
                "DankTrain11949/CyberPunk-Red-Fan-Compendium",
                "Repository metadata is temporarily unavailable.",
                fallbackRepositoryUrl,
                0,
                null,
                false,
                "The external service is temporarily unavailable. Please try again later."
        );
    }
}
