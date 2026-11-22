package kz.kbtu.course_project.infra.external.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepositoryResponse(
        @JsonProperty("full_name") String fullName,
        String description,
        @JsonProperty("html_url") String htmlUrl,
        @JsonProperty("stargazers_count") int stars,
        @JsonProperty("updated_at") Instant updatedAt
) {
}