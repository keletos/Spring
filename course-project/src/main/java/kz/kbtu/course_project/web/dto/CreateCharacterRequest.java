
package kz.kbtu.course_project.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCharacterRequest(

    @NotBlank(message = "Name must not be blank")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    String name,

    @NotBlank(message = "Role must not be blank")
    @Size(max = 50, message = "Role must not exceed 50 characters")
    String role,

    @Min(0)
    Integer eddies,

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

) {
    // Constructor without eddies
    public CreateCharacterRequest(
        String name,
        String role,
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
    ) {
        this(name, role, null, intelligence, reflex, dexterity,
             technology, cool, will, movement, body, empathy, luck);
    }
}
