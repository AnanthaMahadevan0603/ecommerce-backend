package com.anantha.ecommerce.mapper;

import com.anantha.ecommerce.dto.ProductRequestDTO;
import com.anantha.ecommerce.dto.ProductResponseDTO;
import com.anantha.ecommerce.entity.Product;

import java.time.Instant;

public class ProductMapper {

    public static Product toEntity(ProductRequestDTO dto) {
        if (dto == null) return null;

        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .stock(dto.getStock())
                .createdAt(Instant.now())
                .build();
    }

    public static void updateEntity(ProductRequestDTO dto, Product p) {
        if (dto == null || p == null) return;

        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setCategory(dto.getCategory());
        p.setStock(dto.getStock());
    }

    public static ProductResponseDTO toDTO(Product p) {
        if (p == null) return null;

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
}
