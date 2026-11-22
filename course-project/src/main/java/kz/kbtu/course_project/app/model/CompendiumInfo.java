package kz.kbtu.course_project.app.model;

import java.time.Instant;

public record CompendiumInfo(
        String repository,
        String description,
        String url,
        int stars,
        Instant lastUpdated,
        boolean available
) {
}