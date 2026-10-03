package kz.kbtu.course_project.controller;

import jakarta.validation.Valid;
import kz.kbtu.course_project.dto.CreateCharacterRequest;
import kz.kbtu.course_project.dto.CharacterResponse;
import kz.kbtu.course_project.service.CharacterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/characters")
public class CharacterController {

    private final CharacterService characterService;

    // Внедряем сервис через конструктор (Spring сам его подставит)
    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    // 1. Создание персонажа (POST /api/v1/characters)
    @PostMapping
    public ResponseEntity<CharacterResponse> create(@Valid @RequestBody CreateCharacterRequest request, 
                                                    UriComponentsBuilder uriBuilder) {
        
        CharacterResponse response = characterService.createCharacter(request);
        
        // По правилам REST и SIS 1: возвращаем статус 201 Created 
        // и заголовок Location со ссылкой на созданный ресурс
        URI location = uriBuilder.path("/api/v1/characters/{id}")
                .buildAndExpand(response.id())
                .toUri();
                
        return ResponseEntity.created(location).body(response);
    }

    // 2. Получение персонажа по ID (GET /api/v1/characters/{id})
    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getOne(@PathVariable UUID id) {
        CharacterResponse response = characterService.getCharacter(id);
        return ResponseEntity.ok(response);
    }
}
