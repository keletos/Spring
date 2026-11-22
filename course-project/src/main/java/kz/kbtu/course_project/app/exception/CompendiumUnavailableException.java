package kz.kbtu.course_project.app.exception;

public class CompendiumUnavailableException extends RuntimeException {

    public CompendiumUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompendiumUnavailableException(String message) {
        super(message);
    }
}