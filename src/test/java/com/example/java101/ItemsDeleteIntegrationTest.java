package com.example.java101;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ItemsDeleteIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void deleteItemWithJwt() throws Exception {
        String auth = authHeader();
        var item = createItem(auth, Map.of("name", "Widget", "price", 9.99));
        long id = item.get("id").asLong();
        mockMvc.perform(delete("/items/" + id).header("Authorization", auth))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/items/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void deleteItemWithoutAuth() throws Exception {
        String auth = authHeader();
        var item = createItem(auth, Map.of("name", "Widget", "price", 9.99));
        mockMvc.perform(delete("/items/" + item.get("id").asLong()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Not authenticated"));
    }

    @Test
    void deleteItemInvalidJwt() throws Exception {
        String auth = authHeader();
        var item = createItem(auth, Map.of("name", "Widget", "price", 9.99));
        mockMvc.perform(
                        delete("/items/" + item.get("id").asLong())
                                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Could not validate credentials"));
    }

    @Test
    void deleteItemNotFound() throws Exception {
        String auth = authHeader();
        mockMvc.perform(delete("/items/99").header("Authorization", auth))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Item not found"));
    }
}
