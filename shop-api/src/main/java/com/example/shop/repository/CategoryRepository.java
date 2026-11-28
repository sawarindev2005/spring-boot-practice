package com.example.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shop.entity.Category;

/**
 * Repository = ชั้นที่คุยกับฐานข้อมูล
 * แค่ extends JpaRepository ก็ได้ findAll, findById, save, deleteById ... ฟรี
 *
 * "Derived query": ตั้งชื่อ method ตามกฎ Spring จะเขียน SQL ให้เอง
 *   existsByNameIgnoreCase(name) → SELECT COUNT(*) > 0 FROM categories WHERE LOWER(name) = LOWER(?)
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    // ใช้ตอนแก้ไข: มีชื่อซ้ำกับ "หมวดอื่น" ที่ไม่ใช่ตัวเองไหม
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
