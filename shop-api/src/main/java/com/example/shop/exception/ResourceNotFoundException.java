package com.example.shop.exception;

/**
 * โยนเมื่อหาข้อมูลไม่เจอ → GlobalExceptionHandler จะแปลงเป็น HTTP 404
 * ทำแบบทั่วไป (ไม่ผูกกับ Book/Product) จะได้ใช้ซ้ำได้ทุก resource
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super("ไม่พบ " + resourceName + " id " + id);
    }
}
