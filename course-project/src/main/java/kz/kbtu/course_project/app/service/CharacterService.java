
package kz.kbtu.course_project.app.service;

import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.exception.CharacterNotFoundException;
import kz.kbtu.course_project.infra.persistence.entity.CharacterEntity;
import kz.kbtu.course_project.infra.persistence.mapper.CharacterPersistenceMapper;
import kz.kbtu.course_project.infra.persistence.repository.CharacterRepository;
import kz.kbtu.course_project.web.dto.CharacterResponse;
import kz.kbtu.course_project.web.dto.CreateCharacterRequest;
import kz.kbtu.course_project.web.dto.UpdateCharacterRequest;
import kz.kbtu.course_project.web.mapper.CharacterDtoMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final CharacterPersistenceMapper persistenceMapper;
    private final CharacterDtoMapper dtoMapper;

    private static final int DEFAULT_EDDIES = 2550;

    public CharacterService(
            CharacterRepository characterRepository,
            CharacterPersistenceMapper persistenceMapper,
            CharacterDtoMapper dtoMapper
    ) {
        this.characterRepository = characterRepository;
        this.persistenceMapper = persistenceMapper;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public CharacterResponse createCharacter(CreateCharacterRequest request) {

        Character character = new Character(
                null,
                request.name(),
                request.role(),
                DEFAULT_EDDIES,
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

        character.initializeStats();

        CharacterEntity entity = persistenceMapper.toEntity(character);
        CharacterEntity savedEntity = characterRepository.save(entity);

        Character savedCharacter = persistenceMapper.toDomain(savedEntity);

        return dtoMapper.toResponse(savedCharacter);
    }

    @Transactional
    public CharacterResponse updateCharacter(UUID id, UpdateCharacterRequest request) {

        CharacterEntity entity = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        Character character = persistenceMapper.toDomain(entity);

        character.setName(request.name());
        character.setRole(request.role());
        character.setEddies(request.eddies());

        character.updateCharacteristics(
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

        persistenceMapper.updateEntity(character, entity);

        return dtoMapper.toResponse(character);
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

        Character character = persistenceMapper.toDomain(entity);

        return dtoMapper.toResponse(character);
    }

    @Transactional(readOnly = true)
    public Page<CharacterResponse> getAllCharacters(Pageable pageable) {

        return characterRepository.findAll(pageable)
                .map(persistenceMapper::toDomain)
                .map(dtoMapper::toResponse);
    }
}
