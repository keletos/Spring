package kz.kbtu.course_project.app.model;

public record SortOrder(
        String property,
        Direction direction
) {
    public enum Direction {
        ASC,
        DESC
    }
}