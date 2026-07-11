package com.example.java101;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CategoriesUpdateIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void updateCategory() throws Exception {
        String auth = authHeader();
        var category = createCategory(auth, Map.of("name", "Old Name"));
        mockMvc.perform(
                        patch("/categories/" + category.get("id").asLong())
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "name",
                                                        "New Name",
                                                        "description",
                                                        "Updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.description").value("Updated"));
    }
}
