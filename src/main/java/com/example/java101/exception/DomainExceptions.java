package com.example.java101.exception;

public final class DomainExceptions {

    private DomainExceptions() {}

    public static ApiException itemNotFound(long itemId) {
        return new ApiException("Item not found", 404, "ITEM_NOT_FOUND");
    }

    public static ApiException categoryNotFound(long categoryId) {
        return new ApiException("Category not found", 404, "CATEGORY_NOT_FOUND");
    }

    public static ApiException categoryInUse(long categoryId) {
        return new ApiException(
                "Category has items and cannot be deleted", 409, "CATEGORY_IN_USE");
    }

    public static ApiException categoryNameExists(String name) {
        return new ApiException(
                "Category name '" + name + "' already exists", 409, "CATEGORY_NAME_EXISTS");
    }

    public static ApiException userEmailExists(String email) {
        return new ApiException(
                "User email '" + email + "' already exists", 409, "USER_EMAIL_EXISTS");
    }

    public static ApiException incorrectCredentials() {
        return new ApiException("Incorrect email or password", 401, null);
    }

    public static ApiException notAuthenticated() {
        return new ApiException("Not authenticated", 401, null);
    }

    public static ApiException invalidCredentials() {
        return new ApiException("Could not validate credentials", 401, null);
    }
}
