package com.example.shop.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.shop.dto.CategoryRequest;
import com.example.shop.dto.CategoryResponse;
import com.example.shop.dto.ProductResponse;
import com.example.shop.service.CategoryService;

import jakarta.validation.Valid;

/**
 * Controller = "ประตูหน้าบ้าน" รับ HTTP request แล้วส่งต่อให้ Service
 * กฎของ REST ที่ใช้ในไฟล์นี้:
 *   - URL เป็นคำนาม พหูพจน์ (/categories) ไม่ใช่คำกริยา (/getCategories)
 *   - ใช้ HTTP method บอกการกระทำ: GET อ่าน, POST สร้าง, PUT แก้, DELETE ลบ
 *   - ตอบ status code ให้ตรงความหมาย (200, 201, 204, 400, 404, 409)
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/categories → 200 OK
    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAll();
    }

    // GET /api/categories/1 → 200 OK หรือ 404
    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    // GET /api/categories/1/products → สินค้าทั้งหมดในหมวดนี้ (resource ซ้อน resource)
    @GetMapping("/{id}/products")
    public List<ProductResponse> getProducts(@PathVariable Long id) {
        return categoryService.getProducts(id);
    }

    // POST /api/categories → 201 Created + header Location: /api/categories/{idใหม่}
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    // PUT /api/categories/1 → 200 OK
    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    // DELETE /api/categories/1 → 204 No Content (ลบสำเร็จ ไม่มี body)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
