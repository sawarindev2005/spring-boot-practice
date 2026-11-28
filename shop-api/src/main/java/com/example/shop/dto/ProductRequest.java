package com.example.shop.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductRequest(

        @NotBlank(message = "ชื่อสินค้าห้ามว่าง")
        @Size(max = 100, message = "ชื่อสินค้าต้องไม่เกิน 100 ตัวอักษร")
        String name,

        @NotNull(message = "ต้องระบุราคา")
        @DecimalMin(value = "0.01", message = "ราคาต้องมากกว่า 0")
        @Digits(integer = 8, fraction = 2, message = "ราคาทศนิยมได้ไม่เกิน 2 ตำแหน่ง")
        BigDecimal price,

        @NotNull(message = "ต้องระบุจำนวนสต็อก")
        @PositiveOrZero(message = "สต็อกต้องไม่ติดลบ")
        Integer stock,

        // client ส่งแค่ id ของหมวดหมู่มา ไม่ต้องส่ง object ทั้งก้อน
        @NotNull(message = "ต้องระบุ categoryId")
        Long categoryId
) {
}
