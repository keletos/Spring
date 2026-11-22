
package kz.kbtu.course_project.infra.persistence.adapter;

import kz.kbtu.course_project.app.exception.CharacterNotFoundException;
import kz.kbtu.course_project.app.model.PageResult;
import kz.kbtu.course_project.app.model.PaginationQuery;
import kz.kbtu.course_project.app.model.SortOrder;
import kz.kbtu.course_project.app.port.out.CharacterRepositoryPort;
import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.infra.persistence.entity.CharacterEntity;
import kz.kbtu.course_project.infra.persistence.mapper.CharacterPersistenceMapper;
import kz.kbtu.course_project.infra.persistence.repository.CharacterRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CharacterPersistenceAdapter implements CharacterRepositoryPort {

    private final CharacterRepository characterRepository;
    private final CharacterPersistenceMapper persistenceMapper;

    public CharacterPersistenceAdapter(
            CharacterRepository characterRepository,
            CharacterPersistenceMapper persistenceMapper
    ) {
        this.characterRepository = characterRepository;
        this.persistenceMapper = persistenceMapper;
    }

    @Override
    public Character save(Character character) {
        CharacterEntity entity = persistenceMapper.toEntity(character);
        CharacterEntity savedEntity = characterRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Character> findById(UUID id) {
        return characterRepository.findById(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public PageResult<Character> findAll(PaginationQuery query) {
        Sort sort = Sort.by(
                query.sort().stream()
                        .map(order -> new Sort.Order(
                                order.direction() == SortOrder.Direction.ASC
                                        ? Sort.Direction.ASC
                                        : Sort.Direction.DESC,
                                order.property()
                        ))
                        .toList()
        );

        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                sort
        );

        Page<Character> page = characterRepository.findAll(pageable)
                .map(persistenceMapper::toDomain);

        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public Character update(UUID id, Character character) {
        CharacterEntity entity = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        persistenceMapper.updateEntity(character, entity);

        return persistenceMapper.toDomain(entity);
    }

    @Override
    public void deleteById(UUID id) {
        characterRepository.deleteById(id);
    }
}
