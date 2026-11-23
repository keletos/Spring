package kz.kbtu.course_project.web.exception;

import kz.kbtu.course_project.app.exception.CharacterNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationFailure(
            AuthenticationException ex,
            HttpServletRequest request) {
        return createProblem(HttpStatus.UNAUTHORIZED, "Authentication Failed",
                "Invalid username or password.", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        return createProblem(HttpStatus.FORBIDDEN, "Forbidden",
                "You do not have permission to perform this action.", request);
    }

    @ExceptionHandler(CharacterNotFoundException.class)
    public ProblemDetail handleCharacterNotFound(
            CharacterNotFoundException ex,
            HttpServletRequest request) {
        return createProblem(HttpStatus.NOT_FOUND, "Character Not Found",
                ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "One or more request fields contain invalid values.",
                request
        );

        Map<String, String> invalidFields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                invalidFields.put(error.getField(), error.getDefaultMessage()));
        problem.setProperty("invalid_fields", invalidFields);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableMessage(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return createProblem(HttpStatus.BAD_REQUEST, "Invalid Request Body",
                "The request body is missing or contains invalid JSON.", request);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(
            TypeMismatchException ex,
            HttpServletRequest request) {
        String parameterName = ex instanceof MethodArgumentTypeMismatchException mismatch
                ? mismatch.getName()
                : "parameter";

        return createProblem(HttpStatus.BAD_REQUEST, "Invalid Parameter",
                "Parameter '" + parameterName + "' has an invalid value.", request);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLocking(
            OptimisticLockingFailureException ex,
            HttpServletRequest request) {
        return createProblem(HttpStatus.CONFLICT, "Resource Conflict",
                "The character was modified by another request. Please retrieve the latest version and try again.",
                request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {
        logger.error("Unhandled exception for request {} {}", request.getMethod(),
                request.getRequestURI(), ex);
        return createProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error",
                "An unexpected error occurred.", request);
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("about:blank"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("invalid_fields", Map.of());
        return problem;
    }
}
