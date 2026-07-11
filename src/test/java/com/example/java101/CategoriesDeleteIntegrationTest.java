package com.example.java101;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;

class CategoriesDeleteIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void deleteCategory() throws Exception {
        String auth = authHeader();
        var category = createCategory(auth, Map.of("name", "Temporary"));
        long id = category.get("id").asLong();
        mockMvc.perform(delete("/categories/" + id).header("Authorization", auth))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/categories/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void deleteCategoryInUse() throws Exception {
        String auth = authHeader();
        var category = createCategory(auth, Map.of("name", "Tools"));
        createItem(
                auth,
                Map.of(
                        "name",
                        "Hammer",
                        "price",
                        10.0,
                        "category_id",
                        category.get("id").asLong()));
        mockMvc.perform(
                        delete("/categories/" + category.get("id").asLong())
                                .header("Authorization", auth))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CATEGORY_IN_USE"));
    }
}
