
package kz.kbtu.course_project.controller;

import kz.kbtu.course_project.app.exception.CharacterNotFoundException;
import kz.kbtu.course_project.app.model.PageResult;
import kz.kbtu.course_project.app.model.PaginationQuery;
import kz.kbtu.course_project.app.service.CharacterService;
import kz.kbtu.course_project.domain.Character;
import kz.kbtu.course_project.infra.persistence.entity.CharacterEntity;
import kz.kbtu.course_project.web.controller.CharacterController;
import kz.kbtu.course_project.web.mapper.CharacterDtoMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CharacterController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CharacterDtoMapper.class)
class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CharacterService characterService;

    private Character createDomainCharacter(
            UUID id,
            String name,
            String role,
            int eddies,
            int will,
            int body,
            int empathy,
            int luck
    ) {
        Character character = new Character(
                id,
                name,
                role,
                eddies,
                5,  // intelligence
                5,  // reflex
                5,  // dexterity
                5,  // technology
                5,  // cool
                will,
                5,  // movement
                body,
                empathy,
                luck
        );

        character.initializeStats();

        return character;
    }

    /*
        TEST 1 - Create Character
    */
    @Test
    void create_shouldReturn201AndLocation() throws Exception {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Character character = createDomainCharacter(
                id, "V", "Solo", 2550, 5, 6, 4, 5
        );

        when(characterService.createCharacter(any(Character.class)))
                .thenReturn(character);

        mockMvc.perform(post("/api/v1/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "V",
                                    "role": "Solo",
                                    "intelligence": 5,
                                    "reflex": 5,
                                    "dexterity": 5,
                                    "technology": 5,
                                    "cool": 5,
                                    "will": 5,
                                    "movement": 5,
                                    "body": 5,
                                    "empathy": 5,
                                    "luck": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(id.toString())))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("V"))
                .andExpect(jsonPath("$.role").value("Solo"))
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$.eddies").value(2550))
                .andExpect(jsonPath("$.maxHealth").value(40))
                .andExpect(jsonPath("$.currentHealth").value(40));
    }

    /*
        TEST 2 - Get Character
    */
    @Test
    void getOne_shouldReturn200AndCharacter() throws Exception {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Character character = createDomainCharacter(
                id, "V", "Solo", 2550, 5, 6, 4, 5
        );

        when(characterService.getCharacter(id)).thenReturn(character);

        mockMvc.perform(get("/api/v1/characters/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("V"))
                .andExpect(jsonPath("$.role").value("Solo"))
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$.eddies").value(2550));
    }

    /*
        TEST 3 - Get All Characters
    */
    @Test
    void getAll_shouldReturn200AndPaginatedCharacters() throws Exception {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Character character = createDomainCharacter(
                id, "V", "Solo", 2550, 5, 6, 4, 5
        );

        PageResult<Character> characterPage =
                new PageResult<>(List.of(character), 0, 20, 1, 1);

        when(characterService.getAllCharacters(any(PaginationQuery.class)))
                .thenReturn(characterPage);

        mockMvc.perform(get("/api/v1/characters")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].name").value("V"))
                .andExpect(jsonPath("$.content[0].role").value("Solo"))
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1));
    }

    /*
        TEST 4 - Update Character
    */
    @Test
    void update_shouldReturn200AndUpdatedCharacter() throws Exception {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Character character = createDomainCharacter(
                id, "V Updated", "Netrunner", 3000, 8, 7, 6, 5
        );

        when(characterService.updateCharacter(
                eq(id),
                any(Character.class)
        )).thenReturn(character);

        mockMvc.perform(put("/api/v1/characters/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "V Updated",
                                    "role": "Netrunner",
                                    "eddies": 3000,
                                    "intelligence": 5,
                                    "reflex": 5,
                                    "dexterity": 5,
                                    "technology": 5,
                                    "cool": 5,
                                    "will": 5,
                                    "movement": 5,
                                    "body": 7,
                                    "empathy": 6,
                                    "luck": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("V Updated"))
                .andExpect(jsonPath("$.role").value("Netrunner"))
                .andExpect(jsonPath("$.eddies").value(3000))
                .andExpect(jsonPath("$.maxHealth").value(50))
                .andExpect(jsonPath("$.maxHumanity").value(60));
    }

    /*
        TEST 5 - Delete Character
    */
    @Test
    void delete_shouldReturn204NoContent() throws Exception {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(delete("/api/v1/characters/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(characterService).deleteCharacter(id);
    }

    /*
        TEST 6 - Create Character with Invalid Data
    */
    @Test
    void create_withInvalidData_shouldReturn400() throws Exception {

        mockMvc.perform(post("/api/v1/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "role": "Solo",
                                    "intelligence": 5,
                                    "reflex": 5,
                                    "dexterity": 5,
                                    "technology": 5,
                                    "cool": 5,
                                    "will": 5,
                                    "movement": 5,
                                    "body": 5,
                                    "empathy": 5,
                                    "luck": 5
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.invalid_fields.name").exists());

        verify(characterService, never())
                .createCharacter(any(Character.class));
    }

    /*
        TEST 7 - Get Character that Does Not Exist
    */
    @Test
    void getOne_whenCharacterDoesNotExist_shouldReturn404() throws Exception {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(characterService.getCharacter(id))
                .thenThrow(new CharacterNotFoundException(id));

        mockMvc.perform(get("/api/v1/characters/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Character Not Found"))
                .andExpect(jsonPath("$.detail")
                        .value("Character with ID " + id + " was not found."))
                .andExpect(jsonPath("$.instance")
                        .value("/api/v1/characters/" + id))
                .andExpect(jsonPath("$.invalid_fields").isEmpty());

        verify(characterService).getCharacter(id);
    }

    /*
        TEST 8 - Update Character with Optimistic Locking Conflict
    */
    @Test
    void update_whenOptimisticLockingConflictOccurs_shouldReturn409() throws Exception {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(characterService.updateCharacter(
                eq(id),
                any(Character.class)
        )).thenThrow(
                new ObjectOptimisticLockingFailureException(CharacterEntity.class, id)
        );

        mockMvc.perform(put("/api/v1/characters/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "V Updated",
                                    "role": "Netrunner",
                                    "eddies": 3000,
                                    "intelligence": 5,
                                    "reflex": 5,
                                    "dexterity": 5,
                                    "technology": 5,
                                    "cool": 5,
                                    "will": 5,
                                    "movement": 5,
                                    "body": 7,
                                    "empathy": 6,
                                    "luck": 5
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Resource Conflict"))
                .andExpect(jsonPath("$.detail").value(
                        "The character was modified by another request. Please retrieve the latest version and try again."
                ))
                .andExpect(jsonPath("$.instance")
                        .value("/api/v1/characters/" + id))
                .andExpect(jsonPath("$.invalid_fields").isEmpty());
    }

    /*
        TEST 9 - Update Character that Does Not Exist
    */
    @Test
    void update_whenCharacterDoesNotExist_shouldReturn404() throws Exception {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(characterService.updateCharacter(
                eq(id),
                any(Character.class)
        )).thenThrow(new CharacterNotFoundException(id));

        mockMvc.perform(put("/api/v1/characters/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "V Updated",
                                    "role": "Netrunner",
                                    "eddies": 3000,
                                    "intelligence": 5,
                                    "reflex": 5,
                                    "dexterity": 5,
                                    "technology": 5,
                                    "cool": 5,
                                    "will": 5,
                                    "movement": 5,
                                    "body": 7,
                                    "empathy": 6,
                                    "luck": 5
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Character Not Found"))
                .andExpect(jsonPath("$.detail")
                        .value("Character with ID " + id + " was not found."));
    }

    /*
        TEST 10 - Delete Character that Does Not Exist
    */
    @Test
    void delete_whenCharacterDoesNotExist_shouldReturn404() throws Exception {

        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        doThrow(new CharacterNotFoundException(id))
                .when(characterService)
                .deleteCharacter(id);

        mockMvc.perform(delete("/api/v1/characters/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Character Not Found"))
                .andExpect(jsonPath("$.detail")
                        .value("Character with ID " + id + " was not found."))
                .andExpect(jsonPath("$.instance")
                        .value("/api/v1/characters/" + id));
    }

    /*
        TEST 11 - Create Character with Malformed JSON
    */

    @Test
    void create_withMalformedJson_shouldReturn400() throws Exception {

        mockMvc.perform(post("/api/v1/characters")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid Request Body"))
                .andExpect(jsonPath("$.detail")
                        .value("The request body is missing or contains invalid JSON."))
                .andExpect(jsonPath("$.invalid_fields").isEmpty());

        verify(characterService, never()).createCharacter(any(Character.class));
    }

    /*
        TEST 11 - Get Character and receive unexpected exception
    */

    @Test
    void getOne_whenUnexpectedExceptionOccurs_shouldReturn500() throws Exception {
        UUID id = UUID.randomUUID();

        when(characterService.getCharacter(id))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/v1/characters/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Unexpected Error"))
                .andExpect(jsonPath("$.detail")
                        .value("An unexpected error occurred."))
                .andExpect(jsonPath("$.instance")
                        .value("/api/v1/characters/" + id))
                .andExpect(jsonPath("$.invalid_fields").isEmpty());

        verify(characterService).getCharacter(id);
    }

    /*
        TEST 12 - Get Character with wrong param type
    */

    @Test
    void getOne_withInvalidUuid_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/characters/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid Parameter"))
                .andExpect(jsonPath("$.detail")
                        .value("Parameter 'id' has an invalid value."))
                .andExpect(jsonPath("$.instance")
                        .value("/api/v1/characters/not-a-uuid"))
                .andExpect(jsonPath("$.invalid_fields").isEmpty());

        verifyNoInteractions(characterService);
    }

    
}
