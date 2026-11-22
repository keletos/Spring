package kz.kbtu.course_project.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.beans.TypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleTypeMismatch_withGenericException_shouldUseDefaultParameterName() {
        TypeMismatchException exception =
                new TypeMismatchException("abc", Integer.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/characters");

        ProblemDetail response =
                handler.handleTypeMismatch(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid Parameter", response.getTitle());
        assertEquals(
                "Parameter 'parameter' has an invalid value.",
                response.getDetail()
        );
        assertEquals("/api/v1/characters", response.getInstance().toString());
        assertTrue(
                ((java.util.Map<?, ?>) response.getProperties()
                        .get("invalid_fields")).isEmpty()
        );
    }
}