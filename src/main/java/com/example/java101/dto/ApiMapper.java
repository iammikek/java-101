package com.example.java101.dto;

import com.example.java101.domain.Category;
import com.example.java101.domain.Item;
import com.example.java101.domain.User;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ApiMapper {

    private ApiMapper() {}

    public static UserResponse toUser(User user) {
        return new UserResponse(user.getId(), user.getEmail());
    }

    public static CategoryResponse toCategory(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    public static ItemResponse toItem(Item item) {
        CategoryResponse category =
                item.getCategory() == null ? null : toCategory(item.getCategory());
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                scale(item.getPrice()),
                item.getCategoryId(),
                category);
    }

    public static BigDecimal scale(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundAverage(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
