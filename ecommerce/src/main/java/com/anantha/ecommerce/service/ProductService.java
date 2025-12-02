package com.anantha.ecommerce.service;

import com.anantha.ecommerce.dto.ProductRequestDTO;
import com.anantha.ecommerce.dto.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponseDTO create(ProductRequestDTO dto);

    ProductResponseDTO update(Long id, ProductRequestDTO dto);

    void delete(Long id);

    ProductResponseDTO getById(Long id);

    /**
     * Search / filter products.
     * q: search term matched against name OR description
     * category: category filter
     * minPrice / maxPrice: price range filter (nullable)
     */
    Page<ProductResponseDTO> search(String q,
                                    String category,
                                    BigDecimal minPrice,
                                    BigDecimal maxPrice,
                                    Pageable pageable);
}
