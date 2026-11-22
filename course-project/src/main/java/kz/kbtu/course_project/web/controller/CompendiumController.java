package kz.kbtu.course_project.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.kbtu.course_project.app.service.CompendiumService;
import kz.kbtu.course_project.web.dto.CompendiumInfoResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "External resources",
        description = "Information retrieved from external services."
)
@RestController
@RequestMapping("/api/v1/compendium")
public class CompendiumController {

    private final CompendiumService compendiumService;

    public CompendiumController(CompendiumService compendiumService) {
        this.compendiumService = compendiumService;
    }

    @Operation(
            summary = "Get CyberPunk RED compendium metadata",
            description = "Retrieves public repository metadata from the GitHub REST API. " +
                    "If GitHub is unavailable, the endpoint returns a documented fallback response."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repository metadata or fallback information returned."),
            @ApiResponse(responseCode = "500", description = "An unexpected application error occurred.")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompendiumInfoResponse getCompendiumInfo() {
        return compendiumService.getCompendiumInfo();
    }
}
