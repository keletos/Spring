package kz.kbtu.course_project.app.service;

import kz.kbtu.course_project.app.exception.CompendiumUnavailableException;
import kz.kbtu.course_project.app.model.CompendiumInfo;
import kz.kbtu.course_project.app.port.out.CompendiumRepositoryPort;

import java.util.logging.Logger;

public class CompendiumService {

    private static final Logger logger =
            Logger.getLogger(CompendiumService.class.getName());

    private static final String REPOSITORY_NAME =
            "DankTrain11949/CyberPunk-Red-Fan-Compendium";

    private final CompendiumRepositoryPort compendiumRepository;
    private final String fallbackRepositoryUrl;

    public CompendiumService(
            CompendiumRepositoryPort compendiumRepository,
            String fallbackRepositoryUrl
    ) {
        this.compendiumRepository = compendiumRepository;
        this.fallbackRepositoryUrl = fallbackRepositoryUrl;
    }

    public CompendiumInfo getCompendiumInfo() {
        try {
            CompendiumInfo repository =
                    compendiumRepository.fetchCompendiumInfo();

            if (repository == null) {
                logger.warning("GitHub returned an empty repository response.");
                return fallbackResponse();
            }

            return repository;

        } catch (CompendiumUnavailableException exception) {
            logger.warning(
                    "GitHub compendium request failed or circuit is open; using fallback: "
                            + exception.getMessage()
            );

            return fallbackResponse();
        }
    }

    private CompendiumInfo fallbackResponse() {
        return new CompendiumInfo(
                REPOSITORY_NAME,
                "Repository metadata is temporarily unavailable.",
                fallbackRepositoryUrl,
                0,
                null,
                false
        );
    }
}