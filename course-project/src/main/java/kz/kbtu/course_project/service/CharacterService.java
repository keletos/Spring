package kz.kbtu.course_project.service;

import kz.kbtu.course_project.dto.CreateCharacterRequest;
import kz.kbtu.course_project.exception.CharacterNotFoundException;
import kz.kbtu.course_project.dto.CharacterResponse;
import kz.kbtu.course_project.model.Character;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CharacterService {

    private final Map<UUID, Character> memoryDb = new ConcurrentHashMap<>();

    @Value("${game.starting-eddies}")
    private int startingEddies;

    public CharacterResponse createCharacter(CreateCharacterRequest request) {
        UUID id = UUID.randomUUID();

        Character character = new Character(
            id, request.name(), request.role(), startingEddies,
            request.intelligence(), request.reflex(), request.dexterity(),
            request.technology(), request.cool(), request.will(),
            request.movement(), request.body(), request.empathy(), request.luck()
        );

        int calculatedHp = 10 + (5 * (int) Math.ceil((character.getBody() + character.getWill()) / 2.0));
        int calculatedHumanity = character.getEmpathy() * 10;
        int calculatedLuckPoints = character.getLuck();

        character.setMaxHitPoints(calculatedHp);
        character.setCurrentHitPoints(calculatedHp);
        character.setMaxHumanity(calculatedHumanity);
        character.setCurrentHumanity(calculatedHumanity);
        character.setCurrentLuckPoints(calculatedLuckPoints);

        memoryDb.put(id, character);

        return mapToResponse(character);
    }

    public CharacterResponse getCharacter(UUID id) {
        Character character = memoryDb.get(id);
        if (character == null) {
            throw new CharacterNotFoundException(id);
        }
        return mapToResponse(character);
    }

    private CharacterResponse mapToResponse(Character c) {
        return new CharacterResponse(
            c.getId(), c.getName(), c.getRole(), 
            c.getLevel(), c.getEddies(),
            c.getMaxHitPoints(), c.getMaxHumanity(), 
            c.getCurrentHitPoints(), c.getCurrentHumanity(), c.getCurrentLuckPoints(),
            c.getIntelligence(), c.getReflex(), c.getDexterity(),c.getTechnology(), c.getCool(), c.getWill(), c.getMovement(), c.getBody(), c.getEmpathy(), c.getLuck()
        );
    }

}
