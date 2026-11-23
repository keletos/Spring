package kz.kbtu.course_project.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kz.kbtu.course_project.app.model.PageResult;
import kz.kbtu.course_project.app.model.PaginationQuery;
import kz.kbtu.course_project.app.model.SortOrder;
import kz.kbtu.course_project.app.service.CharacterService;
import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.web.dto.CharacterResponse;
import kz.kbtu.course_project.web.dto.CreateCharacterRequest;
import kz.kbtu.course_project.web.dto.UpdateCharacterRequest;
import kz.kbtu.course_project.web.mapper.CharacterDtoMapper;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Tag(
        name = "Characters",
        description = "Create, retrieve, update and delete Cyberpunk RED player characters."
)
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

    @Operation(
            summary = "Create a character",
            description = "Creates a new character and returns its representation. The Location header contains the URI of the created resource."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Character created successfully.",
                    content = @Content(schema = @Schema(implementation = CharacterResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Request body is invalid or violates validation constraints.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected server error occurred.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<CharacterResponse> create(
            @Valid @RequestBody CreateCharacterRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        Character character = dtoMapper.toDomain(request);
        Character createdCharacter = characterService.createCharacter(character);
        CharacterResponse response = dtoMapper.toResponse(createdCharacter);

        URI location = uriBuilder.path("/api/v1/characters/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(
            summary = "Get a character by ID",
            description = "Returns one character identified by its UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Character found.",
                    content = @Content(schema = @Schema(implementation = CharacterResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The supplied ID is not a valid UUID.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "404", description = "No character exists with the supplied ID.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected server error occurred.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CharacterResponse> getOne(
            @Parameter(description = "UUID of the character to retrieve", required = true)
            @PathVariable UUID id
    ) {
        Character character = characterService.getCharacter(id);
        return ResponseEntity.ok(dtoMapper.toResponse(character));
    }

    @Operation(
            summary = "List characters",
            description = "Returns a paginated list of characters. Supports page, size and sort query parameters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of characters returned successfully.",
                    content = @Content(schema = @Schema(implementation = PagedModel.class))),
            @ApiResponse(responseCode = "400", description = "A pagination or sorting parameter is invalid.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected server error occurred.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public PagedModel<CharacterResponse> getAll(
        @ParameterObject
        @PageableDefault(size = 20, sort = "name")
        Pageable pageable
    ) {
        PaginationQuery query = new PaginationQuery(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().stream()
                        .map(order -> new SortOrder(
                                order.getProperty(),
                                order.isAscending()
                                        ? SortOrder.Direction.ASC
                                        : SortOrder.Direction.DESC
                        ))
                        .toList()
        );

        PageResult<Character> result = characterService.getAllCharacters(query);

        Page<CharacterResponse> responsePage = new PageImpl<>(
                result.content().stream()
                        .map(dtoMapper::toResponse)
                        .toList(),
                pageable,
                result.totalElements()
        );

        return new PagedModel<>(responsePage);
    }

    @Operation(
            summary = "Update a character",
            description = "Replaces the editable character fields with the values supplied in the request body."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Character updated successfully.",
                    content = @Content(schema = @Schema(implementation = CharacterResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request body is invalid or violates validation constraints.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "The supplied ID or request body is invalid.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No character exists with the supplied ID.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "The character was concurrently modified.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected server error occurred.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<CharacterResponse> update(
            @Parameter(description = "UUID of the character to update", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCharacterRequest request
    ) {
        Character updatedCharacter = dtoMapper.toDomain(request);
        Character character = characterService.updateCharacter(id, updatedCharacter);
        return ResponseEntity.ok(dtoMapper.toResponse(character));
    }

    @Operation(
            summary = "Delete a character",
            description = "Permanently deletes the character identified by the supplied UUID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Character deleted successfully."),
            @ApiResponse(responseCode = "400", description = "The supplied ID is not a valid UUID.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No character exists with the supplied ID.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected server error occurred.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID of the character to delete", required = true)
            @PathVariable UUID id
    ) {
        characterService.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }
}
