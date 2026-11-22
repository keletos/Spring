package kz.kbtu.course_project.infra.external.github;

import org.springframework.web.client.RestClient;

public class GitHubCompendiumClient {

    private final RestClient restClient;
    private final String compendiumPath;

    public GitHubCompendiumClient(RestClient restClient, String compendiumPath) {
        this.restClient = restClient;
        this.compendiumPath = compendiumPath;
    }

    public GitHubRepositoryResponse fetchCompendiumRepository() {
        return restClient.get()
                .uri(compendiumPath)
                .retrieve()
                .body(GitHubRepositoryResponse.class);
    }
}
