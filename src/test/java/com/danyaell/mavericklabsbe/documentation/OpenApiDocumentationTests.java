package com.danyaell.mavericklabsbe.documentation;

import com.danyaell.mavericklabsbe.support.MySqlIntegrationTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MySqlIntegrationTest
@AutoConfigureMockMvc
class OpenApiDocumentationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldPublishTheThreePublicOperations() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.info.title")
                        .value("Maverick Labs API"))
                .andExpect(jsonPath(
                        "$.paths['/api/v1/games'].get"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/v1/games/{gameCode}'].get"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/v1/routes/analyze'].post"
                ).exists())
                .andExpect(jsonPath(
                        "$.components.schemas.AnalyzeRouteRequest"
                ).exists())
                .andExpect(jsonPath(
                        "$.components.schemas.RouteAnalysisResponse"
                ).exists())
                .andExpect(jsonPath(
                        "$.components.schemas.ErrorResponse"
                ).exists());
    }

    @Test
    void shouldDocumentAnalyzeRouteErrorResponses() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.paths['/api/v1/routes/analyze']" +
                                ".post.responses['400']"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/v1/routes/analyze']" +
                                ".post.responses['404']"
                ).exists())
                .andExpect(jsonPath(
                        "$.paths['/api/v1/routes/analyze']" +
                                ".post.responses['500']"
                ).exists());
    }

    @Test
    void shouldExposeSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}