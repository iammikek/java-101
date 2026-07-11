package com.example.java101.web;

import com.example.java101.dto.HealthResponse;
import com.example.java101.dto.MessageResponse;
import com.example.java101.exception.ApiException;
import com.example.java101.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final ItemService itemService;

    public HealthController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    public MessageResponse root() {
        return new MessageResponse("Hello from java-101");
    }

    @GetMapping("/health")
    public HealthResponse health() {
        try {
            itemService.checkDatabase();
            return new HealthResponse("ok", "connected");
        } catch (Exception ex) {
            throw new ApiException("database unavailable", 503, "DB_UNAVAILABLE");
        }
    }
}
