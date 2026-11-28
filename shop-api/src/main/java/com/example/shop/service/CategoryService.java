package com.example.shop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.dto.CategoryRequest;
import com.example.shop.dto.CategoryResponse;
import com.example.shop.dto.ProductResponse;
import com.example.shop.entity.Category;
import com.example.shop.exception.ConflictException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.CategoryMapper;
import com.example.shop.mapper.ProductMapper;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;

/**
 * Service = ที่อยู่ของ "กฎทางธุรกิจ" (business logic)
 *  - ห้ามชื่อหมวดหมู่ซ้ำ
 *  - ห้ามลบหมวดหมู่ที่ยังมีสินค้า
 * Controller ไม่ควรรู้เรื่องพวกนี้ มีหน้าที่แค่รับ/ส่ง HTTP
 *
 * @Transactional(readOnly = true) ที่ระดับ class = ทุก method เป็นแบบอ่านอย่างเดียวโดยค่าเริ่มต้น
 * method ไหนที่เขียนข้อมูล ค่อยใส่ @Transactional ทับอีกที
 */
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        return CategoryMapper.toResponse(findCategory(id));
    }

    public List<ProductResponse> getProducts(Long categoryId) {
        findCategory(categoryId); // ถ้าไม่มีหมวดนี้ → 404 (ดีกว่าตอบ [] เฉยๆ)
        return productRepository.findByCategoryId(categoryId).stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new ConflictException("มีหมวดหมู่ชื่อ '" + request.name() + "' อยู่แล้ว");
        }
        Category saved = categoryRepository.save(CategoryMapper.toEntity(request));
        return CategoryMapper.toResponse(saved);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findCategory(id);
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.name().trim(), id)) {
            throw new ConflictException("มีหมวดหมู่ชื่อ '" + request.name() + "' อยู่แล้ว");
        }
        CategoryMapper.updateEntity(category, request);
        // ไม่ต้องเรียก save() ก็ได้: อยู่ใน @Transactional แล้ว Hibernate จะเห็นว่า object ถูกแก้
        // และสั่ง UPDATE ให้เองตอนจบ method (เรียกว่า "dirty checking")
        return CategoryMapper.toResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findCategory(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new ConflictException("ลบไม่ได้: หมวดหมู่ '" + category.getName() + "' ยังมีสินค้าอยู่");
        }
        categoryRepository.delete(category);
    }

    // ใช้ร่วมกับ ProductService ด้วย (ตอนสร้างสินค้าต้องหาหมวดหมู่)
    Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("หมวดหมู่", id));
    }
}
