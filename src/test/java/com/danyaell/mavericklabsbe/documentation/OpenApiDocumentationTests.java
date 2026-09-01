package com.danyaell.mavericklabsbe.documentation;

import com.danyaell.mavericklabsbe.support.MySqlIntegrationTest;
import com.danyaell.mavericklabsbe.config.openapi.OpenApiExamples;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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

    private final MockMvc mockMvc;

    @Autowired
    OpenApiDocumentationTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

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
                ).exists())
                .andExpect(jsonPath("$.openapi").value("3.1.0"))
                .andExpect(jsonPath("$.externalDocs.url")
                        .value("https://github.com/Danyaell/maverick-labs-be"))
                .andExpect(jsonPath(
                        "$.paths['/api/v1/games'].get.operationId"
                ).value("listGames"))
                .andExpect(jsonPath(
                        "$.paths['/api/v1/games/{gameCode}'].get.operationId"
                ).value("getGameDetail"))
                .andExpect(jsonPath(
                        "$.paths['/api/v1/routes/analyze'].post.operationId"
                ).value("analyzeRoute"));
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

    @Test
    void shouldKeepPublishedExampleAlignedWithAnalyzer() throws Exception {
        mockMvc.perform(post("/api/v1/routes/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OpenApiExamples.ANALYZE_ROUTE_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        OpenApiExamples.ANALYZE_ROUTE_RESPONSE
                ));
    }
}
