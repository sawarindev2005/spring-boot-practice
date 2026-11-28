package com.example.shop.exception;

/**
 * โยนเมื่อคำขอ "ขัดแย้ง" กับสถานะข้อมูลปัจจุบัน → HTTP 409 Conflict
 * เช่น ชื่อหมวดหมู่ซ้ำ หรือจะลบหมวดหมู่ที่ยังมีสินค้าอยู่
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
