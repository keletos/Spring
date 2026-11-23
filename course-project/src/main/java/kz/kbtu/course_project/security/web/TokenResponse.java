package kz.kbtu.course_project.security.web;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
