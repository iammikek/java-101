package com.example.java101;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;

class CategoriesListIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void listCategoriesEmpty() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.skip").value(0))
                .andExpect(jsonPath("$.limit").value(10));
    }

    @Test
    void listCategoriesWithPagination() throws Exception {
        String auth = authHeader();
        for (String name : new String[] {"A", "B", "C"}) {
            createCategory(auth, Map.of("name", name));
        }
        mockMvc.perform(get("/categories").param("skip", "1").param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.skip").value(1))
                .andExpect(jsonPath("$.limit").value(2))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].name").value("B"))
                .andExpect(jsonPath("$.items[1].name").value("C"));
    }

    @Test
    void getCategory() throws Exception {
        String auth = authHeader();
        var category = createCategory(auth, Map.of("name", "Books"));
        mockMvc.perform(get("/categories/" + category.get("id").asLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Books"));
    }

    @Test
    void getCategoryNotFound() throws Exception {
        mockMvc.perform(get("/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }
}
