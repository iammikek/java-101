package com.example.java101;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ItemsUpdateIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void updateItemCategory() throws Exception {
        String auth = authHeader();
        var item = createItem(auth, Map.of("name", "Widget", "price", 9.99));
        var category = createCategory(auth, Map.of("name", "Tools"));
        mockMvc.perform(
                        patch("/items/" + item.get("id").asLong())
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of("category_id", category.get("id").asLong()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category_id").value(category.get("id").asLong()))
                .andExpect(jsonPath("$.category.name").value("Tools"));
    }

    @Test
    void updateItemPartial() throws Exception {
        String auth = authHeader();
        var item =
                createItem(
                        auth,
                        Map.of("name", "Widget", "description", "Original", "price", 10.0));
        mockMvc.perform(
                        patch("/items/" + item.get("id").asLong())
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("price", 5.99))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Widget"))
                .andExpect(jsonPath("$.description").value("Original"))
                .andExpect(jsonPath("$.price").value(5.99));
    }

    @Test
    void updateItemFull() throws Exception {
        String auth = authHeader();
        var item = createItem(auth, Map.of("name", "Old", "price", 1.0));
        mockMvc.perform(
                        patch("/items/" + item.get("id").asLong())
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                Map.of(
                                                        "name",
                                                        "New",
                                                        "description",
                                                        "Updated",
                                                        "price",
                                                        2.5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.get("id").asLong()))
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.description").value("Updated"))
                .andExpect(jsonPath("$.price").value(2.5))
                .andExpect(jsonPath("$.category_id", nullValue()));
    }

    @Test
    void updateItemNotFound() throws Exception {
        String auth = authHeader();
        mockMvc.perform(
                        patch("/items/99")
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("name", "Nope"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Item not found"));
    }

    @Test
    void updateItemWithoutAuth() throws Exception {
        String auth = authHeader();
        var item = createItem(auth, Map.of("name", "Widget", "price", 9.99));
        mockMvc.perform(
                        patch("/items/" + item.get("id").asLong())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("name", "Nope"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Not authenticated"));
    }
}
