package kz.kbtu.course_project.app.service;

import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.exception.CharacterNotFoundException;
import kz.kbtu.course_project.infra.persistence.entity.CharacterEntity;
import kz.kbtu.course_project.infra.persistence.mapper.CharacterPersistenceMapper;
import kz.kbtu.course_project.infra.persistence.repository.CharacterRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final CharacterPersistenceMapper persistenceMapper;

    public CharacterService(
            CharacterRepository characterRepository,
            CharacterPersistenceMapper persistenceMapper
    ) {
        this.characterRepository = characterRepository;
        this.persistenceMapper = persistenceMapper;
    }

    @CacheEvict(
    value = {"characters", "charactersPage"},
    allEntries = true
    )
    @Transactional
    public Character createCharacter(Character character) {

        character.initializeStats();

        CharacterEntity entity = persistenceMapper.toEntity(character);
        CharacterEntity savedEntity = characterRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @CacheEvict(
    value = {"characters", "charactersPage"},
    allEntries = true
    )
    @Transactional
    public Character updateCharacter(UUID id, Character updatedCharacter) {

        CharacterEntity entity = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        Character character = persistenceMapper.toDomain(entity);

        character.setName(updatedCharacter.getName());
        character.setRole(updatedCharacter.getRole());
        character.updateEddies(updatedCharacter.getEddies());

        character.updateCharacteristics(
                updatedCharacter.getIntelligence(),
                updatedCharacter.getReflex(),
                updatedCharacter.getDexterity(),
                updatedCharacter.getTechnology(),
                updatedCharacter.getCool(),
                updatedCharacter.getWill(),
                updatedCharacter.getMovement(),
                updatedCharacter.getBody(),
                updatedCharacter.getEmpathy(),
                updatedCharacter.getLuck()
        );

        persistenceMapper.updateEntity(character, entity);

        return character;
    }

    @CacheEvict(
    value = {"characters", "charactersPage"},
    allEntries = true
    )
    @Transactional
    public void deleteCharacter(UUID id) {

        CharacterEntity entity = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        characterRepository.delete(entity);
    }

    @Cacheable(value = "characters", key = "#id")
    @Transactional(readOnly = true)
    public Character getCharacter(UUID id) {

        CharacterEntity entity = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        return persistenceMapper.toDomain(entity);
    }

    @Cacheable(value = "charactersPage", key = "#pageable")
    @Transactional(readOnly = true)
    public Page<Character> getAllCharacters(Pageable pageable) {

        return characterRepository.findAll(pageable)
                .map(persistenceMapper::toDomain);
    }
}
