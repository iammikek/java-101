package com.example.java101;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

class ItemsValidationIntegrationTest extends ApiIntegrationTestBase {

    static Object[][] invalidPayloads() {
        ObjectNode noPrice = new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode();
        noPrice.put("name", "No price");

        ObjectNode badPrice = new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode();
        badPrice.put("name", "Bad");
        badPrice.put("price", -1.0);

        ObjectNode emptyName = new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode();
        emptyName.put("name", "");
        emptyName.put("price", 1.0);

        return new Object[][] {{noPrice}, {badPrice}, {emptyName}};
    }

    @ParameterizedTest
    @MethodSource("invalidPayloads")
    void createItemValidationErrors(ObjectNode payload) throws Exception {
        String auth = authHeader();
        mockMvc.perform(
                        post("/items")
                                .header("Authorization", auth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnprocessableEntity());
    }
}
