package kz.kbtu.course_project.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Public metadata about the CyberPunk RED Fan Compendium repository.")
public record CompendiumInfoResponse(
        @Schema(description = "GitHub repository full name", example = "DankTrain11949/CyberPunk-Red-Fan-Compendium")
        String repository,

        @Schema(description = "Repository description")
        String description,

        @Schema(description = "Link to the repository", example = "https://github.com/DankTrain11949/CyberPunk-Red-Fan-Compendium")
        String url,

        @Schema(description = "Number of GitHub stars", example = "42")
        int stars,

        @Schema(description = "Last repository update timestamp")
        Instant lastUpdated,

        @Schema(description = "Whether the information was retrieved from GitHub", example = "true")
        boolean available,

        @Schema(description = "Additional information about the result")
        String message
) {
}
