package com.example.shop.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * จับ exception จากทุก Controller มาไว้ที่เดียว แล้วแปลงเป็น HTTP status + JSON ที่ถูกต้อง
 * → Controller / Service ไม่ต้องเขียน try-catch เลย แค่ throw ออกมา
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // หาข้อมูลไม่เจอ → 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), req, null);
    }

    // ข้อมูลขัดแย้ง (ชื่อซ้ำ, ลบไม่ได้) → 409
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException e, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, e.getMessage(), req, null);
    }

    // body ไม่ผ่าน @Valid → 400 พร้อมบอกว่าฟิลด์ไหนผิด
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError err : e.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(err.getField(), err.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "ข้อมูลไม่ถูกต้อง", req, fieldErrors);
    }

    // JSON พัง หรือชนิดข้อมูลผิด เช่น "price": "abc" → 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "รูปแบบ JSON ไม่ถูกต้อง", req, null);
    }

    // path variable / query param ชนิดผิด เช่น /api/products/abc → 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "ค่าของ '" + e.getName() + "' ไม่ถูกต้อง", req, null);
    }

    // คำขอไม่ถูกต้องตามกฎของ Service (เช่น sort ด้วยฟิลด์ที่ไม่อนุญาต) → 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), req, null);
    }

    // อะไรที่ไม่ได้คาดไว้ → 500 (log รายละเอียดไว้ฝั่ง server แต่ไม่ส่ง stack trace ให้ client เห็น)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOthers(Exception e, HttpServletRequest req) {
        // error มาตรฐานของ Spring เอง (เช่น URL ไม่มีอยู่ = 404, ใช้ method ผิด = 405)
        // มี status ติดมาอยู่แล้ว → ใช้ status นั้นแทนที่จะตอบ 500
        if (e instanceof org.springframework.web.ErrorResponse springError) {
            HttpStatus status = HttpStatus.valueOf(springError.getStatusCode().value());
            return build(status, status.getReasonPhrase(), req, null);
        }
        log.error("Unexpected error at {}", req.getRequestURI(), e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "เกิดข้อผิดพลาดภายในระบบ", req, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                HttpServletRequest req, Map<String, String> fieldErrors) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                Instant.now(),
                fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
