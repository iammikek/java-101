package com.example.java101.service;

import com.example.java101.domain.Item;
import com.example.java101.dto.ApiMapper;
import com.example.java101.dto.CategoryItemStats;
import com.example.java101.dto.ItemCreateRequest;
import com.example.java101.dto.ItemStatsResponse;
import com.example.java101.dto.ItemUpdateRequest;
import com.example.java101.exception.DomainExceptions;
import com.example.java101.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final EntityManager entityManager;

    public ItemService(
            ItemRepository itemRepository,
            CategoryService categoryService,
            EntityManager entityManager) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public CategoryService.ListResult<Item> list(
            int skip,
            int limit,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Long categoryId,
            String nameContains) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Item> countRoot = countQuery.from(Item.class);
        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates =
                buildPredicates(cb, countRoot, minPrice, maxPrice, categoryId, nameContains);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }
        long total = entityManager.createQuery(countQuery).getSingleResult();

        CriteriaQuery<Item> dataQuery = cb.createQuery(Item.class);
        Root<Item> dataRoot = dataQuery.from(Item.class);
        dataQuery.select(dataRoot);
        List<Predicate> dataPredicates =
                buildPredicates(cb, dataRoot, minPrice, maxPrice, categoryId, nameContains);
        if (!dataPredicates.isEmpty()) {
            dataQuery.where(dataPredicates.toArray(Predicate[]::new));
        }
        dataQuery.orderBy(cb.asc(dataRoot.get("id")));

        TypedQuery<Item> typedQuery = entityManager.createQuery(dataQuery);
        typedQuery.setFirstResult(skip);
        typedQuery.setMaxResults(limit);
        List<Item> items =
                typedQuery.getResultList().stream()
                        .map(item -> itemRepository.findByIdWithCategory(item.getId()).orElse(item))
                        .toList();
        return new CategoryService.ListResult<>(items, total);
    }

    @Transactional(readOnly = true)
    public Item getById(long itemId) {
        Item item =
                itemRepository
                        .findByIdWithCategory(itemId)
                        .orElseThrow(() -> DomainExceptions.itemNotFound(itemId));
        if (item.getCategory() == null && item.getCategoryId() != null) {
            item.setCategory(categoryService.getById(item.getCategoryId()));
        }
        return item;
    }

    @Transactional
    public Item create(ItemCreateRequest request) {
        validateCategoryId(request.categoryId());
        Item item = new Item();
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setCategoryId(request.categoryId());
        Item saved = itemRepository.save(item);
        return getById(saved.getId());
    }

    @Transactional
    public Item update(long itemId, ItemUpdateRequest request) {
        Item item = getById(itemId);
        if (request.name() != null) {
            item.setName(request.name());
        }
        if (request.description() != null) {
            item.setDescription(request.description());
        }
        if (request.price() != null) {
            item.setPrice(request.price());
        }
        if (request.categoryId() != null) {
            validateCategoryId(request.categoryId());
            item.setCategoryId(request.categoryId());
        }
        itemRepository.save(item);
        return getById(itemId);
    }

    @Transactional
    public void delete(long itemId) {
        Item item = getById(itemId);
        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public ItemStatsResponse getStats() {
        long count = itemRepository.count();
        if (count == 0) {
            return new ItemStatsResponse(0, BigDecimal.ZERO.setScale(2), null, null, 0, List.of());
        }

        Object[] aggregates =
                entityManager
                        .createQuery(
                                "select avg(i.price), min(i.price), max(i.price) from Item i",
                                Object[].class)
                        .getSingleResult();
        BigDecimal average = ApiMapper.roundAverage(toBigDecimal(aggregates[0]));
        BigDecimal min = ApiMapper.scale(toBigDecimal(aggregates[1]));
        BigDecimal max = ApiMapper.scale(toBigDecimal(aggregates[2]));

        long uncategorized =
                entityManager
                        .createQuery(
                                "select count(i) from Item i where i.categoryId is null", Long.class)
                        .getSingleResult();

        List<Object[]> rows =
                entityManager
                        .createQuery(
                                """
                                select c.id, c.name, count(i.id), avg(i.price)
                                from Category c join Item i on i.categoryId = c.id
                                group by c.id, c.name
                                order by c.name
                                """,
                                Object[].class)
                        .getResultList();

        List<CategoryItemStats> byCategory = new ArrayList<>();
        for (Object[] row : rows) {
            byCategory.add(
                    new CategoryItemStats(
                            (Long) row[0],
                            (String) row[1],
                            (Long) row[2],
                            ApiMapper.roundAverage(toBigDecimal(row[3]))));
        }

        return new ItemStatsResponse(count, average, min, max, uncategorized, byCategory);
    }

    @Transactional(readOnly = true)
    public void checkDatabase() {
        entityManager.createNativeQuery("select 1").getSingleResult();
    }

    private void validateCategoryId(Long categoryId) {
        if (categoryId != null) {
            categoryService.getById(categoryId);
        }
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<Item> root,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Long categoryId,
            String nameContains) {
        List<Predicate> predicates = new ArrayList<>();
        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        if (categoryId != null) {
            predicates.add(cb.equal(root.get("categoryId"), categoryId));
        }
        if (nameContains != null && !nameContains.isBlank()) {
            predicates.add(
                    cb.like(cb.lower(root.get("name")), "%" + nameContains.toLowerCase() + "%"));
        }
        return predicates;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }
}
