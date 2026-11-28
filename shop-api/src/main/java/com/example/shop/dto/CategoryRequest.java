package com.example.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) = รูปแบบข้อมูลที่ "client ส่งเข้ามา"
 * ใช้ record (Java 16+) → ได้ constructor, getter, equals, toString ให้อัตโนมัติ
 *
 * ทำไมไม่รับ Entity ตรงๆ?
 *  - client จะส่ง id หรือฟิลด์ที่ไม่ควรแก้มาได้
 *  - เปลี่ยนโครงสร้างตาราง แล้ว API จะเปลี่ยนตามโดยไม่ตั้งใจ
 */
public record CategoryRequest(

        @NotBlank(message = "ชื่อหมวดหมู่ห้ามว่าง")
        @Size(max = 50, message = "ชื่อหมวดหมู่ต้องไม่เกิน 50 ตัวอักษร")
        String name,

        @Size(max = 255, message = "คำอธิบายต้องไม่เกิน 255 ตัวอักษร")
        String description
) {
}
