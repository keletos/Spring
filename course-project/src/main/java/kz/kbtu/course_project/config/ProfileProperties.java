package kz.kbtu.course_project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "game")
public record ProfileProperties(
    String mode
) {}

