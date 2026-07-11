package com.example.java101;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CategoriesCreateIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void createCategory() throws Exception {
        String auth = authHeader();
        mockMvc.perform(
                        post("/categories")
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "name",
                                                        "Tools",
                                                        "description",
                                                        "Hand tools"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.name").value("Tools"))
                .andExpect(jsonPath("$.description").value("Hand tools"));
    }

    @Test
    void createCategoryWithoutAuth() throws Exception {
        mockMvc.perform(
                        post("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("name", "Tools"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCategoryDuplicateName() throws Exception {
        String auth = authHeader();
        createCategory(auth, Map.of("name", "foo"));
        mockMvc.perform(
                        post("/categories")
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("name", "foo", "description", "duplicate"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CATEGORY_NAME_EXISTS"));
    }
}
