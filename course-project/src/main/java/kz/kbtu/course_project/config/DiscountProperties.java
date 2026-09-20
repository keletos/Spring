package kz.kbtu.course_project.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.features.discount")
public record DiscountProperties(
    boolean enabled,
    @Min(0) int percentage
) {}

