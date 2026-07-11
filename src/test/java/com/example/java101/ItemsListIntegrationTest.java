package com.example.java101;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ItemsListIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void listItemsEmpty() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.skip").value(0))
                .andExpect(jsonPath("$.limit").value(10));
    }

    @Test
    void listItemsWithPagination() throws Exception {
        String auth = authHeader();
        createItem(auth, Map.of("name", "A", "price", 1.0));
        createItem(auth, Map.of("name", "B", "price", 2.0));
        createItem(auth, Map.of("name", "C", "price", 3.0));
        mockMvc.perform(get("/items").param("skip", "1").param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.skip").value(1))
                .andExpect(jsonPath("$.limit").value(2))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].name").value("B"))
                .andExpect(jsonPath("$.items[1].name").value("C"));
    }

    @Test
    void listItemsFilterByMinPrice() throws Exception {
        String auth = authHeader();
        createItem(auth, Map.of("name", "Cheap", "price", 5.0));
        createItem(auth, Map.of("name", "Mid", "price", 10.0));
        createItem(auth, Map.of("name", "Premium", "price", 25.0));
        mockMvc.perform(get("/items").param("min_price", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.items[*].name", containsInAnyOrder("Mid", "Premium")));
    }

    @Test
    void listItemsFilterByMaxPrice() throws Exception {
        String auth = authHeader();
        createItem(auth, Map.of("name", "Cheap", "price", 5.0));
        createItem(auth, Map.of("name", "Mid", "price", 10.0));
        createItem(auth, Map.of("name", "Premium", "price", 25.0));
        mockMvc.perform(get("/items").param("max_price", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.items[*].name", containsInAnyOrder("Cheap", "Mid")));
    }

    @Test
    void listItemsFilterByCategory() throws Exception {
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
                        "Novel",
                        "price",
                        12.0,
                        "category_id",
                        books.get("id").asLong()));
        createItem(
                auth,
                Map.of(
                        "name",
                        "Wrench",
                        "price",
                        15.0,
                        "category_id",
                        tools.get("id").asLong()));
        mockMvc.perform(get("/items").param("category_id", tools.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.items[*].name", containsInAnyOrder("Hammer", "Wrench")));
    }

    @Test
    void listItemsFilterByNameContains() throws Exception {
        String auth = authHeader();
        createItem(auth, Map.of("name", "Blue Widget", "price", 10.0));
        createItem(auth, Map.of("name", "Red Gadget", "price", 12.0));
        createItem(auth, Map.of("name", "green widget", "price", 15.0));
        mockMvc.perform(get("/items").param("name_contains", "widget"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(
                        jsonPath(
                                "$.items[*].name",
                                containsInAnyOrder("Blue Widget", "green widget")));
    }

    @Test
    void listItemsCombinedFilters() throws Exception {
        String auth = authHeader();
        var tools = createCategory(auth, Map.of("name", "Tools"));
        var books = createCategory(auth, Map.of("name", "Books"));
        createItem(
                auth,
                Map.of(
                        "name",
                        "Budget Tool",
                        "price",
                        8.0,
                        "category_id",
                        tools.get("id").asLong()));
        createItem(
                auth,
                Map.of(
                        "name",
                        "Pro Tool",
                        "price",
                        20.0,
                        "category_id",
                        tools.get("id").asLong()));
        createItem(
                auth,
                Map.of(
                        "name",
                        "Budget Book",
                        "price",
                        8.0,
                        "category_id",
                        books.get("id").asLong()));
        mockMvc.perform(
                        get("/items")
                                .param("category_id", tools.get("id").asText())
                                .param("min_price", "10")
                                .param("max_price", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Pro Tool"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"limit=101", "skip=-1", "min_price=-1"})
    void listItemsValidationErrors(String query) throws Exception {
        mockMvc.perform(get("/items?" + query)).andExpect(status().isUnprocessableEntity());
    }
}
