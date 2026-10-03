package kz.kbtu.course_project.exception;

import java.util.UUID;

public class CharacterNotFoundException extends RuntimeException {
    public CharacterNotFoundException(UUID id) {
        super("Персонаж с ID " + id + " не найден в текущей игровой сессии.");
    }
}