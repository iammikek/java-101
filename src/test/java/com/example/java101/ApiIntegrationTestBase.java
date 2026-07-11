package com.example.java101;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.java101.repository.CategoryRepository;
import com.example.java101.repository.ItemRepository;
import com.example.java101.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class ApiIntegrationTestBase {

    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;

    @Autowired private ItemRepository itemRepository;

    @Autowired private CategoryRepository categoryRepository;

    @Autowired private UserRepository userRepository;

    @BeforeEach
    void resetDatabase() {
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected JsonNode registerUser(String email, String password) throws Exception {
        MvcResult result =
                mockMvc.perform(
                                post("/auth/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        Map.of("email", email, "password", password))))
                        .andExpect(status().isCreated())
                        .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    protected String loginToken(String email, String password) throws Exception {
        MvcResult result =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                        .param("username", email)
                                        .param("password", password))
                        .andExpect(status().isOk())
                        .andReturn();
        return objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get("access_token")
                .asText();
    }

    protected String authHeader() throws Exception {
        registerUser("test@example.com", "secret123");
        return "Bearer " + loginToken("test@example.com", "secret123");
    }

    protected JsonNode createCategory(String authHeader, Map<String, Object> payload)
            throws Exception {
        MvcResult result =
                mockMvc.perform(
                                post("/categories")
                                        .header("Authorization", authHeader)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(payload)))
                        .andExpect(status().isCreated())
                        .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    protected JsonNode createItem(String authHeader, Map<String, Object> payload) throws Exception {
        MvcResult result =
                mockMvc.perform(
                                post("/items")
                                        .header("Authorization", authHeader)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(payload)))
                        .andExpect(status().isCreated())
                        .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
