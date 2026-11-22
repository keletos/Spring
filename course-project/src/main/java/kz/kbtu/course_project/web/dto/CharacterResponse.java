package kz.kbtu.course_project.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Character representation returned by the API.")
public record CharacterResponse(
    @Schema(description = "Unique identifier of the character.", example = "4e3b6f1a-1f0d-4a8b-9f2c-1e5d6a7b8c9d")
    UUID id,
    @Schema(description = "Character's display name.", example = "Morgan Blackhand")
    String name,
    @Schema(description = "Character's role/archetype.", example = "Solo")
    String role,

    @Schema(description = "Character progression level.", example = "1")
    int level,
    @Schema(description = "Current eurodollar balance.", example = "500")
    int eddies,

    int maxHealth,
    int maxHumanity,

    int currentHealth,
    int currentHumanity,
    int currentLuckPoints,

    int intelligence,
    int reflex,
    int dexterity,
    int technology,
    int cool,
    int will,
    int movement,
    int body,
    int empathy,
    int luck
) {}
