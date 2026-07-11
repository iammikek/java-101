package com.example.java101.web;

import com.example.java101.dto.ApiMapper;
import com.example.java101.dto.CategoryCreateRequest;
import com.example.java101.dto.CategoryResponse;
import com.example.java101.dto.CategoryUpdateRequest;
import com.example.java101.dto.PaginatedResponse;
import com.example.java101.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public PaginatedResponse<CategoryResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int skip,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        var result = categoryService.list(skip, limit);
        return new PaginatedResponse<>(
                result.items().stream().map(ApiMapper::toCategory).toList(),
                result.total(),
                skip,
                limit);
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse get(@PathVariable long categoryId) {
        return ApiMapper.toCategory(categoryService.getById(categoryId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryCreateRequest request) {
        return ApiMapper.toCategory(categoryService.create(request));
    }

    @PatchMapping("/{categoryId}")
    public CategoryResponse update(
            @PathVariable long categoryId, @Valid @RequestBody CategoryUpdateRequest request) {
        return ApiMapper.toCategory(categoryService.update(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long categoryId) {
        categoryService.delete(categoryId);
    }
}
