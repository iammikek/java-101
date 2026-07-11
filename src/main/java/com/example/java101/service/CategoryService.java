package com.example.java101.service;

import com.example.java101.domain.Category;
import com.example.java101.dto.CategoryCreateRequest;
import com.example.java101.dto.CategoryUpdateRequest;
import com.example.java101.exception.DomainExceptions;
import com.example.java101.repository.CategoryRepository;
import com.example.java101.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;

    public CategoryService(
            CategoryRepository categoryRepository,
            ItemRepository itemRepository,
            EntityManager entityManager) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public ListResult<Category> list(int skip, int limit) {
        long total = categoryRepository.count();
        List<Category> items =
                entityManager
                        .createQuery("select c from Category c order by c.id asc", Category.class)
                        .setFirstResult(skip)
                        .setMaxResults(limit)
                        .getResultList();
        return new ListResult<>(items, total);
    }

    @Transactional(readOnly = true)
    public Category getById(long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> DomainExceptions.categoryNotFound(categoryId));
    }

    @Transactional
    public Category create(CategoryCreateRequest request) {
        ensureUniqueName(request.name(), null);
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(long categoryId, CategoryUpdateRequest request) {
        Category category = getById(categoryId);
        if (request.name() != null) {
            ensureUniqueName(request.name(), categoryId);
            category.setName(request.name());
        }
        if (request.description() != null) {
            category.setDescription(request.description());
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(long categoryId) {
        Category category = getById(categoryId);
        if (itemRepository.countByCategoryId(categoryId) > 0) {
            throw DomainExceptions.categoryInUse(categoryId);
        }
        categoryRepository.delete(category);
    }

    private void ensureUniqueName(String name, Long categoryId) {
        boolean exists =
                categoryId == null
                        ? categoryRepository.existsByName(name)
                        : categoryRepository.existsByNameAndIdNot(name, categoryId);
        if (exists) {
            throw DomainExceptions.categoryNameExists(name);
        }
    }

    public record ListResult<T>(List<T> items, long total) {}
}
