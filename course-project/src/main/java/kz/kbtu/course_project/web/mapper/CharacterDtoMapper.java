package kz.kbtu.course_project.web.mapper;

import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.web.dto.CharacterResponse;
import kz.kbtu.course_project.web.dto.CreateCharacterRequest;
import kz.kbtu.course_project.web.dto.UpdateCharacterRequest;

import org.springframework.stereotype.Component;

@Component
public class CharacterDtoMapper {

    private static final int DEFAULT_EDDIES = 2550;

    // API DTO -> Domain (Create)
    public Character toDomain(CreateCharacterRequest request) {
        return new Character(
                null,
                request.name(),
                request.role(),
                request.eddies() != null
                        ? request.eddies()
                        : DEFAULT_EDDIES,
                request.intelligence(),
                request.reflex(),
                request.dexterity(),
                request.technology(),
                request.cool(),
                request.will(),
                request.movement(),
                request.body(),
                request.empathy(),
                request.luck()
        );
    }

    // API DTO -> Domain (Update)
    public Character toDomain(UpdateCharacterRequest request) {
        return new Character(
                null,
                request.name(),
                request.role(),
                request.eddies(),
                request.intelligence(),
                request.reflex(),
                request.dexterity(),
                request.technology(),
                request.cool(),
                request.will(),
                request.movement(),
                request.body(),
                request.empathy(),
                request.luck()
        );
    }

    // Domain -> API DTO
    public CharacterResponse toResponse(Character c) {
        return new CharacterResponse(
                c.getId(),
                c.getName(),
                c.getRole(),
                c.getLevel(),
                c.getEddies(),
                c.getMaxHitPoints(),
                c.getMaxHumanity(),
                c.getCurrentHitPoints(),
                c.getCurrentHumanity(),
                c.getCurrentLuckPoints(),
                c.getIntelligence(),
                c.getReflex(),
                c.getDexterity(),
                c.getTechnology(),
                c.getCool(),
                c.getWill(),
                c.getMovement(),
                c.getBody(),
                c.getEmpathy(),
                c.getLuck()
        );
    }
}
