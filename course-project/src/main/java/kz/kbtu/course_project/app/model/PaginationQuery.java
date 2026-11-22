package kz.kbtu.course_project.app.model;

import java.util.List;

public record PaginationQuery(
        int page,
        int size,
        List<SortOrder> sort
) {
    public PaginationQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page index cannot be negative.");
        }

        if (size < 1) {
            throw new IllegalArgumentException("Page size must be positive.");
        }

        sort = sort == null ? List.of() : List.copyOf(sort);
    }
}