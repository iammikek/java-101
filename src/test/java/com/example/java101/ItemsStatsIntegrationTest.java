package com.example.java101;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ItemsStatsIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void getItemsStatsEmpty() throws Exception {
        mockMvc.perform(get("/items/stats/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_items").value(0))
                .andExpect(jsonPath("$.average_price").value(0.0))
                .andExpect(jsonPath("$.min_price").isEmpty())
                .andExpect(jsonPath("$.max_price").isEmpty())
                .andExpect(jsonPath("$.uncategorized_count").value(0))
                .andExpect(jsonPath("$.by_category").isEmpty());
    }

    @Test
    void getItemsStats() throws Exception {
        String auth = authHeader();
        createItem(auth, Map.of("name", "A", "price", 10.0));
        createItem(auth, Map.of("name", "B", "price", 20.0));
        createItem(auth, Map.of("name", "C", "price", 30.0));
        mockMvc.perform(get("/items/stats/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_items").value(3))
                .andExpect(jsonPath("$.average_price").value(20.0))
                .andExpect(jsonPath("$.min_price").value(10.0))
                .andExpect(jsonPath("$.max_price").value(30.0))
                .andExpect(jsonPath("$.uncategorized_count").value(3))
                .andExpect(jsonPath("$.by_category").isEmpty());
    }

    @Test
    void getItemsStatsByCategory() throws Exception {
        String auth = authHeader();
        var tools = createCategory(auth, Map.of("name", "Tools"));
        var books = createCategory(auth, Map.of("name", "Books"));
        createItem(
                auth,
                Map.of(
                        "name",
                        "Hammer",
                        "price",
                        10.0,
                        "category_id",
                        tools.get("id").asLong()));
        createItem(
                auth,
                Map.of(
                        "name",
                        "Drill",
                        "price",
                        30.0,
                        "category_id",
                        tools.get("id").asLong()));
        createItem(
                auth,
                Map.of(
                        "name",
                        "Novel",
                        "price",
                        15.0,
                        "category_id",
                        books.get("id").asLong()));
        createItem(auth, Map.of("name", "Loose", "price", 5.0));

        mockMvc.perform(get("/items/stats/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_items").value(4))
                .andExpect(jsonPath("$.uncategorized_count").value(1))
                .andExpect(jsonPath("$.by_category.length()").value(2))
                .andExpect(jsonPath("$.by_category[0].category_id").value(books.get("id").asLong()))
                .andExpect(jsonPath("$.by_category[0].category_name").value("Books"))
                .andExpect(jsonPath("$.by_category[0].item_count").value(1))
                .andExpect(jsonPath("$.by_category[0].average_price").value(15.0))
                .andExpect(jsonPath("$.by_category[1].category_id").value(tools.get("id").asLong()))
                .andExpect(jsonPath("$.by_category[1].category_name").value("Tools"))
                .andExpect(jsonPath("$.by_category[1].item_count").value(2))
                .andExpect(jsonPath("$.by_category[1].average_price").value(20.0));
    }
}
