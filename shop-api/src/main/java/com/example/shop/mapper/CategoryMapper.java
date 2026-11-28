package com.example.shop.mapper;

import com.example.shop.dto.CategoryRequest;
import com.example.shop.dto.CategoryResponse;
import com.example.shop.entity.Category;

/**
 * Mapper = ตัวแปลง Entity ⇄ DTO รวมไว้ที่เดียว
 * Controller และ Service จะได้ไม่ต้องมานั่ง new / set ทีละฟิลด์ซ้ำๆ
 */
public final class CategoryMapper {

    private CategoryMapper() {
        // class นี้มีแต่ static method ไม่ต้องสร้าง object
    }

    public static Category toEntity(CategoryRequest request) {
        return new Category(request.name().trim(), request.description());
    }

    public static void updateEntity(Category category, CategoryRequest request) {
        category.setName(request.name().trim());
        category.setDescription(request.description());
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getProducts().size());
    }
}
