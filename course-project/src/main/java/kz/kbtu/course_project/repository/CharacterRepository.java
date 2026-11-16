package kz.kbtu.course_project.repository;

import kz.kbtu.course_project.entity.CharacterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface CharacterRepository extends JpaRepository<CharacterEntity, UUID> {

    Page<CharacterEntity> findByRoleIgnoreCase(String role, Pageable pageable);

    @Query("SELECT c FROM CharacterEntity c WHERE c.currentHumanity <= :threshold")
    Page<CharacterEntity> findAtRiskCharacters(@Param("threshold") int threshold, Pageable pageable);
}
