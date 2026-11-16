package kz.kbtu.course_project.service;

import kz.kbtu.course_project.dto.CreateCharacterRequest;
import kz.kbtu.course_project.dto.UpdateCharacterRequest;
import kz.kbtu.course_project.dto.CharacterResponse;
import kz.kbtu.course_project.entity.CharacterEntity;
import kz.kbtu.course_project.exception.CharacterNotFoundException;
import kz.kbtu.course_project.repository.CharacterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;

    private static final int DEFAULT_EDDIES = 2550;

    public CharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Transactional
    public CharacterResponse createCharacter(CreateCharacterRequest request) {
        CharacterEntity entity = new CharacterEntity(
            request.name(), request.role(), DEFAULT_EDDIES,
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

    @Transactional
    public CharacterResponse updateCharacter(UUID id, UpdateCharacterRequest request) {

        CharacterEntity entity = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        entity.setName(request.name());
        entity.setRole(request.role());
        
        entity.setEddies(request.eddies());

        entity.setIntelligence(request.intelligence());
        entity.setReflex(request.reflex());
        entity.setDexterity(request.dexterity());
        entity.setTechnology(request.technology());
        entity.setCool(request.cool());
        entity.setWill(request.will());
        entity.setMovement(request.movement());
        entity.setBody(request.body());
        entity.setEmpathy(request.empathy());
        entity.setLuck(request.luck());
        
        int calculatedHp = 10 + (5 * (int) Math.ceil(
                (entity.getBody() + entity.getWill()) / 2.0
        ));

        int calculatedHumanity = entity.getEmpathy() * 10;

        entity.setMaxHitPoints(calculatedHp);
        entity.setMaxHumanity(calculatedHumanity);

        entity.setCurrentHitPoints(
                Math.min(entity.getCurrentHitPoints(), calculatedHp)
        );

        entity.setCurrentHumanity(
                Math.min(entity.getCurrentHumanity(), calculatedHumanity)
        );

        entity.setCurrentLuckPoints(
                Math.min(entity.getCurrentLuckPoints(), request.luck())
        );

        return mapToResponse(entity);
    }

    @Transactional
    public void deleteCharacter(UUID id) {

        CharacterEntity entity = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        characterRepository.delete(entity);
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
