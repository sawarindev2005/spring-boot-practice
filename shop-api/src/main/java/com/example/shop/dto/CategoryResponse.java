package com.example.shop.dto;

/**
 * DTO ที่ "ส่งกลับไปให้ client" → เลือกได้ว่าจะโชว์ฟิลด์ไหนบ้าง
 */
public record CategoryResponse(
        Long id,
        String name,
        String description,
        int productCount
) {
}
