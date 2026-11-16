package kz.kbtu.course_project.controller;

import kz.kbtu.course_project.dto.CharacterResponse;
import kz.kbtu.course_project.dto.CreateCharacterRequest;
import kz.kbtu.course_project.dto.UpdateCharacterRequest;
import kz.kbtu.course_project.service.CharacterService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.mockito.Mockito.never;

import kz.kbtu.course_project.exception.CharacterNotFoundException;

import kz.kbtu.course_project.entity.CharacterEntity;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.mockito.ArgumentMatchers.eq;


@WebMvcTest(CharacterController.class)
class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CharacterService characterService;

    /*
        TEST 1 - Create Character
    */
    @Test
    void create_shouldReturn201AndLocation() throws Exception {

        // Arrange: prepare the expected response
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        CharacterResponse response = new CharacterResponse(
                id,
                "V",
                "Solo",
                1,
                2550,
                40,
                40,
                40,
                40,
                5,      
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5
        );

        when(characterService.createCharacter(any(CreateCharacterRequest.class)))
                .thenReturn(response);

        // Act: send an HTTP POST request
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

                // Assert: check the HTTP response
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

        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        CharacterResponse response = new CharacterResponse(
                id,
                "V",
                "Solo",
                1,
                2550,
                40,
                40,
                40,
                40,
                5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5
        );

        when(characterService.getCharacter(id)).thenReturn(response);

        // Act and Assert
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

        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        CharacterResponse response = new CharacterResponse(
                id,
                "V",
                "Solo",
                1,
                2550,
                40,
                40,
                40,
                40,
                5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5
        );

        Pageable pageable = PageRequest.of(0, 20);

        Page<CharacterResponse> characterPage =
                new PageImpl<>(List.of(response), pageable, 1);

        when(characterService.getAllCharacters(any(Pageable.class)))
                .thenReturn(characterPage);

        // Act and Assert
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

        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        CharacterResponse response = new CharacterResponse(
                id,
                "V Updated",
                "Netrunner",
                1,
                3000,
                50,
                60,
                40,
                40,
                5,
                5, 5, 5, 5, 5, 5, 5, 7, 6, 5
        );

        when(characterService.updateCharacter(
                org.mockito.ArgumentMatchers.eq(id),
                any(UpdateCharacterRequest.class)
        )).thenReturn(response);

        // Act and Assert
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

        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Act and Assert
        mockMvc.perform(delete("/api/v1/characters/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // Verify that the controller called the service
        verify(characterService).deleteCharacter(id);
    }


    /*
        Pessimistic Tests
    */
    
    /*
        TEST 6 - Create Character with Invalid Data
    */
    @Test
    void create_withInvalidData_shouldReturn400() throws Exception {

        // Act and Assert
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

        // Validation should stop the request before it reaches the service
        verify(characterService, never())
                .createCharacter(any(CreateCharacterRequest.class));
    }

    /*
        TEST 7 - Get Character that Does Not Exist
    */
    @Test
    void getOne_whenCharacterDoesNotExist_shouldReturn404() throws Exception {

        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(characterService.getCharacter(id))
                .thenThrow(new CharacterNotFoundException(id));

        // Act and Assert
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

        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(characterService.updateCharacter(
                eq(id),
                any(UpdateCharacterRequest.class)
        )).thenThrow(
                new ObjectOptimisticLockingFailureException(CharacterEntity.class, id)
        );

        // Act and Assert
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

        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(characterService.updateCharacter(
                eq(id),
                any(UpdateCharacterRequest.class)
        )).thenThrow(new CharacterNotFoundException(id));

        // Act and Assert
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

        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        org.mockito.Mockito.doThrow(new CharacterNotFoundException(id))
                .when(characterService)
                .deleteCharacter(id);

        // Act and Assert
        mockMvc.perform(delete("/api/v1/characters/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Character Not Found"))
                .andExpect(jsonPath("$.detail")
                        .value("Character with ID " + id + " was not found."))
                .andExpect(jsonPath("$.instance")
                        .value("/api/v1/characters/" + id));
    }
}
