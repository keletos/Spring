package kz.kbtu.course_project.service;

import kz.kbtu.course_project.dto.CreateCharacterRequest;
import kz.kbtu.course_project.dto.CharacterResponse;
import kz.kbtu.course_project.entity.CharacterEntity;
import kz.kbtu.course_project.exception.CharacterNotFoundException;
import kz.kbtu.course_project.repository.CharacterRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;

    @Value("${game.startingEddies}")
    private int startingEddies;

    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Transactional
    public CharacterResponse createCharacter(CreateCharacterRequest request) {
        CharacterEntity entity = new CharacterEntity(
            request.name(), request.role(), startingEddies,
            request.intelligence(), request.reflex(), request.dexterity(),
            request.technology(), request.cool(), request.will(),
            request.movement(), request.body(), request.empathy(), request.luck()
        );

        int calculatedHp = 10 + (5 * (int) Math.ceil((entity.getBody() + entity.getWill()) / 2.0));
        int calculatedHumanity = entity.getEmpathy() * 10;
        int calculatedLuckPoints = entity.getLuck();

        entity.setMaxHitPoints(calculatedHp);
        entity.setCurrentHitPoints(calculatedHp);
        entity.setMaxHumanity(calculatedHumanity);
        entity.setCurrentHumanity(calculatedHumanity);
        entity.setCurrentLuckPoints(calculatedLuckPoints);

        CharacterEntity savedEntity = characterRepository.save(entity);

        return mapToResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public CharacterResponse getCharacter(UUID id) {
        CharacterEntity entity = characterRepository.findById(id)
            .orElseThrow(() -> new CharacterNotFoundException(id));
        return mapToResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<CharacterResponse> getAllCharacters(Pageable pageable) {
        return characterRepository.findAll(pageable)
            .map(this::mapToResponse);
    }

    private CharacterResponse mapToResponse(CharacterEntity c) {
        return new CharacterResponse(
            c.getId(), c.getName(), c.getRole(), 
            c.getLevel(), c.getEddies(),
            c.getMaxHitPoints(), c.getMaxHumanity(), 
            c.getCurrentHitPoints(), c.getCurrentHumanity(), c.getCurrentLuckPoints(),
            c.getIntelligence(), c.getReflex(), c.getDexterity(), c.getTechnology(), 
            c.getCool(), c.getWill(), c.getMovement(), c.getBody(), c.getEmpathy(), c.getLuck()
        );
    }
}
