package kz.kbtu.course_project.infra.persistence.mapper;

import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.infra.persistence.entity.CharacterEntity;

import org.springframework.stereotype.Component;

@Component
public class CharacterPersistenceMapper {

    // Database entity -> Domain object
    public Character toDomain(CharacterEntity entity) {
        Character character = new Character(
                entity.getId(),
                entity.getName(),
                entity.getRole(),
                entity.getEddies(),
                entity.getIntelligence(),
                entity.getReflex(),
                entity.getDexterity(),
                entity.getTechnology(),
                entity.getCool(),
                entity.getWill(),
                entity.getMovement(),
                entity.getBody(),
                entity.getEmpathy(),
                entity.getLuck()
        );

        character.setLevel(entity.getLevel());
        character.setMaxHitPoints(entity.getMaxHitPoints());
        character.setMaxHumanity(entity.getMaxHumanity());
        character.setCurrentHitPoints(entity.getCurrentHitPoints());
        character.setCurrentHumanity(entity.getCurrentHumanity());
        character.setCurrentLuckPoints(entity.getCurrentLuckPoints());

        return character;
    }

    // Domain object -> New database entity
    public CharacterEntity toEntity(Character character) {
        CharacterEntity entity = new CharacterEntity(
                character.getName(),
                character.getRole(),
                character.getEddies(),
                character.getIntelligence(),
                character.getReflex(),
                character.getDexterity(),
                character.getTechnology(),
                character.getCool(),
                character.getWill(),
                character.getMovement(),
                character.getBody(),
                character.getEmpathy(),
                character.getLuck()
        );

        copyStatsToEntity(character, entity);

        return entity;
    }

    // Copy domain data into an existing database entity
    public void updateEntity(Character character, CharacterEntity entity) {
        entity.setName(character.getName());
        entity.setRole(character.getRole());
        entity.setEddies(character.getEddies());

        entity.setIntelligence(character.getIntelligence());
        entity.setReflex(character.getReflex());
        entity.setDexterity(character.getDexterity());
        entity.setTechnology(character.getTechnology());
        entity.setCool(character.getCool());
        entity.setWill(character.getWill());
        entity.setMovement(character.getMovement());
        entity.setBody(character.getBody());
        entity.setEmpathy(character.getEmpathy());
        entity.setLuck(character.getLuck());

        copyStatsToEntity(character, entity);
    }

    private void copyStatsToEntity(Character character, CharacterEntity entity) {
        entity.setLevel(character.getLevel());
        entity.setMaxHitPoints(character.getMaxHitPoints());
        entity.setMaxHumanity(character.getMaxHumanity());
        entity.setCurrentHitPoints(character.getCurrentHitPoints());
        entity.setCurrentHumanity(character.getCurrentHumanity());
        entity.setCurrentLuckPoints(character.getCurrentLuckPoints());
    }
}
