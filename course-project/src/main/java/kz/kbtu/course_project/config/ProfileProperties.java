package kz.kbtu.course_project.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "game")
public record ProfileProperties(
    String mode,
    @Min(0) int startingEddies
) {}

