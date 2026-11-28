package com.example.shop.exception;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * รูปแบบ JSON ของ error "แบบเดียวกันทุกที่" → frontend เขียนโค้ดรับ error ได้ง่าย
 *
 * {
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "ไม่พบ สินค้า id 99",
 *   "path": "/api/products/99",
 *   "timestamp": "2026-11-24T13:00:00Z"
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // fieldErrors เป็น null → ไม่ต้องแสดง
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        Map<String, String> fieldErrors
) {
}
