package kz.kbtu.course_project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCharacterRequest(
    @NotBlank(message = "Имя персонажа не должно быть пустым")
    @Size(max = 50, message = "Имя не должно превышать 50 символов")
    String name,

    @NotBlank(message = "Роль (класс) обязательна")
    String role,

    @Min(1) @Max(10) int intelligence,
    @Min(1) @Max(10) int reflex,
    @Min(1) @Max(10) int dexterity,
    @Min(1) @Max(10) int technology,
    @Min(1) @Max(10) int cool,
    @Min(1) @Max(10) int will,
    @Min(1) @Max(10) int movement,
    @Min(1) @Max(10) int body,
    @Min(1) @Max(10) int empathy,
    @Min(1) @Max(10) int luck
) {}
