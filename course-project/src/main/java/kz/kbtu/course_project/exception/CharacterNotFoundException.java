
package kz.kbtu.course_project.exception;

import java.util.UUID;

public class CharacterNotFoundException extends RuntimeException {

    public CharacterNotFoundException(UUID id) {
        super("Character with ID " + id + " was not found.");
    }
}
