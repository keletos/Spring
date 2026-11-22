package kz.kbtu.course_project.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ProblemDetail;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.kbtu.course_project.config.ProfileProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Application", description = "Application profile information.")
@RestController
public class ProfileCheckController {

    private final ProfileProperties profileProperties;

    public ProfileCheckController(ProfileProperties profileProperties) {
        this.profileProperties = profileProperties;
    }

    @Operation(
            summary = "Get the active application profile",
            description = "Returns the configured game mode for the active application profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active profile returned."),
            @ApiResponse(responseCode = "500", description = "An unexpected server error occurred.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/profile")
    public String checkDiscount() {
        return "Текущий профиль - " + profileProperties.mode();
    }
}
