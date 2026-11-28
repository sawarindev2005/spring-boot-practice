package com.example.shop.mapper;

import com.example.shop.dto.ProductRequest;
import com.example.shop.dto.ProductResponse;
import com.example.shop.entity.Category;
import com.example.shop.entity.Product;

public final class ProductMapper {

    private ProductMapper() {
    }

    // categoryId ใน request เป็นแค่ตัวเลข → Service ต้องไปหา Category จริงมาให้ก่อน
    public static Product toEntity(ProductRequest request, Category category) {
        return new Product(request.name().trim(), request.price(), request.stock(), category);
    }

    public static void updateEntity(Product product, ProductRequest request, Category category) {
        product.setName(request.name().trim());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);
    }

    public static ProductResponse toResponse(Product product) {
        Category category = product.getCategory();
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                category.getId(),
                category.getName());
    }
}
