package com.example.java101;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ItemsReadIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void getItem() throws Exception {
        String auth = authHeader();
        var item = createItem(auth, Map.of("name", "Widget", "price", 9.99));
        mockMvc.perform(get("/items/" + item.get("id").asLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Widget"));
    }

    @Test
    void getItemNotFound() throws Exception {
        mockMvc.perform(get("/items/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Item not found"));
    }
}
