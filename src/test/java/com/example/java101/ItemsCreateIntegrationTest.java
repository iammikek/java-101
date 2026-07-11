package com.example.java101;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ItemsCreateIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void createItem() throws Exception {
        String auth = authHeader();
        mockMvc.perform(
                        post("/items")
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "name",
                                                        "Widget",
                                                        "description",
                                                        "A nice widget",
                                                        "price",
                                                        9.99))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.name").value("Widget"))
                .andExpect(jsonPath("$.description").value("A nice widget"))
                .andExpect(jsonPath("$.price").value(9.99));
    }

    @Test
    void createItemOptionalDescription() throws Exception {
        String auth = authHeader();
        mockMvc.perform(
                        post("/items")
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("name", "Thing", "price", 5.0))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", nullValue()));
    }

    @Test
    void createItemWithCategory() throws Exception {
        String auth = authHeader();
        var category = createCategory(auth, Map.of("name", "Electronics"));
        mockMvc.perform(
                        post("/items")
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "name",
                                                        "Gadget",
                                                        "price",
                                                        15.0,
                                                        "category_id",
                                                        category.get("id").asLong()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category_id").value(category.get("id").asLong()))
                .andExpect(jsonPath("$.category.name").value("Electronics"));
    }

    @Test
    void createItemWithInvalidCategoryId() throws Exception {
        String auth = authHeader();
        mockMvc.perform(
                        post("/items")
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "name",
                                                        "Gadget",
                                                        "price",
                                                        15.0,
                                                        "category_id",
                                                        999))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }

    @Test
    void createItemWithoutAuth() throws Exception {
        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("name", "Thing", "price", 5.0))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Not authenticated"));
    }
}
