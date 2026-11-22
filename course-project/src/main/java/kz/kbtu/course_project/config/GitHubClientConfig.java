package kz.kbtu.course_project.config;

import kz.kbtu.course_project.infra.external.github.GitHubCompendiumClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class GitHubClientConfig {

    @Bean
    public GitHubCompendiumClient gitHubCompendiumClient(
            RestClient.Builder restClientBuilder,
            @Value("${integrations.github.base-url}") String baseUrl,
            @Value("${integrations.github.compendium-path}") String compendiumPath,
            @Value("${integrations.github.connect-timeout}") Duration connectTimeout,
            @Value("${integrations.github.read-timeout}") Duration readTimeout
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(readTimeout);

        RestClient restClient = restClientBuilder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .defaultHeader("User-Agent", "cyberpunk-red-character-api")
                .build();

        return new GitHubCompendiumClient(restClient, compendiumPath);
    }
}
