package kz.kbtu.course_project.externalAPI;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import kz.kbtu.course_project.infra.external.github.GitHubCompendiumClient;
import kz.kbtu.course_project.infra.external.github.GitHubRepositoryResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class GitHubCompendiumClientTest {

    private static final String BASE_URL = "https://api.github.com";

    private static final String COMPENDIUM_PATH =
            "/repos/DankTrain11949/CyberPunk-Red-Fan-Compendium";

    @Test
    void fetchCompendiumRepository_shouldSendGetRequestAndDeserializeResponse() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(BASE_URL);

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        GitHubCompendiumClient client =
                new GitHubCompendiumClient(builder.build(), COMPENDIUM_PATH);

        String responseJson = """
                {
                    "full_name": "DankTrain11949/CyberPunk-Red-Fan-Compendium",
                    "description": "A compendium for Cyberpunk Red",
                    "html_url": "https://github.com/DankTrain11949/CyberPunk-Red-Fan-Compendium",
                    "stargazers_count": 8,
                    "updated_at": "2026-05-24T04:20:09Z",
                    "id": 123456
                }
                """;

        server.expect(requestTo(BASE_URL + COMPENDIUM_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        responseJson,
                        MediaType.APPLICATION_JSON
                ));

        GitHubRepositoryResponse response =
                client.fetchCompendiumRepository();

        assertNotNull(response);
        assertEquals(
                "DankTrain11949/CyberPunk-Red-Fan-Compendium",
                response.fullName()
        );
        assertEquals("A compendium for Cyberpunk Red", response.description());
        assertEquals(8, response.stars());
        assertEquals(
                "https://github.com/DankTrain11949/CyberPunk-Red-Fan-Compendium",
                response.htmlUrl()
        );
        assertEquals(
                "2026-05-24T04:20:09Z",
                response.updatedAt().toString()
        );

        server.verify();
    }

    @Test
    void fetchCompendiumRepository_whenGitHubReturns404_shouldThrowRestClientException() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(BASE_URL);

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        GitHubCompendiumClient client =
                new GitHubCompendiumClient(builder.build(), COMPENDIUM_PATH);

        server.expect(requestTo(BASE_URL + COMPENDIUM_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(org.springframework.http.HttpStatus.NOT_FOUND));

        assertThrows(
                org.springframework.web.client.RestClientException.class,
                client::fetchCompendiumRepository
        );

        server.verify();
    }
}
