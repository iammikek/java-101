package com.example.java101.repository;

import com.example.java101.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    long countByCategoryId(Long categoryId);

    @Query("select i from Item i left join fetch i.category where i.id = :id")
    Optional<Item> findByIdWithCategory(Long id);
}
