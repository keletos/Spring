
package kz.kbtu.course_project.web.controller;

import jakarta.validation.Valid;
import kz.kbtu.course_project.app.service.CharacterService;
import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.web.dto.CharacterResponse;
import kz.kbtu.course_project.web.dto.CreateCharacterRequest;
import kz.kbtu.course_project.web.dto.UpdateCharacterRequest;
import kz.kbtu.course_project.web.mapper.CharacterDtoMapper;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/characters")
public class CharacterController {

    private final CharacterService characterService;
    private final CharacterDtoMapper dtoMapper;

    public CharacterController(
            CharacterService characterService,
            CharacterDtoMapper dtoMapper
    ) {
        this.characterService = characterService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<CharacterResponse> create(
            @Valid @RequestBody CreateCharacterRequest request,
            UriComponentsBuilder uriBuilder
    ) {

        Character character = dtoMapper.toDomain(request);

        Character createdCharacter =
                characterService.createCharacter(character);

        CharacterResponse response =
                dtoMapper.toResponse(createdCharacter);

        URI location = uriBuilder.path("/api/v1/characters/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getOne(
            @PathVariable UUID id
    ) {

        Character character = characterService.getCharacter(id);

        return ResponseEntity.ok(dtoMapper.toResponse(character));
    }

    @GetMapping
    public PagedModel<CharacterResponse> getAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {

        return new PagedModel<>(
                characterService.getAllCharacters(pageable)
                        .map(dtoMapper::toResponse)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCharacterRequest request
    ) {

        Character updatedCharacter = dtoMapper.toDomain(request);

        Character character =
                characterService.updateCharacter(id, updatedCharacter);

        return ResponseEntity.ok(dtoMapper.toResponse(character));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        characterService.deleteCharacter(id);

        return ResponseEntity.noContent().build();
    }
}
