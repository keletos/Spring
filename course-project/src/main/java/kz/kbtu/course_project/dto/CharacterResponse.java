package kz.kbtu.course_project.dto;

import java.util.UUID;

public record CharacterResponse(
    UUID id,
    String name,
    String role,

    // Stats
    int level,
    int eddies,

    int maxHealth,
    int maxHumanity,

    int currentHealth,
    int currentHumanity,
    int currentLuckPoints,

    // Characteristics
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
