package com.example.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.shop.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Derived query: SELECT * FROM products WHERE category_id = ?
    List<Product> findByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    /**
     * ค้นหาแบบมีเงื่อนไข "ไม่บังคับ" → ถ้า keyword หรือ categoryId เป็น null ก็ข้ามเงื่อนไขนั้นไป
     * เขียนเองด้วย JPQL (ภาษาคล้าย SQL แต่ใช้ชื่อ class/ฟิลด์ ไม่ใช่ชื่อตาราง)
     * รับ Pageable → Spring ใส่ LIMIT / OFFSET / ORDER BY ให้เอง
     */
    @Query("""
            SELECT p FROM Product p
            WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
            """)
    Page<Product> search(@Param("keyword") String keyword,
                         @Param("categoryId") Long categoryId,
                         Pageable pageable);
}
