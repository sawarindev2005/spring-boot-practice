package com.example.shop.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stock,
        Long categoryId,
        String categoryName
) {
}
