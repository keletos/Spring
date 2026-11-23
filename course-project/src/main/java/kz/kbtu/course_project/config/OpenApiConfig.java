package kz.kbtu.course_project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courseProjectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cyberpunk RED Character API")
                        .version("1.0.0")
                        .description("""
                                REST API for managing Cyberpunk RED player characters
                                and retrieving external compendium metadata.
                                All endpoints return documented response schemas.
                                """))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi apiV1() {
        return GroupedOpenApi.builder()
                .group("api-v1")
                .pathsToMatch("/api/v1/**", "/api/auth/**", "/profile")
                .build();
    }

    @Bean
    public GroupedOpenApi characterApiV1() {
        return GroupedOpenApi.builder()
                .group("characters-v1")
                .pathsToMatch("/api/v1/characters/**", "/api/v1/characters")
                .build();
    }
}
