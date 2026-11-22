package kz.kbtu.course_project.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload used to replace the editable fields of an existing character.")
public record UpdateCharacterRequest(

    @Schema(description = "Character's display name.", example = "Morgan Blackhand")
    @NotBlank(message = "Name must not be blank")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    String name,

    @Schema(description = "Character's role/archetype.", example = "Solo")
    @NotBlank(message = "Role must not be blank")
    @Size(max = 50, message = "Role must not exceed 50 characters")
    String role,

    @Schema(description = "Current eurodollar balance.", example = "1200")
    @NotNull(message = "Eddies must not be null")
    @Min(0)
    Integer eddies,

    @Schema(description = "Intelligence attribute (1-10).", example = "6")
    @Min(1) @Max(10) int intelligence,
    @Schema(description = "Reflex attribute (1-10).", example = "8")
    @Min(1) @Max(10) int reflex,
    @Schema(description = "Dexterity attribute (1-10).", example = "7")
    @Min(1) @Max(10) int dexterity,
    @Schema(description = "Technology attribute (1-10).", example = "5")
    @Min(1) @Max(10) int technology,
    @Schema(description = "Cool attribute (1-10).", example = "6")
    @Min(1) @Max(10) int cool,
    @Schema(description = "Will attribute (1-10).", example = "6")
    @Min(1) @Max(10) int will,
    @Schema(description = "Movement attribute (1-10).", example = "6")
    @Min(1) @Max(10) int movement,
    @Schema(description = "Body attribute (1-10).", example = "7")
    @Min(1) @Max(10) int body,
    @Schema(description = "Empathy attribute (1-10).", example = "5")
    @Min(1) @Max(10) int empathy,
    @Schema(description = "Luck attribute (1-10).", example = "4")
    @Min(1) @Max(10) int luck
) {}
