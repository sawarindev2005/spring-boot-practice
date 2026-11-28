package com.example.shop.exception;

/**
 * โยนเมื่อคำขอจาก client ไม่ถูกต้อง (นอกเหนือจาก @Valid) → HTTP 400
 * เช่น ขอเรียงลำดับด้วยฟิลด์ที่ไม่อนุญาต
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
