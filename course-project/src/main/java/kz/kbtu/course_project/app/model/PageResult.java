package kz.kbtu.course_project.app.model;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public PageResult {
        content = List.copyOf(content);
    }
}