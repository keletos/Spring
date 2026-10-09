package kz.kbtu.course_project.controller;

import jakarta.validation.Valid;
import kz.kbtu.course_project.dto.CreateCharacterRequest;
import kz.kbtu.course_project.dto.CharacterResponse;
import kz.kbtu.course_project.service.CharacterService;
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
    
    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @PostMapping
    public ResponseEntity<CharacterResponse> create(@Valid @RequestBody CreateCharacterRequest request, UriComponentsBuilder uriBuilder) {
        
        CharacterResponse response = characterService.createCharacter(request);
        
        URI location = uriBuilder.path("/api/v1/characters/{id}")
                .buildAndExpand(response.id())
                .toUri();
                
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getOne(@PathVariable UUID id) {
        CharacterResponse response = characterService.getCharacter(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public PagedModel<CharacterResponse> getAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        
        return new PagedModel<>(characterService.getAllCharacters(pageable));
    }
}
