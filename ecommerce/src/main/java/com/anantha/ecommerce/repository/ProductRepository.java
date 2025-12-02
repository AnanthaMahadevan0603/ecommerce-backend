package com.anantha.ecommerce.repository;

import com.anantha.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // search by name or description
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

    // filter by category
    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

    // price range
    Page<Product> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

    // category + price range
    Page<Product> findByCategoryIgnoreCaseAndPriceBetween(String category, BigDecimal min, BigDecimal max, Pageable pageable);
}
