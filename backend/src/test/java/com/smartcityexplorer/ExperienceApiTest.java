package com.smartcityexplorer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExperienceApiTest {
    @Autowired MockMvc mvc;

    @Test void seededCatalogIsAvailable() throws Exception {
        mvc.perform(get("/api/experiences")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(12)))
                .andExpect(jsonPath("$[0].name").value("Art Institute Afternoon"));
    }
    @Test void combinedFiltersReturnOnlyMatchingExperience() throws Exception {
        mvc.perform(get("/api/experiences").param("mood", "Study & Work").param("maxBudget", "0").param("maxDuration", "60"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Harold Washington Library"));
    }
    @Test void searchIsCaseInsensitiveAndSearchesNeighborhood() throws Exception {
        mvc.perform(get("/api/experiences").param("q", "PILSEN"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Pilsen Mural Walk"));
    }
    @Test void wildcardIsLiteral() throws Exception {
        mvc.perform(get("/api/experiences").param("q", "%")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }
    @Test void categoryAndNeighborhoodFiltersCompose() throws Exception {
        mvc.perform(get("/api/experiences").param("category", "Culture").param("neighborhood", "Loop"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
    }
    @Test void sortsByBudget() throws Exception {
        mvc.perform(get("/api/experiences").param("sort", "budget"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].budgetLevel").value(0));
    }
    @Test void returnsDetails() throws Exception {
        mvc.perform(get("/api/experiences/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Chicago Riverwalk"));
    }
    @Test void missingExperienceReturns404Problem() throws Exception {
        mvc.perform(get("/api/experiences/99999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }
    @Test void invalidBudgetReturns400() throws Exception {
        mvc.perform(get("/api/experiences").param("maxBudget", "-1"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }
    @Test void invalidDurationAndSortReturn400() throws Exception {
        mvc.perform(get("/api/experiences").param("maxDuration", "0")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/experiences").param("sort", "password")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/experiences").param("maxBudget", "abc")).andExpect(status().isBadRequest());
    }
    @Test void onlyConfiguredOriginReceivesCorsPermission() throws Exception {
        mvc.perform(get("/api/experiences").header("Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        mvc.perform(get("/api/experiences").header("Origin", "https://untrusted.example"))
                .andExpect(status().isForbidden()).andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
    @Test void healthEndpointWorks() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }
}
