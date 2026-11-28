package com.example.shop.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/**
 * ห่อผลลัพธ์แบบแบ่งหน้า (pagination) ให้ JSON อ่านง่ายและคงที่
 * (ไม่ส่ง Page ของ Spring ออกไปตรงๆ เพราะ JSON ของมันยาวและอาจเปลี่ยนตามเวอร์ชัน)
 *
 * @param <T> ชนิดของข้อมูลในแต่ละหน้า
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {

    // แปลง Page<Entity> → PageResponse<DTO> ในบรรทัดเดียว
    public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
