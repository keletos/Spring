package kz.kbtu.course_project.controller;

import kz.kbtu.course_project.app.model.PageResult;
import kz.kbtu.course_project.app.model.PaginationQuery;
import kz.kbtu.course_project.app.service.CharacterService;
import kz.kbtu.course_project.security.config.SecurityConfig;
import kz.kbtu.course_project.web.controller.CharacterController;
import kz.kbtu.course_project.web.mapper.CharacterDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharacterController.class)
@Import({SecurityConfig.class, CharacterDtoMapper.class})
class CharacterSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CharacterService characterService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void publicCharacterRead_allowsAnonymousRequest() throws Exception {
        when(characterService.getAllCharacters(any(PaginationQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 20, 0, 0));

        mvc.perform(get("/api/v1/characters"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_withoutToken_returns401() throws Exception {
        mvc.perform(delete("/api/v1/characters/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_asStudent_returns403() throws Exception {
        mvc.perform(delete("/api/v1/characters/{id}", UUID.randomUUID())
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_STUDENT"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_asAdmin_returns204() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(delete("/api/v1/characters/{id}", id)
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());

        verify(characterService).deleteCharacter(id);
    }
}
