package kz.kbtu.course_project.app.port.out;

import kz.kbtu.course_project.app.model.PageResult;
import kz.kbtu.course_project.app.model.PaginationQuery;
import kz.kbtu.course_project.domain.Character;

import java.util.Optional;
import java.util.UUID;

public interface CharacterRepositoryPort {

    Character save(Character character);

    Optional<Character> findById(UUID id);

    PageResult<Character> findAll(PaginationQuery query);

    Character update(UUID id, Character character);

    void deleteById(UUID id);
}