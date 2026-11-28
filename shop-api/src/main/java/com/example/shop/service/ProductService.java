package com.example.shop.service;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.dto.PageResponse;
import com.example.shop.dto.ProductRequest;
import com.example.shop.dto.ProductResponse;
import com.example.shop.entity.Category;
import com.example.shop.entity.Product;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.ProductMapper;
import com.example.shop.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    // ฟิลด์ที่ client ขอเรียงลำดับได้ (whitelist) → กัน ?sort=อะไรก็ได้ ที่ทำให้ query พัง
    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "name", "price", "stock");

    public PageResponse<ProductResponse> search(String keyword, Long categoryId, Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("เรียงลำดับได้เฉพาะ " + SORTABLE_FIELDS
                        + " เท่านั้น (ได้รับ '" + order.getProperty() + "')");
            }
        }
        // keyword ว่าง "" ให้ถือว่าไม่ได้ค้นหา
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return PageResponse.from(
                productRepository.search(kw, categoryId, pageable),
                ProductMapper::toResponse);
    }

    public ProductResponse getById(Long id) {
        return ProductMapper.toResponse(findProduct(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = categoryService.findCategory(request.categoryId()); // ไม่มี → 404
        Product saved = productRepository.save(ProductMapper.toEntity(request, category));
        return ProductMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        Category category = categoryService.findCategory(request.categoryId());
        ProductMapper.updateEntity(product, request, category);
        return ProductMapper.toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("สินค้า", id));
    }
}
