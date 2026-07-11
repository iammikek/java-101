package com.example.java101.web;

import com.example.java101.dto.ApiMapper;
import com.example.java101.dto.ItemCreateRequest;
import com.example.java101.dto.ItemResponse;
import com.example.java101.dto.ItemStatsResponse;
import com.example.java101.dto.ItemUpdateRequest;
import com.example.java101.dto.PaginatedResponse;
import com.example.java101.service.ItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public PaginatedResponse<ItemResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int skip,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit,
            @RequestParam(name = "min_price", required = false) @DecimalMin(value = "0.01")
                    BigDecimal minPrice,
            @RequestParam(name = "max_price", required = false) @DecimalMin(value = "0.01")
                    BigDecimal maxPrice,
            @RequestParam(name = "category_id", required = false) @Min(1) Long categoryId,
            @RequestParam(name = "name_contains", required = false) String nameContains) {
        var result = itemService.list(skip, limit, minPrice, maxPrice, categoryId, nameContains);
        return new PaginatedResponse<>(
                result.items().stream().map(ApiMapper::toItem).toList(),
                result.total(),
                skip,
                limit);
    }

    @GetMapping("/stats/summary")
    public ItemStatsResponse stats() {
        return itemService.getStats();
    }

    @GetMapping("/{itemId}")
    public ItemResponse get(@PathVariable long itemId) {
        return ApiMapper.toItem(itemService.getById(itemId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(@Valid @RequestBody ItemCreateRequest request) {
        return ApiMapper.toItem(itemService.create(request));
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(
            @PathVariable long itemId, @Valid @RequestBody ItemUpdateRequest request) {
        return ApiMapper.toItem(itemService.update(itemId, request));
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long itemId) {
        itemService.delete(itemId);
    }
}
