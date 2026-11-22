package kz.kbtu.course_project.config;

import kz.kbtu.course_project.app.port.out.CompendiumRepositoryPort;
import kz.kbtu.course_project.app.service.CompendiumService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompendiumServiceConfig {

    @Bean
    public CompendiumService compendiumService(
            CompendiumRepositoryPort compendiumRepository,
            @Value("${integrations.github.compendium-url}") String fallbackRepositoryUrl
    ) {
        return new CompendiumService(compendiumRepository, fallbackRepositoryUrl);
    }
}
