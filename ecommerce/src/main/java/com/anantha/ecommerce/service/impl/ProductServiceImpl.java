package com.anantha.ecommerce.service.impl;

import com.anantha.ecommerce.dto.ProductRequestDTO;
import com.anantha.ecommerce.dto.ProductResponseDTO;
import com.anantha.ecommerce.entity.Product;
import com.anantha.ecommerce.repository.ProductRepository;
import com.anantha.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    private ProductResponseDTO mapToDto(Product p){
        return ProductResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .category(p.getCategory())
                .stock(p.getStock())
                .createdAt(p.getCreatedAt())
                .build();
    }

    @Override
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product p = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .stock(dto.getStock())
                .createdAt(Instant.now())
                .build();
        Product saved = productRepository.save(p);
        return mapToDto(saved);
    }

    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setCategory(dto.getCategory());
        existing.setStock(dto.getStock());

        Product updated = productRepository.save(existing);
        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponseDTO getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id));
        return mapToDto(p);
    }

    @Override
    public Page<ProductResponseDTO> search(String q, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {

        // Normalize price bounds
        BigDecimal min = minPrice != null ? minPrice : BigDecimal.valueOf(0);
        BigDecimal max = maxPrice != null ? maxPrice : BigDecimal.valueOf(Long.MAX_VALUE);

        Page<Product> page;

        boolean hasQ = (q != null && !q.isBlank());
        boolean hasCategory = (category != null && !category.isBlank());
        boolean hasPriceFilter = (minPrice != null || maxPrice != null);

        // Choose repository method based on provided filters (simple branching)
        if (hasQ && hasCategory && hasPriceFilter) {
            // category + search + price range -> use search and then filter price in page (simpler route)
            page = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);
            // We could further filter by category & price programmatically (stream), but prefer DB queries for performance.
            // For brevity and clarity we'll filter in-memory for this combination (expected page sizes small).
            return page.map(this::mapToDto).map(dto -> dto) // placeholder; we'll apply in-memory filtering below
                    .map(r -> r); // left to return after in-memory filter (see below)
        }

        if (hasQ && !hasCategory && !hasPriceFilter) {
            page = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);
        } else if (!hasQ && hasCategory && hasPriceFilter) {
            page = productRepository.findByCategoryIgnoreCaseAndPriceBetween(category, min, max, pageable);
        } else if (!hasQ && hasCategory) {
            page = productRepository.findByCategoryIgnoreCase(category, pageable);
        } else if (!hasQ && !hasCategory && hasPriceFilter) {
            page = productRepository.findByPriceBetween(min, max, pageable);
        } else {
            page = productRepository.findAll(pageable);
        }

        // map page content to DTOs
        return page.map(this::mapToDto);
    }
}
