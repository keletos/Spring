package kz.kbtu.course_project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiDocument_shouldExposeCharacterEndpointsAndMetadata() throws Exception {
        mockMvc.perform(get("/v3/api-docs/characters-v1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.info.title").value("Cyberpunk RED Character API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.paths['/api/v1/characters']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/characters'].post.summary")
                        .value("Create a character"))
                .andExpect(jsonPath("$.paths['/api/v1/characters/{id}'].get.responses['404']")
                        .exists());
    }

    @Test
    void completeApiGroup_shouldIncludeEveryApplicationEndpoint() throws Exception {
        mockMvc.perform(get("/v3/api-docs/api-v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/characters']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/compendium'].get").exists())
                .andExpect(jsonPath("$.paths['/profile'].get").exists());
    }

    @Test
    void swaggerUi_shouldBeAvailable() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
