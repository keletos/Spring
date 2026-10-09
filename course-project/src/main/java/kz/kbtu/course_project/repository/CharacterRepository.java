package kz.kbtu.course_project.repository;

import kz.kbtu.course_project.entity.CharacterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface CharacterRepository extends JpaRepository<CharacterEntity, UUID> {

    // 1. Derived Query — поиск по роли (авто-генерация по названию метода, слайд 24)
    Page<CharacterEntity> findByRoleIgnoreCase(String role, Pageable pageable);

    // 2. Кастомный JPQL запрос с аннотацией @Query (слайд 25)
    // Например, находим персонажей, у которых критически мало человечности (Cyberpsychosis risk)
    @Query("SELECT c FROM CharacterEntity c WHERE c.currentHumanity <= :threshold")
    Page<CharacterEntity> findAtRiskCharacters(@Param("threshold") int threshold, Pageable pageable);
}
