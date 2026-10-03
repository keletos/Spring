package kz.kbtu.course_project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CharacterNotFoundException.class)
    public ProblemDetail handleCharacterNotFound(CharacterNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, 
                ex.getMessage()
        );
        problemDetail.setTitle("Ресурс не найден");
        problemDetail.setType(URI.create("https://kbtu.kz"));
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, 
                "Некоторые поля запроса заполнены некорректно."
        );
        problemDetail.setTitle("Ошибка валидации данных");
        problemDetail.setType(URI.create("https://kbtu.kz"));

        Map<String, String> invalidFields = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            invalidFields.put(error.getField(), error.getDefaultMessage())
        );

        problemDetail.setProperty("invalid_fields", invalidFields);

        return problemDetail;
    }
}
