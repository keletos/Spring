
package kz.kbtu.course_project.web.mapper;

import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.web.dto.CharacterResponse;

import org.springframework.stereotype.Component;

@Component
public class CharacterDtoMapper {

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
